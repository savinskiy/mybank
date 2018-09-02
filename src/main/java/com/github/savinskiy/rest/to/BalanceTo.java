package com.github.savinskiy.rest.to;

import java.math.BigDecimal;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class BalanceTo {

  private BigDecimal value;

  private Currency currency;

  public enum Currency {
    RUB, USD, GBP, BTC
  }
}
