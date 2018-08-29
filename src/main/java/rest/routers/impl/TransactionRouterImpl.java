package rest.routers.impl;

import com.google.inject.Inject;
import io.vertx.core.json.Json;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.RoutingContext;
import java.util.List;
import rest.routers.TransactionRouter;
import rest.to.BalanceTo;
import rest.to.TransactionTo;
import services.TransactionService;

public class TransactionRouterImpl implements TransactionRouter {

  private TransactionService transactionService;

  @Inject
  public TransactionRouterImpl(TransactionService transactionService) {
    this.transactionService = transactionService;
  }

  @Override
  public void createTransaction(RoutingContext routingContext) {

    JsonObject body = routingContext.getBodyAsJson();
    TransactionTo transactionTo = body.mapTo(TransactionTo.class);
    TransactionTo resultTo = transactionService.addTransaction(transactionTo);
    routingContext.response()
        .setStatusCode(201)
        .end(Json.encodePrettily(resultTo));
  }

  @Override
  public void depositMoney(RoutingContext routingContext) {

    JsonObject body = routingContext.getBodyAsJson();
    BalanceTo balanceTo = body.mapTo(BalanceTo.class);
    String accountId = routingContext.request().getParam("accountId");
    long id = Long.parseLong(accountId);
    TransactionTo resultTo = transactionService.depositMoney(id, balanceTo);
    routingContext.response()
        .setStatusCode(201)
        .end(Json.encodePrettily(resultTo));
  }

  @Override
  public void getAllTransactions(RoutingContext routingContext) {

    List<TransactionTo> transactions = transactionService.getTransactions();
    routingContext.response()
        .end(Json.encodePrettily(transactions));
  }

  @Override
  public void getTransactionById(RoutingContext routingContext) {

    String transactionId = routingContext.request().getParam("transactionId");
    long id = Long.parseLong(transactionId);
    TransactionTo transactionTo = transactionService.getTransactionById(id);
    routingContext.response()
        .end(Json.encodePrettily(transactionTo));
  }
}
