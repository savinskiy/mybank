package com.github.savinskiy;

import com.github.savinskiy.core.configuration.DIContainer;
import com.github.savinskiy.core.configuration.DatabaseLauncher;
import com.github.savinskiy.rest.ServerLauncher;
import io.vertx.core.Future;
import io.vertx.core.Vertx;
import lombok.extern.slf4j.Slf4j;

//// TODO: 23.08.2018 todo multimodule, probably get rid of guice dependency
// TODO: 02.09.2018 injection of database initializer
// TODO: 02.09.2018 readme
@Slf4j
public class Main {

  public static void main(String[] args) {
    log.info("Start mybank application");
    DatabaseLauncher.run(5432, "pg-config");
    // need to initialize it before deploying verticle
    DIContainer.getInjector();
    Vertx vertx = Vertx.vertx();
    deploy(vertx, ServerLauncher.class);
  }


  private static Future<Void> deploy(Vertx vertx, Class verticle) {
    Future<Void> done = Future.future();

    vertx.deployVerticle(verticle.getName(), r -> {
      if (r.succeeded()) {
        log.info("Server is deployed");
      } else {
        log.error("Failed to deploy server: {}", r.cause());
      }
    });

    return done;
  }
}
