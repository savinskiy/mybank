package core.dao.impl;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import core.dao.AccountDao;
import core.entities.Account;
import java.util.List;
import javax.persistence.EntityManager;

// unused for now
@Singleton
public class AccountDaoImpl implements AccountDao {

  private final EntityManager entityManager;

  @Inject
  public AccountDaoImpl(EntityManager entityManager) {
    this.entityManager = entityManager;
  }

  @Override
  public void save(Account object) {
    entityManager.persist(object);
 }

  @Override
  public List<Account> getAll() {
    return entityManager.createQuery(
        "Select t from " + Account.class.getSimpleName() + " t").getResultList();
  }

  @Override
  public Account getById(Long id) {
    return entityManager.find(Account.class, id);
  }

  @Override
  public void delete(Account account) {
    entityManager.remove(account);
  }
}