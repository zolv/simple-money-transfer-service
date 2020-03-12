package com.testbank.money.common.exception;

import com.testbank.money.common.model.Currency;

import lombok.Getter;

@Getter
public class UnsupportedExchange extends RuntimeException {

  private static final long serialVersionUID = 1898842147686749395L;

  private final Currency from;
  private final Currency to;

  public UnsupportedExchange(Currency from, Currency to) {
    this(from, to, null);
  }

  public UnsupportedExchange(Currency from, Currency to, Throwable cause) {
    super(cause);
    this.from = from;
    this.to = to;
  }
}
