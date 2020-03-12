package com.testbank.money;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpRequest.BodyPublishers;
import java.net.http.HttpResponse;
import java.util.Random;

import org.junit.AfterClass;
import org.junit.BeforeClass;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.testbank.money.api.model.AccountRequest;
import com.testbank.money.api.model.AccountResponse;
import com.testbank.money.api.model.TransferRequest;
import com.testbank.money.api.model.TransferResponse;

import io.javalin.http.BadRequestResponse;
import io.javalin.http.NotFoundResponse;

public class BaseE2ETest {

  protected static final Random rand = new Random();

  protected static final int port = 8081;
  protected static final String ROOT_URL = "http://localhost:" + port;

  private final HttpClient httpClient =
      HttpClient.newBuilder().version(HttpClient.Version.HTTP_2).build();

  private ObjectMapper jsonMapper = new ObjectMapper();

  @BeforeClass
  public static void startApp() throws Exception {
    MoneyTransferApp.start(port, true);
  }

  @AfterClass
  public static void stopApp() throws Exception {
    MoneyTransferApp.stop();
  }

  protected AccountResponse createAccount(AccountRequest prototype) throws IOException {
    final String accountAsString = this.jsonMapper.writeValueAsString(prototype);
    final HttpRequest request =
        HttpRequest.newBuilder()
            .POST(BodyPublishers.ofString(accountAsString))
            .uri(URI.create(ROOT_URL + "/accounts"))
            .header("Content-Type", "application/json")
            .build();

    return this.jsonMapper.readValue(this.callFor2xxOrException(request), AccountResponse.class);
  }

  protected AccountResponse getAccount(String identifier)
      throws JsonParseException, JsonMappingException, IOException {
    final HttpRequest request =
        HttpRequest.newBuilder()
            .GET()
            .uri(URI.create(ROOT_URL + "/accounts/" + identifier))
            .header("Content-Type", "application/json")
            .build();

    return this.jsonMapper.readValue(this.callFor2xxOrException(request), AccountResponse.class);
  }

  protected AccountResponse sendResponseAccount(HttpRequest request) {
    try {
      return this.jsonMapper.readValue(
          this.httpClient.send(request, HttpResponse.BodyHandlers.ofString()).body(),
          AccountResponse.class);
    } catch (IOException | InterruptedException e) {
      throw new RuntimeException(e);
    }
  }

  protected TransferResponse createTransfer(TransferRequest prototype) throws IOException {
    final HttpRequest request =
        HttpRequest.newBuilder()
            .POST(BodyPublishers.ofString(this.jsonMapper.writeValueAsString(prototype)))
            .uri(URI.create(ROOT_URL + "/transfers"))
            .header("Content-Type", "application/json")
            .build();
    return this.jsonMapper.readValue(this.callFor2xxOrException(request), TransferResponse.class);
  }

  protected TransferResponse getTransfer(String identifier)
      throws JsonParseException, JsonMappingException, IOException {
    final HttpRequest request =
        HttpRequest.newBuilder()
            .GET()
            .uri(URI.create(ROOT_URL + "/transfers/" + identifier))
            .header("Content-Type", "application/json")
            .build();

    return this.jsonMapper.readValue(this.callFor2xxOrException(request), TransferResponse.class);
  }

  protected String callFor2xxOrException(HttpRequest request) {
    try {
      final HttpResponse<String> response =
          this.httpClient.send(request, HttpResponse.BodyHandlers.ofString());
      switch (response.statusCode()) {
        case 200:
          return response.body();
        case 400:
          throw new BadRequestResponse();
        case 404:
          throw new NotFoundResponse();
        default:
          throw new UnknownError();
      }

    } catch (IOException | InterruptedException e) {
      throw new RuntimeException(e);
    }
  }
}
