package com.testbank.money.common.exception;

import com.testbank.money.domain.model.Transfer;

import lombok.Getter;

@Getter
public class InvalidTransferAmountException extends InvalidTransferException {

  private static final long serialVersionUID = 2016636839623585137L;

  public InvalidTransferAmountException(Transfer transfer) {
    this(transfer, null);
  }

  public InvalidTransferAmountException(Transfer transfer, Throwable cause) {
    super(transfer, cause);
  }
}
