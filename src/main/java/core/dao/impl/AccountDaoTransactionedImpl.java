package core.dao.impl;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import core.dao.AccountDao;
import core.entities.Account;
import javax.persistence.EntityManager;

@Singleton
public class AccountDaoTransactionedImpl extends GenericDaoTransactionedImpl<Account>
    implements AccountDao {

  @Inject
  public AccountDaoTransactionedImpl(EntityManager entityManager) {
    super(entityManager);
  }
}