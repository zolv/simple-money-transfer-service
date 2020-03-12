package com.testbank.money.controller;

import java.io.IOException;
import java.math.BigDecimal;
import java.net.http.HttpClient;
import java.util.stream.IntStream;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import com.anarsoft.vmlens.concurrent.junit.ConcurrentTestRunner;
import com.anarsoft.vmlens.concurrent.junit.ThreadCount;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.testbank.money.BaseE2ETest;
import com.testbank.money.api.model.AccountRequest;
import com.testbank.money.api.model.AccountResponse;
import com.testbank.money.api.model.TransferRequest;
import com.testbank.money.api.model.TransferResponse;
import com.testbank.money.common.model.Currency;

@RunWith(ConcurrentTestRunner.class)
public class BidirectionalTransfersStressTest extends BaseE2ETest {

  private static final int TRANSFERS_IN_ONE_DIRECTION = 100;
  private static final int TRANSFERS_PER_ONE_THREAD = TRANSFERS_IN_ONE_DIRECTION * 2;

  private static final int THREAD_COUNT = 10;
  private static final BigDecimal STARTING_BALANCE = new BigDecimal("1000000");

  private static final BigDecimal ACC1_TO_ACC2_SINGLE_TRANSFER_AMOUNT = new BigDecimal("100");
  private static final BigDecimal ACC2_TO_ACC1_SINGLE_TRANSFER_AMOUNT = new BigDecimal("50");

  private static final BigDecimal ACC1_TO_ACC2_TOTAL_TRANSFERED_AMOUNT =
      new BigDecimal(TRANSFERS_IN_ONE_DIRECTION)
          .multiply(new BigDecimal(THREAD_COUNT))
          .multiply(ACC1_TO_ACC2_SINGLE_TRANSFER_AMOUNT);

  private static final BigDecimal ACC2_TO_ACC1_TOTAL_TRANSFERED_AMOUNT =
      new BigDecimal(TRANSFERS_IN_ONE_DIRECTION)
          .multiply(new BigDecimal(THREAD_COUNT))
          .multiply(ACC2_TO_ACC1_SINGLE_TRANSFER_AMOUNT);

  private static final BigDecimal ACCOUNT1_EXPECTED_BALANCE =
      STARTING_BALANCE
          .subtract(ACC1_TO_ACC2_TOTAL_TRANSFERED_AMOUNT)
          .add(ACC2_TO_ACC1_TOTAL_TRANSFERED_AMOUNT);

  private static final BigDecimal ACCOUNT2_EXPECTED_BALANCE =
      STARTING_BALANCE
          .subtract(ACC2_TO_ACC1_TOTAL_TRANSFERED_AMOUNT)
          .add(ACC1_TO_ACC2_TOTAL_TRANSFERED_AMOUNT);

  private final HttpClient httpClient =
      HttpClient.newBuilder().version(HttpClient.Version.HTTP_2).build();

  private ObjectMapper jsonMapper = new ObjectMapper();

  private AccountResponse account1;
  private AccountResponse account2;

  @Before
  public void before() throws IOException {
    this.account1 = this.createAccount(new AccountRequest(Currency.EUR, STARTING_BALANCE));
    this.account2 = this.createAccount(new AccountRequest(Currency.EUR, STARTING_BALANCE));
  }

  @Test
  @ThreadCount(THREAD_COUNT)
  public void makeManyBidirectionalTransfers() {
    IntStream.range(0, TRANSFERS_PER_ONE_THREAD)
        .forEach(
            i -> {
              try {
                /*
                 * Deadlock can appear mostly when there are 2 transfers between 2 accounts in both directions.
                 * So we are trying to exploit it.
                 */
                final AccountResponse acc1;
                final AccountResponse acc2;
                final BigDecimal amount;
                if ((i % 2) == 0) {
                  acc1 = this.account1;
                  acc2 = this.account2;
                  amount = ACC1_TO_ACC2_SINGLE_TRANSFER_AMOUNT;
                } else {
                  acc1 = this.account2;
                  acc2 = this.account1;
                  amount = ACC2_TO_ACC1_SINGLE_TRANSFER_AMOUNT;
                }

                final TransferRequest newTransfer =
                    new TransferRequest(acc1.getId(), acc2.getId(), amount, Currency.EUR);
                final TransferResponse transfer = this.createTransfer(newTransfer);

                Assert.assertTrue(transfer.getId() != null);
                Assert.assertFalse(transfer.getId().isEmpty());

              } catch (final IOException e) {
                throw new RuntimeException(e);
              }
            });
  }

  @After
  public void assertBalance() throws JsonParseException, JsonMappingException, IOException {
    final AccountResponse account1State = this.getAccount(this.account1.getId());
    final AccountResponse account2State = this.getAccount(this.account2.getId());

    Assert.assertEquals(ACCOUNT1_EXPECTED_BALANCE, account1State.getBalance());

    Assert.assertEquals(ACCOUNT2_EXPECTED_BALANCE, account2State.getBalance());
  }
}
