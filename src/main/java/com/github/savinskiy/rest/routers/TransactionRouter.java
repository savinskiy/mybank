package com.github.savinskiy.rest.routers;

import io.vertx.ext.web.RoutingContext;

public interface TransactionRouter {

  void createTransaction(RoutingContext routingContext);

  void depositMoney(RoutingContext routingContext);

  void getAllTransactions(RoutingContext routingContext);

  void getTransactionById(RoutingContext routingContext);
}
