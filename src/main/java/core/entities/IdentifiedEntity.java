package core.entities;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import lombok.Data;

@Data
@Entity
public abstract class IdentifiedEntity {

  @Id
  @GeneratedValue
  private Long id;
}