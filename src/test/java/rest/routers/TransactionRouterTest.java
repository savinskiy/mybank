package rest.routers;

import core.configuration.DIContainer;
import core.configuration.DbModule;
import core.dao.AccountDao;
import core.dao.GenericDao;
import core.entities.Account;
import core.entities.Balance;
import core.entities.Balance.Currency;
import core.entities.Transaction;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonArray;
import io.vertx.ext.unit.Async;
import io.vertx.ext.unit.TestContext;
import io.vertx.ext.unit.junit.RepeatRule;
import io.vertx.ext.unit.junit.VertxUnitRunner;
import io.vertx.ext.web.client.WebClient;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.concurrent.CountDownLatch;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import rest.ServerLauncher;
import rest.to.AccountTo;
import rest.to.BalanceTo;
import rest.to.TransactionTo;

@RunWith(VertxUnitRunner.class)
public class TransactionRouterTest {

  private static final String ACC_START_AMOUNT = "12.3";

  private Vertx vertx;

  private AccountDao accountDao;
  private GenericDao<Transaction> genericDao;

  private List<Long> accountIds;

  @Rule
  public RepeatRule rule = new RepeatRule();

  @Before
  public void setUp(TestContext context) {
    DbModule.APPLICATION_PROPERTIES = "src/test/resources/application.properties";
    // need to initialize it before deploying verticle
    accountDao = DIContainer.getInjector().getInstance(AccountDao.class);
    genericDao = DIContainer.getInjector().getInstance(GenericDao.class);
    loadDatabase();
    vertx = Vertx.vertx();
    vertx.deployVerticle(ServerLauncher.class.getName(),
        context.asyncAssertSuccess());
  }

  @After
  public void tearDown(TestContext context) {
    vertx.close(context.asyncAssertSuccess());
  }

  // TODO: 29.08.2018 write tests for other API
  //  @Repeat(1000)
  @Test
//  @Ignore
  public void testIntensiveLoadWithTransactions(TestContext context) throws InterruptedException {

    int loops = 1000;
//    create huge countdown for asynchronous completion, finish last query by complete
    final Async async = context.async(loops * 3);
    // synchronization, wait till all transactions are completed
    CountDownLatch latch = new CountDownLatch(loops);

    WebClient webClient = WebClient.create(vertx);
    for (int i = 0; i < loops; i++) {

      TransactionTo transactionTo =
          buildRandomTransaction(BalanceTo.Currency.BTC, 0.01);

      webClient.post(8082, "localhost", "/api/transactions")
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

    webClient.get(8082, "localhost", "/api/accounts")
        .send(ar -> {
          if (ar.succeeded()) {
            JsonArray accountsArray = ar.result().bodyAsJsonArray();
            List<AccountTo> accounts = IntStream.range(0, accountIds.size())
                .mapToObj(accountsArray::getJsonObject)
                .map(json -> json.mapTo(AccountTo.class))
                .collect(Collectors.toList());

            BigDecimal totalAmount = accounts.stream()
                .map(AccountTo::getBalances)
                .flatMap(List::stream)
                .map(BalanceTo::getValue)
                .reduce(BigDecimal::add)
                .orElseThrow(IllegalStateException::new);

            BigDecimal actual = BigDecimal.valueOf(36.9).setScale(totalAmount.scale());
            context.assertEquals(totalAmount, actual);
            async.complete();
          } else {
            System.out.println(ar.cause());
          }
        });

  }

  private void loadDatabase() {

    Account account = buildAccount("alex", "details", Currency.BTC, ACC_START_AMOUNT);
    accountDao.save(account);
    Account account2 = buildAccount("max", null, Currency.BTC, ACC_START_AMOUNT);
    accountDao.save(account2);
    Account account3 = buildAccount("leila", null, Currency.BTC, ACC_START_AMOUNT);
    accountDao.save(account3);
    accountIds = Arrays.asList(account.getId(), account2.getId(), account3.getId());
  }

  private Account buildAccount(
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

  private Balance findBalanceByCurrency(Account account, Balance.Currency currency) {

    return account.getBalances().stream()
        .filter(balance -> balance.getCurrency().equals(currency))
        .findFirst()
        .orElseThrow(() -> new IllegalStateException(
            "Unknown type of currency: " + currency));
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
}