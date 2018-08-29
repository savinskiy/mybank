package services;

import java.util.List;
import rest.to.BalanceTo;
import rest.to.TransactionTo;

public interface TransactionService {

  TransactionTo addTransaction(TransactionTo transaction);

  TransactionTo depositMoney(Long id, BalanceTo balance);

  List<TransactionTo> getTransactions();

  TransactionTo getTransactionById(Long id);
}
