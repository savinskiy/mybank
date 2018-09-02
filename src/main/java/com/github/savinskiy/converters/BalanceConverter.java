package com.github.savinskiy.converters;

import com.github.savinskiy.core.entities.Balance;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;
import com.github.savinskiy.rest.to.BalanceTo;

@Mapper(componentModel = "jsr330")
public interface BalanceConverter {

  public final static BalanceConverter INSTANCE = Mappers.getMapper(BalanceConverter.class);

  Balance toEntity(BalanceTo to);

  BalanceTo toTo(Balance entity);
}
