package com.testbank.money.service;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.Queue;
import java.util.Set;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedTransferQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import javax.validation.ConstraintViolation;
import javax.validation.Valid;
import javax.validation.ValidationException;
import javax.validation.Validator;

import com.testbank.money.common.exception.InsufficientFunds;
import com.testbank.money.common.exception.InvalidTransferException;
import com.testbank.money.common.exception.UnsupportedExchange;
import com.testbank.money.common.model.TransferStatus;
import com.testbank.money.di.Context;
import com.testbank.money.di.Service;
import com.testbank.money.domain.model.Transfer;
import com.testbank.money.persistence.AccountRepository;
import com.testbank.money.persistence.TransferRepository;
import com.testbank.money.persistence.model.AccountEntity;
import com.testbank.money.persistence.model.TransferEntity;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class TransferService implements Service {

  private int workerThreadsMaxCount = 10;

  private Queue<TransferEntity> transferQueue = new ConcurrentLinkedQueue<>();

  /** We set up worker limit, but we don't setup any queue limit. */
  private final ExecutorService executor =
      new ThreadPoolExecutor(
          0,
          this.workerThreadsMaxCount,
          60L,
          TimeUnit.SECONDS,
          new LinkedTransferQueue<Runnable>());

  public Transfer makeTransfer(@Valid final Transfer newTransfer) throws InvalidTransferException {
    this.validate(newTransfer);
    /*
     * Repository returns currently the same instance, but we cannot rely on it.
     */
    final TransferEntity persistedTransfer =
        this.getTransferRepository().save(this.toEntity(newTransfer));
    this.transferQueue.add(persistedTransfer);
    this.executor.execute(this::executeTransfer);

    return this.toDomain(persistedTransfer);
  }

  private void validate(@Valid final Transfer newTransfer) throws InvalidTransferException {
    final Validator validator = Context.get().getInstance(Validator.class.getSimpleName());
    final Set<ConstraintViolation<@Valid Transfer>> violations = validator.validate(newTransfer);
    if (!violations.isEmpty()) {
      throw new ValidationException();
    }
  }

  public Optional<Transfer> findById(final String id) {
    return this.getTransferRepository().findByIdentifier(id).map(this::toDomain);
  }

  private void executeTransfer() {
    final TransferEntity transfer = this.transferQueue.poll();
    try {

      final AccountRepository accountRepository = this.getAccountRepository();

      final AccountEntity fromAccount =
          accountRepository.getByIdentifier(transfer.getFromAccountId());
      final AccountEntity toAccount = accountRepository.getByIdentifier(transfer.getToAccountId());

      /**
       * Order account by unique identifier to prevent a deadlock. So even if there are 2 transfers
       * processed between 2 accounts in 2 directions, proper lock acquiring order will prevent a
       * deadlock.
       */
      final AccountEntity lockFirst;
      final AccountEntity lockSecond;

      if (fromAccount.getId().compareTo(toAccount.getId()) <= 0) {
        lockFirst = fromAccount;
        lockSecond = toAccount;
      } else {
        lockFirst = toAccount;
        lockSecond = fromAccount;
      }

      synchronized (lockFirst) {
        synchronized (lockSecond) {
          this.doTransfer(fromAccount, toAccount, transfer);
          transfer.setStatus(TransferStatus.PROCESSED);
        }
      }

    } catch (final Exception e) {
      log.error(e.getMessage(), e);
      transfer.setStatus(TransferStatus.ERROR);
    }
  }

  private void doTransfer(
      final AccountEntity fromAccount, final AccountEntity toAccount, final TransferEntity transfer)
      throws UnsupportedExchange, InsufficientFunds {
    final ExchangeRateService exchangeRateService = this.getExchangeRateService();

    /*
     * It is possible to send EUR currency from USD account to GBP account.
     */
    final BigDecimal fromAccountTransferAmount =
        exchangeRateService.exchangeTo(
            transfer.getCurrency(), transfer.getAmount(), fromAccount.getCurrency());

    if (fromAccount.getBalance().compareTo(fromAccountTransferAmount) >= 0) {
      final BigDecimal toAcountTransferAmount =
          exchangeRateService.exchangeTo(
              transfer.getCurrency(), transfer.getAmount(), toAccount.getCurrency());

      fromAccount.setBalance(fromAccount.getBalance().subtract(fromAccountTransferAmount));
      toAccount.setBalance(toAccount.getBalance().add(toAcountTransferAmount));

    } else {
      /*
       * Jacoco doesn't register this part.
       */
      throw new InsufficientFunds(fromAccount.getId(), fromAccountTransferAmount);
    }
  }

  private TransferEntity toEntity(final Transfer newTransfer) {
    return TransferEntity.builder()
        .id(newTransfer.getId())
        .fromAccountId(newTransfer.getFromAccountId())
        .toAccountId(newTransfer.getToAccountId())
        .amount(newTransfer.getAmount())
        .currency(newTransfer.getCurrency())
        .status(newTransfer.getStatus())
        .build();
  }

  private Transfer toDomain(final TransferEntity newTransfer) {
    return Transfer.builder()
        .id(newTransfer.getId())
        .fromAccountId(newTransfer.getFromAccountId())
        .toAccountId(newTransfer.getToAccountId())
        .amount(newTransfer.getAmount())
        .currency(newTransfer.getCurrency())
        .status(newTransfer.getStatus())
        .build();
  }

  private AccountRepository getAccountRepository() {
    return Context.get().getInstance(AccountRepository.class.getSimpleName());
  }

  private TransferRepository getTransferRepository() {
    return (TransferRepository) Context.get().getInstance(TransferRepository.class.getSimpleName());
  }

  private ExchangeRateService getExchangeRateService() {
    return (ExchangeRateService)
        Context.get().getInstance(ExchangeRateService.class.getSimpleName());
  }
}
