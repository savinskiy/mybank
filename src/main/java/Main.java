import core.configuration.DIContainer;
import core.configuration.DatabaseLauncher;
import io.vertx.core.Future;
import io.vertx.core.Vertx;
import rest.ServerLauncher;

//// TODO: 23.08.2018 todo multimodule, probably get rid of guice dependency
//// TODO: 23.08.2018 todo logs
public class Main {

  public static void main(String[] args) {
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
