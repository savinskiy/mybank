package converters;

import com.google.inject.Inject;
import core.dao.GenericDao;
import core.entities.Account;
import core.entities.Transaction;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;
import org.mapstruct.factory.Mappers;
import rest.to.TransactionTo;

@Mapper(componentModel = "jsr330", uses = BalanceConverter.class)
public abstract class TransactionConverter implements
    EntityToConverter<Transaction, TransactionTo> {

  public final static TransactionConverter INSTANCE = Mappers.getMapper(TransactionConverter.class);

  @Inject
  private GenericDao<Account> genericDao;

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
    Account account = genericDao.getByIdOrThrowException(Account.class, id);
    return account;
  }
}
