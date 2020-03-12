package com.testbank.money.controller;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import com.anarsoft.vmlens.concurrent.junit.ConcurrentTestRunner;
import com.anarsoft.vmlens.concurrent.junit.ThreadCount;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.testbank.money.BaseE2ETest;
import com.testbank.money.api.model.AccountRequest;
import com.testbank.money.api.model.AccountResponse;
import com.testbank.money.api.model.TransferRequest;
import com.testbank.money.api.model.TransferResponse;
import com.testbank.money.common.model.Currency;

import io.javalin.http.BadRequestResponse;

@RunWith(ConcurrentTestRunner.class)
public class ChecksumBalanceStressTest extends BaseE2ETest {

  private static final int TRANSFERS_PER_ONE_THREAD = 100;

  private static final int THREAD_COUNT = 10;
  private static final int ACCOUNT_COUNT = THREAD_COUNT;

  private static final BigDecimal STARTING_BALANCE = new BigDecimal("1000000");

  private static final BigDecimal EXPECTED_BALANCE_SUM =
      STARTING_BALANCE.multiply(new BigDecimal(ACCOUNT_COUNT));

  private List<AccountResponse> accounts;

  @Before
  public void before() throws JsonProcessingException {
    this.accounts =
        IntStream.rangeClosed(1, ACCOUNT_COUNT)
            .mapToObj(
                i -> {
                  try {
                    return this.createAccount(new AccountRequest(Currency.EUR, STARTING_BALANCE));
                  } catch (final IOException e) {
                    throw new RuntimeException("Problem creating account");
                  }
                })
            .collect(Collectors.toList());
  }

  @Test
  @ThreadCount(THREAD_COUNT)
  public void makeManyRandomTransfers() {
    IntStream.range(0, TRANSFERS_PER_ONE_THREAD)
        .forEach(
            i -> {
              try {

                final TransferRequest newTransfer =
                    new TransferRequest(
                        this.accounts.get(rand.nextInt(this.accounts.size())).getId(),
                        this.accounts.get(rand.nextInt(this.accounts.size())).getId(),
                        new BigDecimal(1 + rand.nextInt(1000)),
                        Currency.EUR);
                final TransferResponse transfer = this.createTransfer(newTransfer);

                Assert.assertTrue(transfer.getId() != null);
                Assert.assertFalse(transfer.getId().isEmpty());

              } catch (final JsonProcessingException e) {
                throw new RuntimeException(e);
              } catch (final BadRequestResponse e) {
                /*
                 * Do nothing even if it is potential bad request.
                 * Balance sum check should be wrong in this case.
                 */
              } catch (final IOException e) {
                throw new UnknownError();
              }
            });
  }

  @After
  public void assertBalance() {

    final BigDecimal sum =
        this.accounts
            .stream()
            .map(
                account -> {
                  try {
                    return this.getAccount(account.getId());
                  } catch (final IOException e) {
                    throw new UnknownError();
                  }
                })
            .map(accountResponse -> accountResponse.getBalance())
            .reduce(BigDecimal.ZERO, (a, b) -> a.add(b), (a, b) -> a.add(b));

    Assert.assertEquals(EXPECTED_BALANCE_SUM, sum);
  }
}
