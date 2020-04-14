package com.testbank.money;

import java.util.Optional;

import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;

import com.testbank.money.api.controller.AccountController;
import com.testbank.money.api.controller.TransferController;
import com.testbank.money.api.model.AccountRequest;
import com.testbank.money.api.model.TransferRequest;
import com.testbank.money.common.exception.InvalidTransferException;
import com.testbank.money.common.exception.JsonMapperService;
import com.testbank.money.di.Context;
import com.testbank.money.persistence.AccountRepository;
import com.testbank.money.persistence.TransferRepository;
import com.testbank.money.service.AccountService;
import com.testbank.money.service.ExchangeRateService;
import com.testbank.money.service.TransferService;

import io.javalin.Javalin;
import io.javalin.http.NotFoundResponse;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class MoneyTransferApp {

  public static void main(final String[] args) {
    start(8080, (args.length > 0) && args[0].contentEquals("withBalance"));
  }

  public static void start(final int port, final boolean withBalance) {
    final Context context = Context.get();

    context.getWithBalance().set(withBalance);

    context.registerSingleton(JsonMapperService.class.getSimpleName(), new JsonMapperService());
    context.registerSingleton(AccountRepository.class.getSimpleName(), new AccountRepository());
    context.registerSingleton(TransferRepository.class.getSimpleName(), new TransferRepository());
    context.registerSingleton(AccountService.class.getSimpleName(), new AccountService());
    context.registerSingleton(ExchangeRateService.class.getSimpleName(), new ExchangeRateService());
    context.registerSingleton(TransferService.class.getSimpleName(), new TransferService());
    context.registerSingleton(AccountController.class.getSimpleName(), new AccountController());
    context.registerSingleton(TransferController.class.getSimpleName(), new TransferController());

    final ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
    context.registerSingleton(Validator.class.getSimpleName(), factory.getValidator());

    final JsonMapperService jsonMapper =
        Context.get().getInstance(JsonMapperService.class.getSimpleName());
    final AccountController accountController =
        Context.get().getInstance(AccountController.class.getSimpleName());
    final TransferController transferController =
        Context.get().getInstance(TransferController.class.getSimpleName());

    final Javalin server = Javalin.create();
    context.setServerInstance(server);

    server.start(port);
    server.get(
        "/",
        ctx -> {
          ctx.result("Hello Fellow! Your Simple Money Transfer Service is up and running");
        });

    server.post(
        "/accounts",
        ctx -> {
          log.info(ctx.body());
          ctx.result(
              jsonMapper.toJson(
                  accountController.create(jsonMapper.fromJson(ctx.body(), AccountRequest.class))));
        });
    server.get(
        "/accounts/:accountId",
        ctx -> {
          ctx.result(jsonMapper.toJson(accountController.get(ctx.pathParam("accountId"))));
        });

    server.post(
        "/transfers",
        ctx -> {
          ctx.result(
              jsonMapper.toJson(
                  transferController.create(
                      jsonMapper.fromJson(ctx.body(), TransferRequest.class))));
        });
    server.get(
        "/transfers/:transferId",
        ctx -> {
          ctx.result(jsonMapper.toJson(transferController.get(ctx.pathParam("transferId"))));
        });
    server.exception(
        InvalidTransferException.class,
        (e, ctx) -> {
          ctx.status(400);
        });
    server.exception(
        NotFoundResponse.class,
        (e, ctx) -> {
          ctx.status(404);
        });
  }

  public static void stop() {
    Optional.ofNullable(Context.get().getServerInstance()).ifPresent(server -> server.stop());
  }
}
