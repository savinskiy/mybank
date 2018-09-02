package com.github.savinskiy.converters;

import com.github.savinskiy.core.dao.AccountDao;
import com.github.savinskiy.core.entities.Account;
import com.github.savinskiy.core.entities.Transaction;
import com.github.savinskiy.rest.to.TransactionTo;
import com.google.inject.Inject;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "jsr330", uses = BalanceConverter.class)
public abstract class TransactionConverter implements
    EntityToConverter<Transaction, TransactionTo> {

  public final static TransactionConverter INSTANCE = Mappers.getMapper(TransactionConverter.class);

  @Inject
  private AccountDao accountDao;

  @Mappings({
      @Mapping(target = "id", ignore = true),
      @Mapping(source = "accountIdFrom", target = "accountFrom"),
      @Mapping(source = "accountIdTo", target = "accountTo")
  })
  @Override
  public abstract Transaction toEntity(TransactionTo to);

  @Mappings({
      @Mapping(source = "accountFrom.id", target = "accountIdFrom"),
      @Mapping(source = "accountTo.id", target = "accountIdTo")
  })
  @Override
  public abstract TransactionTo toTo(Transaction entity);

  public Account map(long id) {
    Account account = accountDao.getByIdOrThrowException(Account.class, id);
    return account;
  }
}
