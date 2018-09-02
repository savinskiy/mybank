package com.github.savinskiy.core.dao.impl;

import com.github.savinskiy.core.dao.AccountDao;
import com.github.savinskiy.core.entities.Account;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import javax.persistence.EntityManager;
import javax.persistence.EntityNotFoundException;

@Singleton
public class AccountDaoTransactionedImpl extends GenericDaoTransactionedImpl<Account>
    implements AccountDao {

  @Inject
  public AccountDaoTransactionedImpl(EntityManager entityManager) {
    super(entityManager);
  }

  @Override
  public Account getByIdOrThrowException(Class<Account> clazz, long id) {
    Account account = super.getByIdOrThrowException(clazz, id);
    if (account.isDeleted()) {
      throw new EntityNotFoundException("Entity with id=" + id + " was not found");
    }
    return account;
  }

  @Override
  public List<Account> getAll(Class<Account> clazz) {
    List<Account> accounts = super.getAll(clazz);
    Predicate<Account> isDeleted = Account::isDeleted;
    return accounts.stream()
        .filter(isDeleted.negate())
        .collect(Collectors.toList());
  }
}