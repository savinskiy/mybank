package com.github.savinskiy.core.dao;

import com.github.savinskiy.core.entities.IdentifiedEntity;
import java.util.List;

public interface GenericDao<T extends IdentifiedEntity> {

  void save(IdentifiedEntity entity);

  List<T> getAll(Class<T> clazz);

  T getById(Class<T> clazz, long id);

  T getByIdOrThrowException(Class<T> clazz, long id);

  void delete(IdentifiedEntity entity);
}
