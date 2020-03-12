package com.testbank.money.di;

import lombok.Getter;

@Getter
public class UnknownBeanException extends RuntimeException {

  private static final long serialVersionUID = -4442591238173029933L;

  private final String name;

  public UnknownBeanException(String name) {
    this(name, null);
  }

  public UnknownBeanException(String name, Throwable cause) {
    super(cause);
    this.name = name;
  }
}
