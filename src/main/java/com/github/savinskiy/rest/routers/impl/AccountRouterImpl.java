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

@Singleton
public class AccountRouterImpl implements AccountRouter {

  private AccountService accountService;

  @Inject
  public AccountRouterImpl(AccountService accountService) {
    this.accountService = accountService;
  }

  @Override
  public void createAccount(RoutingContext routingContext) {

    JsonObject body = routingContext.getBodyAsJson();
    AccountTo accountTo = body.mapTo(AccountTo.class);
    AccountTo resultTo = accountService.addAccount(accountTo);
    routingContext.response()
        .setStatusCode(201)
        .end(Json.encodePrettily(resultTo));
  }

  @Override
  public void getAllAccounts(RoutingContext routingContext) {

    List<AccountTo> accounts = accountService.getAccounts();
    routingContext.response()
        .end(Json.encodePrettily(accounts));
  }

  @Override
  public void getAccountById(RoutingContext routingContext) {

    String accountId = routingContext.request().getParam("accountId");
    long id = Long.parseLong(accountId);
    AccountTo accountTo = accountService.getAccountById(id);
    routingContext.response()
        .end(Json.encodePrettily(accountTo));
  }

  @Override
  public void deleteAccount(RoutingContext routingContext) {

    String accountId = routingContext.request().getParam("accountId");
    long id = Long.parseLong(accountId);
    accountService.deleteAccountById(id);
    routingContext.response().setStatusCode(204).end();
  }
}
