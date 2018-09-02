package com.github.savinskiy.services.impl;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.github.savinskiy.converters.AccountConverter;
import com.github.savinskiy.core.dao.GenericDao;
import com.github.savinskiy.core.entities.Account;
import java.util.List;
import java.util.stream.Collectors;
import com.github.savinskiy.rest.to.AccountTo;
import com.github.savinskiy.services.AccountService;

// TODO: 25.08.2018 lombok injection (later)
// TODO: 25.08.2018 uuid (later)
// TODO: 29.08.2018 print errorTO stack-trace only in dev mode
@Singleton
public class AccountServiceImpl implements AccountService {

  private final GenericDao<Account> genericDao;
  private final AccountConverter accountConverter;

  @Inject
  public AccountServiceImpl(GenericDao genericDao,
      AccountConverter accountConverter) {
    this.genericDao = genericDao;
    this.accountConverter = accountConverter;
  }

  @Override
  public AccountTo addAccount(AccountTo accountTo) {
    Account account = accountConverter.toEntity(accountTo);
    genericDao.save(account);
    AccountTo resultTo = accountConverter.toTo(account);
    return resultTo;
  }

  @Override
  public List<AccountTo> getAccounts() {
    List<Account> accounts = genericDao.getAll(Account.class);
    List<AccountTo> accountTos = accounts.stream()
        .map(accountConverter::toTo)
        .collect(Collectors.toList());
    return accountTos;
  }

  @Override
  public AccountTo getAccountById(Long id) {
    Account account = genericDao.getByIdOrThrowException(Account.class, id);
    AccountTo accountTo = accountConverter.toTo(account);
    return accountTo;
  }

  @Override
  public void deleteAccountById(Long id) {
    Account account = genericDao.getByIdOrThrowException(Account.class, id);
    genericDao.delete(account);
  }
}
