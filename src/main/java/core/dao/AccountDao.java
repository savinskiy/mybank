package core.dao;

import core.entities.Account;
import java.util.List;

public interface AccountDao {

  void save(Account object);

  List<Account> getAll();

  Account getById(Long id);

  void delete(Account account);
}
