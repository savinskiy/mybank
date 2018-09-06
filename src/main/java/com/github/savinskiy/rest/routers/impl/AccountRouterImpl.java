package com.github.savinskiy.rest.routers.impl;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import io.vertx.core.json.Json;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.RoutingContext;
import java.util.List;
import com.github.savinskiy.rest.routers.AccountRouter;
import com.github.savinskiy.rest.to.AccountTo;
import com.github.savinskiy.services.AccountService;
import lombok.extern.slf4j.Slf4j;

@Singleton
@Slf4j
public class AccountRouterImpl implements AccountRouter {

  private AccountService accountService;

  @Inject
  public AccountRouterImpl(AccountService accountService) {
    this.accountService = accountService;
  }

  @Override
  public void createAccount(RoutingContext routingContext) {
    log.info("Start creating an account");

    JsonObject body = routingContext.getBodyAsJson();
    AccountTo accountTo = body.mapTo(AccountTo.class);
    AccountTo resultTo = accountService.addAccount(accountTo);
    routingContext.response()
        .setStatusCode(201)
        .end(Json.encodePrettily(resultTo));
    log.info("Finish creating an account");
  }

  @Override
  public void getAllAccounts(RoutingContext routingContext) {
    log.info("Start getting all accounts");

    List<AccountTo> accounts = accountService.getAccounts();
    routingContext.response()
        .end(Json.encodePrettily(accounts));
    log.info("Finish getting all accounts");
  }

  @Override
  public void getAccountById(RoutingContext routingContext) {
    log.info("Start getting account by id");

    String accountId = routingContext.request().getParam("accountId");
    long id = Long.parseLong(accountId);
    AccountTo accountTo = accountService.getAccountById(id);
    routingContext.response()
        .end(Json.encodePrettily(accountTo));
    log.info("Finish getting account by id");
  }

  @Override
  public void deleteAccount(RoutingContext routingContext) {
    log.info("Start deleting account by id");

    String accountId = routingContext.request().getParam("accountId");
    long id = Long.parseLong(accountId);
    accountService.deleteAccountById(id);
    routingContext.response().setStatusCode(204).end();
    log.info("Finish deleting account by id");
  }
}
