package com.testbank.money.controller;

import java.io.IOException;
import java.math.BigDecimal;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.testbank.money.BaseE2ETest;
import com.testbank.money.api.model.AccountRequest;
import com.testbank.money.api.model.AccountResponse;
import com.testbank.money.common.model.Currency;

import io.javalin.http.NotFoundResponse;

public class AccountE2ETest extends BaseE2ETest {

  @Before
  public void before() throws JsonProcessingException {}

  @Test
  public void mustCreateAccount() throws IOException {
    /*
     * When
     */
    final AccountResponse account = this.createAccount(new AccountRequest(Currency.EUR, null));

    /*
     * Then
     */
    Assert.assertFalse(account.getId().isEmpty());
  }

  @Test
  public void mustGetAccount() throws IOException {
    /*
     * Given
     */
    final AccountResponse accountCreated =
        this.createAccount(new AccountRequest(Currency.EUR, null));

    /*
     * When
     */
    final AccountResponse accountGot = this.getAccount(accountCreated.getId());

    /*
     * Then
     */
    Assert.assertFalse(accountGot.getId().isEmpty());
    Assert.assertEquals(accountCreated.getId(), accountGot.getId());
    Assert.assertEquals(Currency.EUR, accountGot.getCurrency());
    Assert.assertEquals(BigDecimal.ZERO, accountGot.getBalance());
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

  @Test(expected = NotFoundResponse.class)
  public void mustReturnNotFoundCode() throws IOException {
    /*
     * When
     */
    this.getAccount("nonexising-id");
  }
}
