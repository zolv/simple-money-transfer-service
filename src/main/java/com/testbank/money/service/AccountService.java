package com.testbank.money.service;

import java.math.BigDecimal;
import java.util.Optional;

import com.testbank.money.common.model.Currency;
import com.testbank.money.di.Context;
import com.testbank.money.di.Service;
import com.testbank.money.domain.model.Account;
import com.testbank.money.persistence.AccountRepository;
import com.testbank.money.persistence.model.AccountEntity;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class AccountService implements Service {

  public AccountEntity create(Account account) {
    log.info("Creating account");

    final AccountRepository accountRepository = this.getAccountRepository();

    /*
     * Used by E2E testing.
     */
    final BigDecimal initialBalance =
        Context.get().getWithBalance().get() && (account.getBalance() != null)
            ? account.getBalance()
            : BigDecimal.ZERO;

    final AccountEntity newAccount =
        AccountEntity.builder()
            .currency(account.getCurrency() != null ? account.getCurrency() : Currency.EUR)
            .balance(initialBalance)
            .build();
    return accountRepository.save(newAccount);
  }

  public Optional<Account> findByIdentifier(String id) {
    final AccountRepository accountRepository = this.getAccountRepository();
    final Optional<AccountEntity> byId = accountRepository.findByIdentifier(id);
    return byId.map(this::toDomain);
  }

  private Account toDomain(AccountEntity entity) {
    return Account.builder()
        .currency(entity.getCurrency())
        .balance(entity.getBalance())
        .id(entity.getId())
        .build();
  }

  private AccountRepository getAccountRepository() {
    return Context.get().getInstance(AccountRepository.class.getSimpleName());
  }
}
