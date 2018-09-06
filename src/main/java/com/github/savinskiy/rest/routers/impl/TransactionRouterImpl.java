package com.github.savinskiy.rest.routers.impl;

import com.github.savinskiy.rest.routers.TransactionRouter;
import com.github.savinskiy.rest.to.BalanceTo;
import com.github.savinskiy.rest.to.TransactionTo;
import com.github.savinskiy.services.TransactionService;
import com.google.inject.Inject;
import io.vertx.core.json.Json;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.RoutingContext;
import java.util.List;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class TransactionRouterImpl implements TransactionRouter {

  private TransactionService transactionService;

  @Inject
  public TransactionRouterImpl(TransactionService transactionService) {
    this.transactionService = transactionService;
  }

  @Override
  public void createTransaction(RoutingContext routingContext) {
    log.info("Start creating a transaction");

    JsonObject body = routingContext.getBodyAsJson();
    TransactionTo transactionTo = body.mapTo(TransactionTo.class);
    TransactionTo resultTo = transactionService.addTransaction(transactionTo);
    routingContext.response()
        .setStatusCode(201)
        .end(Json.encodePrettily(resultTo));
    log.info("Finished creating a transaction");
  }

  @Override
  public void depositMoney(RoutingContext routingContext) {
    log.info("Start depositing money");

    JsonObject body = routingContext.getBodyAsJson();
    BalanceTo balanceTo = body.mapTo(BalanceTo.class);
    String accountId = routingContext.request().getParam("accountId");
    long id = Long.parseLong(accountId);
    TransactionTo resultTo = transactionService.depositMoney(id, balanceTo);
    routingContext.response()
        .setStatusCode(201)
        .end(Json.encodePrettily(resultTo));
    log.info("Finished depositing money");
  }

  @Override
  public void getAllTransactions(RoutingContext routingContext) {
    log.info("Start getting all transactions");

    List<TransactionTo> transactions = transactionService.getTransactions();
    routingContext.response()
        .end(Json.encodePrettily(transactions));
    log.info("Finish getting all transactions");
  }

  @Override
  public void getTransactionById(RoutingContext routingContext) {
    log.info("Start getting a transaction by id");

    String transactionId = routingContext.request().getParam("transactionId");
    long id = Long.parseLong(transactionId);
    TransactionTo transactionTo = transactionService.getTransactionById(id);
    routingContext.response()
        .end(Json.encodePrettily(transactionTo));
    log.info("Finish getting a transaction by id");
  }
}
