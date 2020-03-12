package com.testbank.money.di;

import lombok.Getter;

@Getter
public class InstantiationException extends RuntimeException {

  private static final long serialVersionUID = -5386545327216899024L;

  private final String name;

  public InstantiationException(String name, Throwable cause) {
    super(cause);
    this.name = name;
  }
}
