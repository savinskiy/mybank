package services;

import java.util.List;
import rest.to.AccountTo;

public interface AccountService {

  AccountTo addAccount(AccountTo account);

  List<AccountTo> getAccounts();

  AccountTo getAccountById(Long id);

  void deleteAccountById(Long id);
}
