package com.github.savinskiy.rest.routers;

import com.github.savinskiy.core.configuration.DIContainer;
import com.github.savinskiy.core.configuration.DatabaseLauncher;
import com.github.savinskiy.core.configuration.DbModule;
import com.github.savinskiy.core.dao.AccountDao;
import com.github.savinskiy.core.dao.GenericDao;
import com.github.savinskiy.core.entities.Account;
import com.github.savinskiy.core.entities.Balance;
import com.github.savinskiy.core.entities.Balance.Currency;
import com.github.savinskiy.core.entities.Transaction;
import com.github.savinskiy.rest.ServerLauncher;
import com.github.savinskiy.rest.to.AccountTo;
import com.github.savinskiy.rest.to.BalanceTo;
import com.github.savinskiy.rest.to.TransactionTo;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonArray;
import io.vertx.ext.unit.Async;
import io.vertx.ext.unit.TestContext;
import io.vertx.ext.unit.junit.Timeout;
import io.vertx.ext.unit.junit.VertxUnitRunner;
import io.vertx.ext.web.client.WebClient;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Properties;
import java.util.Random;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Semaphore;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(VertxUnitRunner.class)
public class TransactionRouterTest {

  private static final String ACC_START_AMOUNT = "12.3";
  private static final String SERVER_HOST = "localhost";

  private static int serverPort;

  private static Vertx vertx;

  private static AccountDao accountDao;
  private static GenericDao<Transaction> genericDao;

  private static List<Long> accountIds = new ArrayList<>();

  @Rule
  public Timeout timeoutRule = Timeout.seconds(180);

  @BeforeClass
  public static void setUp(TestContext context) {
    DbModule.APPLICATION_PROPERTIES = "src/test/resources/application.properties";
    DatabaseLauncher.run(15432, "pg-config-test");
    // need to initialize it before deploying verticle
    accountDao = DIContainer.getInjector().getInstance(AccountDao.class);
    genericDao = DIContainer.getInjector().getInstance(GenericDao.class);
    loadDatabase();
    serverPort = getPortFromProperties();
    vertx = Vertx.vertx();
    vertx.deployVerticle(ServerLauncher.class.getName(),
        context.asyncAssertSuccess());
  }

  @AfterClass
  public static void tearDown(TestContext context) {
    DatabaseLauncher.stop();
    vertx.close(context.asyncAssertSuccess());
  }

  // TODO: 29.08.2018 write more tests for transactions
  // TODO: 29.08.2018 move out account test to other class
//    @Repeat(100)
  @Test
//  @Ignore
  public void testIntensiveLoadWithTransactions(TestContext context) throws InterruptedException {

    int totalRequests = 3000;
    // +2 methods that make asserts in the end
    final Async async = context.async(totalRequests + 2);
    // synchronization, wait till all transactions are completed
    CountDownLatch latch = new CountDownLatch(totalRequests);

    WebClient webClient = WebClient.create(vertx);
    for (int i = 0; i < totalRequests; i++) {

      TransactionTo transactionTo =
          buildRandomTransaction(BalanceTo.Currency.BTC, 0.01);

      webClient.post(serverPort, SERVER_HOST, "/api/transactions")
          .sendJson(transactionTo, ar -> {
            latch.countDown();
            if (ar.succeeded()) {
              async.countDown();
            } else {
              System.out.println(ar.cause());
            }
          });
    }

    latch.await();

    webClient.get(serverPort, SERVER_HOST, "/api/transactions")
        .send(ar -> {
          if (ar.succeeded()) {
            List<TransactionTo> transactions = ar.result().bodyAsJson(List.class);
            context.assertEquals(transactions.size(), totalRequests);
            async.countDown();
          } else {
            System.out.println(ar.cause());
          }
        });

    webClient.get(serverPort, SERVER_HOST, "/api/accounts")
        .send(ar -> {
          if (ar.succeeded()) {
            JsonArray accountsArray = ar.result().bodyAsJsonArray();
            List<AccountTo> accounts = IntStream.range(0, accountsArray.size())
                .mapToObj(accountsArray::getJsonObject)
                .filter(accountTo -> accountIds.contains(accountTo.getLong("id")))
                .map(json -> json.mapTo(AccountTo.class))
                .collect(Collectors.toList());

            BigDecimal totalAmount = accounts.stream()
                .map(AccountTo::getBalances)
                .flatMap(List::stream)
                .map(BalanceTo::getValue)
                .reduce(BigDecimal::add)
                .orElseThrow(IllegalStateException::new);

            BigDecimal expected = new BigDecimal(ACC_START_AMOUNT).setScale(totalAmount.scale())
                .multiply(BigDecimal.valueOf(accountIds.size()));
            context.assertEquals(expected, totalAmount);
            async.countDown();
          } else {
            System.out.println(ar.cause());
          }
        });

  }

  @Test
  public void testCreateAccount(TestContext context) throws InterruptedException {

    final Async async = context.async();

    WebClient webClient = WebClient.create(vertx);

    AccountTo accountTo = buildAccountTo();

    webClient.post(serverPort, SERVER_HOST, "/api/accounts")
        .sendJson(accountTo, ar -> {
          if (ar.succeeded()) {
            AccountTo result = ar.result().bodyAsJson(AccountTo.class);
            context.assertEquals(accountTo.getName(), result.getName());
            context.assertEquals(accountTo.getDetails(), result.getDetails());
            BigDecimal totalAmount = result.getBalances().stream()
                .map(BalanceTo::getValue)
                .reduce(BigDecimal::add)
                .orElseThrow(IllegalStateException::new);
            context.assertEquals(BigDecimal.ZERO, totalAmount);
            async.complete();
          } else {
            System.out.println(ar.cause());
          }
        });
  }

  @Test
  public void testGetAccount(TestContext context) throws InterruptedException {

    final Async async = context.async();

    WebClient webClient = WebClient.create(vertx);

    webClient.get(serverPort, SERVER_HOST, "/api/accounts/" + accountIds.get(0))
        .send(ar -> {
          if (ar.succeeded()) {
            AccountTo result = ar.result().bodyAsJson(AccountTo.class);
            context.assertEquals("alex", result.getName());
            context.assertEquals("details", result.getDetails());

            BigDecimal totalAmount = result.getBalances().stream()
                .map(BalanceTo::getValue)
                .reduce(BigDecimal::add)
                .orElseThrow(IllegalStateException::new);

            BigDecimal expected = new BigDecimal(ACC_START_AMOUNT).setScale(totalAmount.scale());

            context.assertEquals(expected, totalAmount);
            async.complete();
          } else {
            System.out.println(ar.cause());
          }
        });
  }

  @Test
  public void testGetAllAccounts(TestContext context) throws InterruptedException {

    final Async async = context.async();

    WebClient webClient = WebClient.create(vertx);

    webClient.get(serverPort, SERVER_HOST, "/api/accounts")
        .send(ar -> {
          if (ar.succeeded()) {
            JsonArray accountsArray = ar.result().bodyAsJsonArray();
            List<Long> accounts = IntStream.range(0, accountsArray.size())
                .mapToObj(accountsArray::getJsonObject)
                .map(accountTo -> accountTo.getLong("id"))
                .collect(Collectors.toList());
            context.assertTrue(accounts.containsAll(accountIds));

            async.complete();
          } else {
            System.out.println(ar.cause());
          }
        });
  }

  @Test
  public void testDeleteAccount(TestContext context) throws InterruptedException {

    final Async async = context.async(2);

    WebClient webClient = WebClient.create(vertx);

    Account account = buildAccount("doomed acc", "account for deletion",
        Currency.BTC, "1000000");
    accountDao.save(account);
    Semaphore semaphore = new Semaphore(1);
    webClient.delete(serverPort, SERVER_HOST, "/api/accounts/" + account.getId())
        .send(ar -> {
          semaphore.release();
          if (ar.succeeded()) {
            async.countDown();
          } else {
            System.out.println(ar.cause());
          }
        });

    semaphore.acquire();

    webClient.get(serverPort, SERVER_HOST, "/api/accounts/" + account.getId())
        .send(ar -> {
          if (ar.succeeded()) {
            // TODO: 03.09.2018 check status code must be 404
            System.out.println(ar.result().statusCode());
            context.assertInRange(400, ar.result().statusCode(), 199);
            async.complete();
          } else {
            throw new RuntimeException("Something went wrong during deletion: " + ar.cause());
          }
        });
  }


  private static void loadDatabase() {

    Account account = buildAccount("alex", "details", Currency.BTC, ACC_START_AMOUNT);
    accountDao.save(account);
    accountIds.add(account.getId());
    Account account2 = buildAccount("max", null, Currency.BTC, ACC_START_AMOUNT);
    accountDao.save(account2);
    accountIds.add(account2.getId());
    Account account3 = buildAccount("leila", null, Currency.BTC, ACC_START_AMOUNT);
    accountDao.save(account3);
    accountIds.add(account3.getId());
  }

  private static Account buildAccount(
      String name,
      String details,
      Balance.Currency currency,
      String amount) {

    Account account = new Account();
    account.setName(name);
    account.setDetails(details);
    Balance balance = findBalanceByCurrency(account, currency);
    balance.setValue(new BigDecimal(amount));
    return account;
  }

  private static Balance findBalanceByCurrency(Account account, Balance.Currency currency) {

    return account.getBalances().stream()
        .filter(balance -> balance.getCurrency().equals(currency))
        .findFirst()
        .orElseThrow(() -> new IllegalStateException(
            "Unknown type of currency: " + currency));
  }

  private AccountTo buildAccountTo() {

    AccountTo account = new AccountTo();
    account.setName("hacker");
    account.setDetails("get rich or die trying");
    account.setBalances(Collections.singletonList(
        BalanceTo.builder()
            .currency(BalanceTo.Currency.USD)
            .value(BigDecimal.valueOf(1_000_000))
            .build()
        )
    );
    return account;
  }

  private TransactionTo buildRandomTransaction(BalanceTo.Currency currency, double amountCap) {

    Random random = new Random();
    BigDecimal value = BigDecimal.valueOf(amountCap * random.nextDouble())
        .setScale(6, BigDecimal.ROUND_HALF_DOWN);
    List<Long> idsCopy = new ArrayList<>(accountIds);
    Long from = randomKick(idsCopy, random);
    Long to = randomKick(idsCopy, random);

    return TransactionTo.builder()
        .accountIdFrom(from)
        .accountIdTo(to)
        .amount(BalanceTo.builder().currency(currency).value(value).build())
        .build();
  }

  private Long randomKick(List<Long> ids, Random random) {

    int i = random.nextInt(ids.size());
    return ids.remove(i);
  }

  private static int getPortFromProperties() {
    Properties properties = DbModule.getProperties();
    String port = properties.getProperty("server.port");
    return Integer.parseInt(port);
  }
}