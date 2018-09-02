package com.github.savinskiy;

import com.github.savinskiy.core.configuration.DIContainer;
import com.github.savinskiy.core.configuration.DatabaseLauncher;
import io.vertx.core.Future;
import io.vertx.core.Vertx;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.github.savinskiy.rest.ServerLauncher;

//// TODO: 23.08.2018 todo multimodule, probably get rid of guice dependency
//// TODO: 23.08.2018 todo logs
//@Slf4j
public class Main {

  private static Logger log = LoggerFactory.getLogger(Main.class);

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
        System.out.println("Server is deployed");
      } else {
        System.out.println(r.cause());
      }
    });

    return done;
  }
}
