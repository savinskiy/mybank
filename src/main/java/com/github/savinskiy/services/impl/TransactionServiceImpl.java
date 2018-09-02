package com.github.savinskiy.services.impl;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.github.savinskiy.converters.BalanceConverter;
import com.github.savinskiy.converters.TransactionConverter;
import com.github.savinskiy.core.dao.AccountDao;
import com.github.savinskiy.core.dao.GenericDao;
import com.github.savinskiy.core.entities.Account;
import com.github.savinskiy.core.entities.Balance;
import com.github.savinskiy.core.entities.Transaction;
import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import com.github.savinskiy.rest.to.BalanceTo;
import com.github.savinskiy.rest.to.TransactionTo;
import com.github.savinskiy.services.TransactionService;

@Singleton
public class TransactionServiceImpl implements TransactionService {

  private final GenericDao<Transaction> genericDao;
  private final AccountDao accountDao;
  private final TransactionConverter transactionConverter;
  private final BalanceConverter balanceConverter;

  @Inject
  public TransactionServiceImpl(GenericDao genericDao,
      AccountDao accountDao,
      TransactionConverter transactionConverter,
      BalanceConverter balanceConverter) {

    this.genericDao = genericDao;
    this.accountDao = accountDao;
    this.transactionConverter = transactionConverter;
    this.balanceConverter = balanceConverter;
  }

  @Override
  public TransactionTo addTransaction(TransactionTo transactionTo) {

    Transaction transaction = transactionConverter.toEntity(transactionTo);

    Balance transfer = transaction.getAmount();
    BigDecimal transferValue = transfer.getValue();
//      requirePositive(transferValue);

    Account fromAcc = transaction.getAccountFrom();
    Account toAcc = transaction.getAccountTo();
    List<Account> accounts = Arrays.asList(fromAcc, toAcc);
    Collections.sort(accounts);
    Account first = accounts.get(0);
    Account second = accounts.get(1);

    synchronized (first) {
      synchronized (second) {
        Balance fromBalance = findBalanceByCurrency(fromAcc, transfer.getCurrency());
        depositAmount(fromAcc.getId(), fromBalance, transferValue.negate());

        Balance toBalance = findBalanceByCurrency(toAcc, transfer.getCurrency());
        depositAmount(toAcc.getId(), toBalance, transferValue);

        accountDao.save(fromAcc);
        accountDao.save(toAcc);
        // TODO: 29.08.2018 check transaction is already active
        genericDao.save(transaction);
      }
    }
    TransactionTo resultTo = transactionConverter.toTo(transaction);
    return resultTo;
  }

  @Override
  public TransactionTo depositMoney(Long id, BalanceTo balanceTo) {

    Balance deposit = balanceConverter.toEntity(balanceTo);
    BigDecimal depositValue = deposit.getValue();
    Account account = accountDao.getByIdOrThrowException(Account.class, id);

    synchronized (account) {
      Balance accBalance = findBalanceByCurrency(account, deposit.getCurrency());
      depositAmount(account.getId(), accBalance, depositValue);
      accountDao.save(account);
    }

    Transaction transaction = new Transaction();
    transaction.setAmount(deposit);
    transaction.setAccountTo(account);
    transaction.setAccountFrom(null);
    genericDao.save(transaction);

    TransactionTo transactionTo = transactionConverter.toTo(transaction);
    return transactionTo;
  }

  @Override
  public List<TransactionTo> getTransactions() {
    List<Transaction> transactions = genericDao.getAll(Transaction.class);
    List<TransactionTo> transactionTos = transactions.stream()
        .map(transactionConverter::toTo)
        .collect(Collectors.toList());
    return transactionTos;
  }

  @Override
  public TransactionTo getTransactionById(Long id) {
    Transaction transaction = genericDao.getByIdOrThrowException(Transaction.class, id);
    TransactionTo transactionTo = transactionConverter.toTo(transaction);
    return transactionTo;
  }

  private void depositAmount(Long id, Balance balance, BigDecimal value) {
    BigDecimal newBalanceValue = balance.getValue().add(value);
    if (newBalanceValue.compareTo(BigDecimal.ZERO) == -1) {
      throw new IllegalStateException(
          "Payer id=" + id + " doesn't have enough money");
    }
    balance.setValue(newBalanceValue);
  }

  private Balance findBalanceByCurrency(Account account, Balance.Currency currency) {

    return account.getBalances().stream()
        .filter(balance -> balance.getCurrency().equals(currency))
        .findFirst()
        .orElseThrow(() -> new IllegalStateException(
            "Unknown type of currency: " + currency));
  }

  private void requirePositive(BigDecimal transferValue) {
    if (transferValue.compareTo(BigDecimal.ZERO) == -1) {
      throw new IllegalArgumentException("Transfer value should be >= 0");
    }
  }
}
