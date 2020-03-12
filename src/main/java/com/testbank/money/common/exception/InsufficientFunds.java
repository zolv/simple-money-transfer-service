package com.testbank.money.common.exception;

import java.math.BigDecimal;

import lombok.Getter;

@Getter
public class InsufficientFunds extends Exception {

  private static final long serialVersionUID = -5604167009583291040L;

  private final String accountId;
  private final BigDecimal amount;

  public InsufficientFunds(String accountId, BigDecimal amount) {
    this(accountId, amount, null);
  }

  public InsufficientFunds(String accountId, BigDecimal amount, Throwable cause) {
    super(cause);
    this.accountId = accountId;
    this.amount = amount;
  }
}
