package com.github.savinskiy.core.entities;

import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.MappedSuperclass;
import lombok.Data;

@Data
@MappedSuperclass
public abstract class IdentifiedEntity {

  @Id
  @GeneratedValue
  private Long id;
}