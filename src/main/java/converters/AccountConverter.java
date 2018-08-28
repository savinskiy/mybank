package converters;

import core.entities.Account;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;
import rest.to.AccountTo;

@Mapper(componentModel = "jsr330")
public interface AccountConverter extends EntityToConverter<Account, AccountTo> {

  public static AccountConverter INSTANCE = Mappers.getMapper(AccountConverter.class);

  @Mapping(target = "id", ignore = true)
  @Override
  Account toEntity(AccountTo to);

  @Override
  AccountTo toTo(Account entity);
}
