package com.github.savinskiy.rest.routers;

import io.vertx.ext.web.RoutingContext;

public interface AccountRouter {

  void createAccount(RoutingContext routingContext);

  void deleteAccount(RoutingContext routingContext);

  void getAccountById(RoutingContext routingContext);

  void getAllAccounts(RoutingContext routingContext);
}
