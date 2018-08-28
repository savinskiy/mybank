package core.configuration;

import com.google.inject.AbstractModule;
import converters.AccountConverter;
import core.dao.AccountDao;
import core.dao.impl.AccountDaoImpl;
import core.dao.impl.AccountDaoTransactionedImpl;
import rest.routers.AccountRouter;
import rest.routers.impl.AccountRouterImpl;
import services.AccountService;
import services.impl.AccountServiceImpl;

public class AppModule extends AbstractModule {

  @Override
  public void configure() {
    bind(AccountService.class).to(AccountServiceImpl.class).asEagerSingleton();
    bind(AccountDao.class).to(AccountDaoTransactionedImpl.class).asEagerSingleton();
//    bind(AccountDao.class).to(AccountDaoImpl.class).asEagerSingleton();
    bind(AccountRouter.class).to(AccountRouterImpl.class).asEagerSingleton();
    bind(AccountConverter.class).toInstance(AccountConverter.INSTANCE);
  }
}