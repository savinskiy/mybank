package com.github.savinskiy.core.configuration;

import com.google.inject.AbstractModule;
import com.google.inject.TypeLiteral;
import com.github.savinskiy.converters.AccountConverter;
import com.github.savinskiy.converters.BalanceConverter;
import com.github.savinskiy.converters.TransactionConverter;
import com.github.savinskiy.core.dao.AccountDao;
import com.github.savinskiy.core.dao.GenericDao;
import com.github.savinskiy.core.dao.impl.AccountDaoTransactionedImpl;
import com.github.savinskiy.core.dao.impl.GenericDaoTransactionedImpl;
import com.github.savinskiy.core.entities.Account;
import com.github.savinskiy.rest.routers.AccountRouter;
import com.github.savinskiy.rest.routers.TransactionRouter;
import com.github.savinskiy.rest.routers.impl.AccountRouterImpl;
import com.github.savinskiy.rest.routers.impl.TransactionRouterImpl;
import com.github.savinskiy.services.AccountService;
import com.github.savinskiy.services.TransactionService;
import com.github.savinskiy.services.impl.AccountServiceImpl;
import com.github.savinskiy.services.impl.TransactionServiceImpl;

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