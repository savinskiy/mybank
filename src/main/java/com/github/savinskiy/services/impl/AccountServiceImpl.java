package com.github.savinskiy.services.impl;

import com.github.savinskiy.converters.AccountConverter;
import com.github.savinskiy.core.dao.AccountDao;
import com.github.savinskiy.core.entities.Account;
import com.github.savinskiy.rest.to.AccountTo;
import com.github.savinskiy.services.AccountService;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import java.util.List;
import java.util.stream.Collectors;

// TODO: 25.08.2018 lombok injection (later)
// TODO: 25.08.2018 uuid (later)
// TODO: 29.08.2018 print errorTO stack-trace only in dev mode
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
    account.setDeleted(false);
    accountDao.save(account);
    AccountTo resultTo = accountConverter.toTo(account);
    return resultTo;
  }

  @Override
  public List<AccountTo> getAccounts() {
    List<Account> accounts = accountDao.getAll(Account.class);
    List<AccountTo> accountTos = accounts.stream()
        .map(accountConverter::toTo)
        .collect(Collectors.toList());
    return accountTos;
  }

  @Override
  public AccountTo getAccountById(Long id) {
    Account account = accountDao.getByIdOrThrowException(Account.class, id);
    AccountTo accountTo = accountConverter.toTo(account);
    return accountTo;
  }

  @Override
  public void deleteAccountById(Long id) {
    Account account = accountDao.getByIdOrThrowException(Account.class, id);
    account.setDeleted(true);
    accountDao.update(account);
  }
}
