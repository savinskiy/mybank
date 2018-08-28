package rest.routers;

import static io.netty.handler.codec.http.HttpHeaderValues.APPLICATION_JSON;
import static io.vertx.core.http.HttpHeaders.CONTENT_TYPE;

import com.gatehill.vertxoas.RouterSpecGenerator;
import com.google.inject.Injector;
import core.configuration.DIContainer;
import core.configuration.DbModule;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.Context;
import io.vertx.core.Future;
import io.vertx.core.Vertx;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.handler.BodyHandler;
import io.vertx.ext.web.handler.ErrorHandler;

// TODO: 28.08.2018 make transactional annotations (later)
// TODO: 25.08.2018 handle 404 (later)
// TODO: 27.08.2018 add swagger (later)
public class ServerLauncher extends AbstractVerticle {

  private AccountRouter accountRouter;

  @Override
  public void init(Vertx vertx, Context context) {
    super.init(vertx, context);
    Injector injector = DIContainer.getInjector();
    accountRouter = injector.getInstance(AccountRouter.class);
  }

  @Override
  public void start(Future<Void> fut) {

    String property = DbModule.getProperties().getProperty("server.port");
    int SERVER_PORT = Integer.parseInt(property);

    vertx
        .createHttpServer()
        .requestHandler(createRouter()::accept)
        .listen(SERVER_PORT, result -> {
          if (result.succeeded()) {
            fut.complete();
          } else {
            fut.fail(result.cause());
          }
        });
  }

  private Router createRouter() {
    Router router = Router.router(vertx);
    router.route().failureHandler(ErrorHandler.create(true));

    router.mountSubRouter("/api", apiRouter());

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

    router.post("/accounts").handler(accountRouter::createAccount);
    router.get("/accounts").handler(accountRouter::getAllAccounts);
    router.get("/accounts/:accountId").handler(accountRouter::getAccountById);
    router.delete("/accounts/:accountId").handler(accountRouter::deleteAccount);

    // todo: fork and fix this so badly generated specification (later)
    RouterSpecGenerator.publishApiDocs(router, "/api/spec");
    return router;
  }
}