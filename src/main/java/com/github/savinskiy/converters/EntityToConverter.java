package com.github.savinskiy.converters;

import com.github.savinskiy.core.entities.IdentifiedEntity;
import java.math.BigDecimal;
import com.github.savinskiy.rest.to.IdentifiedTo;

public interface EntityToConverter<E extends IdentifiedEntity, T extends IdentifiedTo> {

  E toEntity(T to);

  T toTo(E entity);

  default BigDecimal toBigDecimal(BigDecimal source) {
    return source != null ? new BigDecimal(source.toString()) : BigDecimal.ZERO;
  }
}
