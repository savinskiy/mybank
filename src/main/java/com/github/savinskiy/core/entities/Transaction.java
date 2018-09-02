package com.github.savinskiy.core.entities;

import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
public class Transaction extends IdentifiedEntity {

  @Embedded
  @Column(nullable = false)
  private Balance amount;

  @ManyToOne
  @JoinColumn(name = "account_id_from")
  private Account accountFrom;

  @ManyToOne(optional = false)
  @JoinColumn(name = "account_id_to")
  private Account accountTo;
}