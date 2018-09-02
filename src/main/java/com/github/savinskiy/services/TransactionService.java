package com.github.savinskiy.services;

import java.util.List;
import com.github.savinskiy.rest.to.BalanceTo;
import com.github.savinskiy.rest.to.TransactionTo;

public interface TransactionService {

  TransactionTo addTransaction(TransactionTo transaction);

  TransactionTo depositMoney(Long id, BalanceTo balance);

  List<TransactionTo> getTransactions();

  TransactionTo getTransactionById(Long id);
}
