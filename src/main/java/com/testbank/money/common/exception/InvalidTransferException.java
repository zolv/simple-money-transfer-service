package com.testbank.money.common.exception;

import com.testbank.money.domain.model.Transfer;

import lombok.Getter;

@Getter
public class InvalidTransferException extends Exception {

  private static final long serialVersionUID = 2016636839623585137L;

  private final Transfer transfer;

  public InvalidTransferException(Transfer transfer) {
    this(transfer, null);
  }

  public InvalidTransferException(Transfer transfer, Throwable cause) {
    super(cause);
    this.transfer = transfer;
  }
}
