package rest.routers;

import com.google.inject.Inject;
import core.configuration.DIContainer;
import core.configuration.DbModule;
import core.entities.Account;
import io.vertx.core.Vertx;
import io.vertx.core.http.HttpClient;
import io.vertx.ext.unit.Async;
import io.vertx.ext.unit.TestContext;
import io.vertx.ext.unit.junit.VertxUnitRunner;
import javax.persistence.EntityManager;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(VertxUnitRunner.class)
public class TransferControllerTest {

  private Vertx vertx;

  private EntityManager entityManager;

  @Before
  public void setUp(TestContext context) {
    DbModule.APPLICATION_PROPERTIES = "src/test/resources/application.properties";
    // need to initialize it before deploying verticle
    entityManager = DIContainer.getInjector().getInstance(EntityManager.class);
    loadSql();
    vertx = Vertx.vertx();
    vertx.deployVerticle(ServerLauncher.class.getName(),
        context.asyncAssertSuccess());
  }

  @After
  public void tearDown(TestContext context) { vertx.close(context.asyncAssertSuccess());
  }

  @Test
  public void testMyApplication(TestContext context) {
    final Async async = context.async(1);

    HttpClient httpClient = vertx.createHttpClient();
    httpClient.get(80, "localhost", "/api/accounts",
        response -> {
          response.handler(body -> {
            context.assertTrue(body.toJsonArray().size() == 2);
            async.countDown();
          });
        });
  }

  private void loadSql() {

    entityManager.getTransaction().begin();
    Account account = buildAccount("alex", "details");
    entityManager.persist(account);
    Account account2 = buildAccount("max", null);
    entityManager.persist(account2);
    entityManager.getTransaction().commit();
  }

  private Account buildAccount(String name, String details) {

    return Account.builder()
        .name(name)
        .details(details)
        .build();
  }
}