package com.github.savinskiy.core.entities;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.jetbrains.annotations.NotNull;

// TODO: 29.08.2018 make field isDeleted
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
public class Account extends IdentifiedEntity implements Comparable<Account> {

  private String name;

  private String details;

  @ElementCollection
  private List<Balance> balances = initBalances();

  @Override
  public int compareTo(@NotNull Account o) {
    return Long.compare(this.getId(), o.getId());
  }

  private List<Balance> initBalances() {
    return Arrays.asList(Balance.Currency.values()).stream()
        .map(this::createEmptyBalance)
        .collect(Collectors.toList());
  }

  private Balance createEmptyBalance(Balance.Currency currency) {
    return Balance.builder()
        .currency(currency)
        .value(BigDecimal.ZERO)
        .build();
  }
}