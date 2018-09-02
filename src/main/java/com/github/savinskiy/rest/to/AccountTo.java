package com.github.savinskiy.rest.to;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class AccountTo extends IdentifiedTo {

  private String name;

  private String details;

  private List<BalanceTo> balances;
}