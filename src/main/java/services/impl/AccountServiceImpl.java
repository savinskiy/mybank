package services.impl;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import converters.AccountConverter;
import core.dao.AccountDao;
import core.entities.Account;
import java.util.List;
import java.util.stream.Collectors;
import rest.to.AccountTo;
import services.AccountService;

// TODO: 25.08.2018 lombok injection (later)
// TODO: 25.08.2018 uuid (later)
@Singleton
public class AccountServiceImpl implements AccountService {

  private final AccountDao accountDao;
  private final AccountConverter accountConverter;

  @Inject
  public AccountServiceImpl(AccountDao accountDao,
      AccountConverter accountConverter) {
    this.accountDao = accountDao;
    this.accountConverter = accountConverter;
  }

  @Override
  public AccountTo addAccount(AccountTo accountTo) {
    Account account = accountConverter.toEntity(accountTo);
    accountDao.save(account);
    AccountTo resultTo = accountConverter.toTo(account);
    return resultTo;
  }

  @Override
  public List<AccountTo> getAccounts() {
    List<Account> accounts = accountDao.getAll();
    List<AccountTo> accountTos = accounts.stream()
        .map(accountConverter::toTo)
        .collect(Collectors.toList());
    return accountTos;
  }

  @Override
  public AccountTo getAccountById(Long id) {
    Account account = accountDao.getById(id);
    AccountTo accountTo = accountConverter.toTo(account);
    return accountTo;
  }

  @Override
  public void deleteAccountById(Long id) {
    Account account = accountDao.getById(id);
    accountDao.delete(account);
  }
}
