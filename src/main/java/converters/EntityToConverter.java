package converters;

import core.entities.IdentifiedEntity;
import rest.to.IdentifiedTo;

public interface EntityToConverter<E extends IdentifiedEntity, T extends IdentifiedTo> {

  E toEntity(T to);

  T toTo(E entity);
}
