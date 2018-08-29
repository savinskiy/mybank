package core.configuration;

import com.google.inject.AbstractModule;
import com.google.inject.TypeLiteral;
import converters.AccountConverter;
import converters.BalanceConverter;
import converters.TransactionConverter;
import core.dao.AccountDao;
import core.dao.GenericDao;
import core.dao.impl.AccountDaoTransactionedImpl;
import core.dao.impl.GenericDaoTransactionedImpl;
import core.entities.Account;
import rest.routers.AccountRouter;
import rest.routers.TransactionRouter;
import rest.routers.impl.AccountRouterImpl;
import rest.routers.impl.TransactionRouterImpl;
import services.AccountService;
import services.TransactionService;
import services.impl.AccountServiceImpl;
import services.impl.TransactionServiceImpl;

public class AppModule extends AbstractModule {

  @Override
  public void configure() {
    bind(AccountService.class).to(AccountServiceImpl.class).asEagerSingleton();
    bind(TransactionService.class).to(TransactionServiceImpl.class).asEagerSingleton();
    bind(GenericDao.class).to(GenericDaoTransactionedImpl.class).asEagerSingleton();
    bind(AccountDao.class).to(AccountDaoTransactionedImpl.class).asEagerSingleton();
//    bind(GenericDao.class).to(GenericDaoImpl.class).asEagerSingleton();
    bind(new TypeLiteral<GenericDao<Account>>() {
    })
        .to(new TypeLiteral<GenericDaoTransactionedImpl<Account>>() {
        }).asEagerSingleton();
    bind(AccountRouter.class).to(AccountRouterImpl.class).asEagerSingleton();
    bind(TransactionRouter.class).to(TransactionRouterImpl.class).asEagerSingleton();
    bind(BalanceConverter.class).toInstance(BalanceConverter.INSTANCE);
    bind(AccountConverter.class).toInstance(AccountConverter.INSTANCE);
    bind(TransactionConverter.class).toInstance(TransactionConverter.INSTANCE);
  }
}