package rest.to;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonProperty.Access;
import lombok.Data;

@Data
public abstract class IdentifiedTo {

  @JsonProperty(access = Access.READ_ONLY)
  private Long id;
}
