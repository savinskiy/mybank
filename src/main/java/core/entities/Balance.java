package core.entities;

import java.math.BigDecimal;
import javax.persistence.Embeddable;
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

  private Currency currency;

  public enum Currency {
    RUB, USD, GBP, BTC
  }
}
