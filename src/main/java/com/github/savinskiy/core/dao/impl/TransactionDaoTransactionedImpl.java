package com.github.savinskiy.core.dao.impl;

import com.github.savinskiy.core.dao.TransactionDao;
import com.github.savinskiy.core.entities.Transaction;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import javax.persistence.EntityManager;

@Singleton
public class TransactionDaoTransactionedImpl extends GenericDaoTransactionedImpl<Transaction>
    implements TransactionDao {

  @Inject
  public TransactionDaoTransactionedImpl(EntityManager entityManager) {
    super(entityManager);
  }
}