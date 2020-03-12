package com.testbank.money.controller;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.Optional;

import org.junit.Assert;
import org.junit.Test;

import com.testbank.money.BaseE2ETest;
import com.testbank.money.api.model.AccountRequest;
import com.testbank.money.api.model.AccountResponse;
import com.testbank.money.api.model.TransferRequest;
import com.testbank.money.api.model.TransferResponse;
import com.testbank.money.common.model.Currency;
import com.testbank.money.common.model.TransferStatus;

public class TransferE2ETest2 extends BaseE2ETest {

  @Test
  public void mustCreateSimpleTransfer() throws IOException {
    /*
     * Given
     */

    final AccountResponse from =
        this.createAccount(new AccountRequest(Currency.EUR, new BigDecimal(1000)));
    final AccountResponse to =
        this.createAccount(new AccountRequest(Currency.EUR, new BigDecimal(1000)));

    /*
     * When
     */
    final TransferResponse transferCreated =
        this.createTransfer(
            new TransferRequest(from.getId(), to.getId(), new BigDecimal(700), Currency.EUR));

    /*
     * Then
     */
    Assert.assertFalse(transferCreated.getId().isEmpty());
    Assert.assertEquals(TransferStatus.NEW, transferCreated.getStatus());
  }

  @Test
  public void mustProcessTransferInMax1second() throws InterruptedException, IOException {
    /*
     * Given
     */
    final AccountResponse from =
        this.createAccount(new AccountRequest(Currency.EUR, new BigDecimal(1000)));
    final AccountResponse to =
        this.createAccount(new AccountRequest(Currency.EUR, new BigDecimal(1000)));

    /*
     * When
     */
    final TransferResponse transferCreated =
        this.createTransfer(
            new TransferRequest(from.getId(), to.getId(), new BigDecimal(700), Currency.EUR));

    /*
     * Then
     */
    Optional<TransferResponse> transferGot = Optional.empty();
    int tries = 0;
    do {
      Thread.sleep(50);
      tries += 50;

      transferGot = Optional.of(this.getTransfer(transferCreated.getId()));

    } while ((tries < 1000)
        && transferGot.isPresent()
        && transferGot.get().getStatus().equals(TransferStatus.NEW));

    Assert.assertTrue(transferGot.isPresent());

    Assert.assertEquals(TransferStatus.PROCESSED, transferGot.get().getStatus());
  }

  @Test
  public void mustFailDueToInsufficientBalance() throws InterruptedException, IOException {
    /*
     * Given
     */
    final AccountResponse from =
        this.createAccount(new AccountRequest(Currency.EUR, new BigDecimal(1000)));
    final AccountResponse to =
        this.createAccount(new AccountRequest(Currency.EUR, new BigDecimal(1000)));

    /*
     * When
     */
    final TransferResponse transferCreated =
        this.createTransfer(
            new TransferRequest(from.getId(), to.getId(), new BigDecimal(2000), Currency.EUR));

    /*
     * Then
     */
    Optional<TransferResponse> transferGot = Optional.empty();
    int tries = 0;
    do {
      Thread.sleep(50);
      tries += 50;
      transferGot = Optional.of(this.getTransfer(transferCreated.getId()));
    } while ((tries < 1000)
        && transferGot.isPresent()
        && transferGot.get().getStatus().equals(TransferStatus.NEW));

    Assert.assertTrue(transferGot.isPresent());

    Assert.assertEquals(TransferStatus.ERROR, transferGot.get().getStatus());
  }

  @Test
  public void mustStoreAccountWithDefaults() throws IOException {
    /*
     * When
     */
    final AccountResponse accountCreated = this.createAccount(new AccountRequest(null, null));

    /*
     * Then
     */
    Assert.assertFalse(accountCreated.getId().isEmpty());
    Assert.assertEquals(accountCreated.getId(), accountCreated.getId());
    Assert.assertEquals(Currency.EUR, accountCreated.getCurrency());
    Assert.assertEquals(BigDecimal.ZERO, accountCreated.getBalance());
  }
}
