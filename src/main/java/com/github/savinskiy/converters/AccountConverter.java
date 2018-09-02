package com.github.savinskiy.converters;

import com.github.savinskiy.core.entities.Account;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;
import org.mapstruct.factory.Mappers;
import com.github.savinskiy.rest.to.AccountTo;

@Mapper(componentModel = "jsr330", uses = BalanceConverter.class)
public interface AccountConverter extends EntityToConverter<Account, AccountTo> {

  public final static AccountConverter INSTANCE = Mappers.getMapper(AccountConverter.class);

  // TODO: 29.08.2018 move id mappping to parent class (later)
  @Mappings({
      @Mapping(target = "id", ignore = true),
      @Mapping(target = "balances", ignore = true)
  })
  @Override
  Account toEntity(AccountTo to);

  @Override
  AccountTo toTo(Account entity);
}
