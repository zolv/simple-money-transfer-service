package com.testbank.money.api.controller;

import com.testbank.money.api.model.TransferRequest;
import com.testbank.money.api.model.TransferResponse;
import com.testbank.money.common.exception.InvalidTransferException;
import com.testbank.money.di.Context;
import com.testbank.money.di.Service;
import com.testbank.money.domain.model.Transfer;
import com.testbank.money.service.TransferService;

import io.javalin.http.BadRequestResponse;
import io.javalin.http.NotFoundResponse;

public class TransferController implements Service {

  public TransferResponse create(TransferRequest prototype) {
    final TransferService service =
        Context.get().getInstance(TransferService.class.getSimpleName());
    try {
      return this.toApiResponse(service.makeTransfer(this.toDomain(prototype)));
    } catch (final InvalidTransferException e) {
      throw new BadRequestResponse();
    }
  }

  public TransferResponse get(String id) {
    final TransferService service =
        Context.get().getInstance(TransferService.class.getSimpleName());
    return service
        .findById(id)
        .map(this::toApiResponse)
        .orElseThrow(() -> new NotFoundResponse("No transfer with given id"));
  }

  private TransferResponse toApiResponse(Transfer transfer) {
    return new TransferResponse(
        transfer.getId(),
        transfer.getFromAccountId(),
        transfer.getToAccountId(),
        transfer.getAmount(),
        transfer.getCurrency(),
        transfer.getStatus());
  }

  private Transfer toDomain(TransferRequest newTransfer) {
    return Transfer.builder()
        .fromAccountId(newTransfer.getFromAccountId())
        .toAccountId(newTransfer.getToAccountId())
        .amount(newTransfer.getAmount())
        .currency(newTransfer.getCurrency())
        .build();
  }
}
