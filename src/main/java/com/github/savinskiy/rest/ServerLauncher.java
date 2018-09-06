package com.github.savinskiy.rest;

import static io.netty.handler.codec.http.HttpHeaderValues.APPLICATION_JSON;
import static io.vertx.core.http.HttpHeaders.CONTENT_TYPE;

import com.gatehill.vertxoas.RouterSpecGenerator;
import com.github.savinskiy.core.configuration.DIContainer;
import com.github.savinskiy.core.configuration.DbModule;
import com.github.savinskiy.rest.routers.AccountRouter;
import com.github.savinskiy.rest.routers.TransactionRouter;
import com.google.inject.Injector;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.Context;
import io.vertx.core.Future;
import io.vertx.core.Vertx;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.handler.BodyHandler;
import io.vertx.ext.web.handler.ErrorHandler;
import lombok.extern.slf4j.Slf4j;

// TODO: 28.08.2018 make transactional annotations (later)
// TODO: 25.08.2018 handle 404 (later)
// TODO: 27.08.2018 add swagger (later)
@Slf4j
public class ServerLauncher extends AbstractVerticle {

  private AccountRouter accountRouter;
  private TransactionRouter transactionRouter;

  @Override
  public void init(Vertx vertx, Context context) {
    log.info("Start deploying server");
    super.init(vertx, context);
    Injector injector = DIContainer.getInjector();
    accountRouter = injector.getInstance(AccountRouter.class);
    transactionRouter = injector.getInstance(TransactionRouter.class);
  }

  @Override
  public void start(Future<Void> fut) {

    String property = DbModule.getProperties().getProperty("server.port");
    int SERVER_PORT = Integer.parseInt(property);
    log.info("Host: localhost, port: {}", SERVER_PORT);

    vertx
        .createHttpServer()
        .requestHandler(createRouter()::accept)
        .listen(SERVER_PORT, result -> {
          if (result.succeeded()) {
            log.info("Server successfully deployed");
            fut.complete();
          } else {
            log.error("Failed to deploy server: {}", result.cause());
            fut.fail(result.cause());
          }
        });
  }

  private Router createRouter() {
    Router router = Router.router(vertx);
    router.route().failureHandler(ErrorHandler.create(true));
    log.debug("Created router");

    router.mountSubRouter("/api", apiRouter());
    log.debug("Mounted subrouter with API");

    return router;
  }

  private Router apiRouter() {

    Router router = Router.router(vertx);
    router.route().consumes(APPLICATION_JSON.toString());
    router.route().produces(APPLICATION_JSON.toString());
    router.route().handler(BodyHandler.create());
    router.route().handler(context -> {
      context.response().headers().add(CONTENT_TYPE, APPLICATION_JSON);
      context.next();
    });

    router.post("/accounts").blockingHandler(accountRouter::createAccount, false);
    router.get("/accounts").blockingHandler(accountRouter::getAllAccounts, false);
    router.get("/accounts/:accountId").blockingHandler(accountRouter::getAccountById, false);
    router.delete("/accounts/:accountId").blockingHandler(accountRouter::deleteAccount, false);
    log.debug("Account Router configured");

    router.post("/transactions")
        .blockingHandler(transactionRouter::createTransaction, false);
    router.post("/transactions/deposit/:accountId")
        .blockingHandler(transactionRouter::depositMoney, false);
    router.get("/transactions")
        .blockingHandler(transactionRouter::getAllTransactions, false);
    router.get("/transactions/:transactionId")
        .blockingHandler(transactionRouter::getTransactionById, false);
    log.debug("Transaction Router configured");

    // todo: fork and fix this so badly generated specification (later)
    String swaggerPath = "/api/spec";
    RouterSpecGenerator.publishApiDocs(router, swaggerPath);
    log.debug("Generated swagger spec by path '{}'", swaggerPath);
    return router;
  }
}