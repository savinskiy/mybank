package rest.to;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class TransactionTo extends IdentifiedTo {

  private BalanceTo amount;

  private Long accountIdFrom;

  private Long accountIdTo;
}