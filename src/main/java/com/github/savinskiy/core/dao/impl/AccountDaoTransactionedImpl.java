package com.github.savinskiy.core.dao.impl;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.github.savinskiy.core.dao.AccountDao;
import com.github.savinskiy.core.entities.Account;
import javax.persistence.EntityManager;

@Singleton
public class AccountDaoTransactionedImpl extends GenericDaoTransactionedImpl<Account>
    implements AccountDao {

  @Inject
  public AccountDaoTransactionedImpl(EntityManager entityManager) {
    super(entityManager);
  }
}