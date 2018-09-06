package com.github.savinskiy.services.impl;

import com.github.savinskiy.converters.BalanceConverter;
import com.github.savinskiy.converters.TransactionConverter;
import com.github.savinskiy.core.dao.AccountDao;
import com.github.savinskiy.core.dao.TransactionDao;
import com.github.savinskiy.core.entities.Account;
import com.github.savinskiy.core.entities.Balance;
import com.github.savinskiy.core.entities.Transaction;
import com.github.savinskiy.rest.to.BalanceTo;
import com.github.savinskiy.rest.to.TransactionTo;
import com.github.savinskiy.services.TransactionService;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;

@Singleton
@Slf4j
public class TransactionServiceImpl implements TransactionService {

  private final TransactionDao transactionDao;
  private final AccountDao accountDao;
  private final TransactionConverter transactionConverter;
  private final BalanceConverter balanceConverter;

  @Inject
  public TransactionServiceImpl(TransactionDao transactionDao,
      AccountDao accountDao,
      TransactionConverter transactionConverter,
      BalanceConverter balanceConverter) {

    this.transactionDao = transactionDao;
    this.accountDao = accountDao;
    this.transactionConverter = transactionConverter;
    this.balanceConverter = balanceConverter;
  }

  @Override
  public TransactionTo addTransaction(TransactionTo transactionTo) {
    log.trace("Input TransactionTo: {}", transactionTo);
    log.debug("Start transaction from {} to {}",
        transactionTo.getAccountIdFrom(), transactionTo.getAccountIdTo());

    Transaction transaction = transactionConverter.toEntity(transactionTo);
    log.trace("Converted Transaction: {}", transaction);

    Balance transfer = transaction.getAmount();
    BigDecimal transferValue = transfer.getValue();
    log.debug("transferValue: {}", transferValue);
//      requirePositive(transferValue);

    Account fromAcc = transaction.getAccountFrom();
    Account toAcc = transaction.getAccountTo();
    List<Account> accounts = Arrays.asList(fromAcc, toAcc);
    log.trace("Accounts unordered: {}", accounts);
    Collections.sort(accounts);
    Account first = accounts.get(0);
    Account second = accounts.get(1);
    log.trace("Accounts first: {}", first);
    log.trace("Accounts second: {}", second);

    synchronized (first) {
      log.debug("Acquired lock on {}", first.getId());
      synchronized (second) {
        log.debug("Acquired lock on {}", second.getId());
        Balance fromBalance = findBalanceByCurrency(fromAcc, transfer.getCurrency());
        log.trace("From balance: {}", fromBalance);
        depositAmount(fromAcc.getId(), fromBalance, transferValue.negate());

        Balance toBalance = findBalanceByCurrency(toAcc, transfer.getCurrency());
        log.trace("To balance: {}", toBalance);
        depositAmount(toAcc.getId(), toBalance, transferValue);

        accountDao.save(fromAcc);
        accountDao.save(toAcc);
        // TODO: 29.08.2018 check transaction is already active
        transactionDao.save(transaction);
      }
      log.debug("Released lock on {}", second.getId());
    }
    log.debug("Released lock on {}", first.getId());
    TransactionTo resultTo = transactionConverter.toTo(transaction);
    log.trace("Result TransactionTo: {}", resultTo);
    return resultTo;
  }

  @Override
  public TransactionTo depositMoney(Long id, BalanceTo balanceTo) {
    log.trace("Input id: {}, balanceTo: {}", id, balanceTo);
    log.debug("Start deposit money to {}", id);

    Balance deposit = balanceConverter.toEntity(balanceTo);
    log.trace("Balance entity: {}", deposit);
    BigDecimal depositValue = deposit.getValue();
    Account account = accountDao.getByIdOrThrowException(Account.class, id);
    log.trace("Account entity: {}", account);

    synchronized (account) {
      log.debug("Acquired lock on {}", account.getId());
      Balance accBalance = findBalanceByCurrency(account, deposit.getCurrency());
      log.trace("Account balance: {}", accBalance);
      depositAmount(account.getId(), accBalance, depositValue);
      accountDao.save(account);
    }
    log.debug("Released lock on {}", account.getId());

    Transaction transaction = new Transaction();
    transaction.setAmount(deposit);
    transaction.setAccountTo(account);
    transaction.setAccountFrom(null);
    transactionDao.save(transaction);
    log.trace("Transaction entity created: {}", transaction);

    TransactionTo transactionTo = transactionConverter.toTo(transaction);
    log.trace("TransactionTo converted: {}", transactionTo);
    return transactionTo;
  }

  @Override
  public List<TransactionTo> getTransactions() {
    List<Transaction> transactions = transactionDao.getAll(Transaction.class);
    log.trace("Getting entity transactions: {}", transactions);
    List<TransactionTo> transactionTos = transactions.stream()
        .map(transactionConverter::toTo)
        .collect(Collectors.toList());
    log.trace("Converted transactions: {}", transactionTos);
    return transactionTos;
  }

  @Override
  public TransactionTo getTransactionById(Long id) {
    log.trace("Get Transaction by id: {}", id);
    Transaction transaction = transactionDao.getByIdOrThrowException(Transaction.class, id);
    log.trace("Getting Transaction by id: {}", transaction);
    TransactionTo transactionTo = transactionConverter.toTo(transaction);
    log.trace("Converted transactionTo: {}", transactionTo);
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
