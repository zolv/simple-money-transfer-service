package com.testbank.money.api.controller;

import com.testbank.money.api.model.AccountRequest;
import com.testbank.money.api.model.AccountResponse;
import com.testbank.money.di.Context;
import com.testbank.money.di.Service;
import com.testbank.money.domain.model.Account;
import com.testbank.money.persistence.model.AccountEntity;
import com.testbank.money.service.AccountService;

import io.javalin.http.NotFoundResponse;

public class AccountController implements Service {

  public AccountEntity create(AccountRequest prototype) {
    final AccountService service = Context.get().getInstance(AccountService.class.getSimpleName());
    return service.create(toDomain(prototype));
  }

  public AccountResponse get(String identifier) {
    final AccountService service = Context.get().getInstance(AccountService.class.getSimpleName());
    return service
        .findByIdentifier(identifier)
        .map(this::toApi)
        .orElseThrow(() -> new NotFoundResponse("Account not found"));
  }

  private Account toDomain(AccountRequest request) {
    return Account.builder().currency(request.getCurrency()).balance(request.getBalance()).build();
  }

  private AccountResponse toApi(Account entity) {
    return new AccountResponse(entity.getId(), entity.getCurrency(), entity.getBalance());
  }
}
