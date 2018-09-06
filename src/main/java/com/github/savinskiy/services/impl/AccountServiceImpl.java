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
import lombok.extern.slf4j.Slf4j;

// TODO: 25.08.2018 lombok injection (later)
// TODO: 25.08.2018 uuid (later)
// TODO: 29.08.2018 print errorTO stack-trace only in dev mode
@Singleton
@Slf4j
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
    log.trace("Input AccountTo: {}", accountTo);
    Account account = accountConverter.toEntity(accountTo);
    account.setDeleted(false);
    log.trace("Converted account: {}", account);
    accountDao.save(account);
    AccountTo resultTo = accountConverter.toTo(account);
    log.trace("Converted result: {}", resultTo);
    return resultTo;
  }

  @Override
  public List<AccountTo> getAccounts() {
    List<Account> accounts = accountDao.getAll(Account.class);
    log.trace("Getting entity accounts: {}", accounts);
    List<AccountTo> accountTos = accounts.stream()
        .map(accountConverter::toTo)
        .collect(Collectors.toList());
    log.trace("Converted accounts: {}", accountTos);
    return accountTos;
  }

  @Override
  public AccountTo getAccountById(Long id) {
    log.trace("Get Account by id: {}", id);
    Account account = accountDao.getByIdOrThrowException(Account.class, id);
    log.trace("Account entity: {}", account);
    AccountTo accountTo = accountConverter.toTo(account);
    log.trace("Converted account: {}", accountTo);
    return accountTo;
  }

  @Override
  public void deleteAccountById(Long id) {
    log.trace("Delete Account by id: {}", id);
    Account account = accountDao.getByIdOrThrowException(Account.class, id);
    account.setDeleted(true);
    log.trace("Deleted Account entity: {}: {}", account);
    accountDao.update(account);
  }
}
