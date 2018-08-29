package converters;

import core.entities.Balance;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;
import rest.to.BalanceTo;

@Mapper(componentModel = "jsr330")
public interface BalanceConverter {

  public final static BalanceConverter INSTANCE = Mappers.getMapper(BalanceConverter.class);

  Balance toEntity(BalanceTo to);

  BalanceTo toTo(Balance entity);
}
