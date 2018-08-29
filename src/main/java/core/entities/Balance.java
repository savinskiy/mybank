package core.entities;

import java.math.BigDecimal;
import javax.persistence.Embeddable;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Embeddable
public class Balance {

  private BigDecimal value;

  @Enumerated(EnumType.STRING)
  private Currency currency;

  public enum Currency {
    RUB, USD, GBP, BTC
  }
}
