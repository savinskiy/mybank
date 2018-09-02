package com.github.savinskiy.core.dao.impl;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.github.savinskiy.core.dao.GenericDao;
import com.github.savinskiy.core.entities.IdentifiedEntity;
import java.util.List;
import java.util.Optional;
import javax.persistence.EntityExistsException;
import javax.persistence.EntityManager;

// unused for now
@Singleton
public class GenericDaoImpl<T extends IdentifiedEntity> implements GenericDao<T> {

  private final EntityManager entityManager;

  @Inject
  public GenericDaoImpl(EntityManager entityManager) {
    this.entityManager = entityManager;
  }

  @Override
  public void save(IdentifiedEntity object) {
    entityManager.persist(object);
  }

  @Override
  public List<T> getAll(Class<T> clazz) {
    return entityManager.createQuery(
        "Select t from " + clazz.getSimpleName() + " t").getResultList();
  }

  @Override
  public T getById(Class<T> clazz, long id) {
    return entityManager.find(clazz, id);
  }

  @Override
  public T getByIdOrThrowException(Class<T> clazz, long id) {
    return Optional.ofNullable(entityManager.find(clazz, id))
        .orElseThrow(() -> new EntityExistsException("Entity with id=" + id + " was not found"));
  }

  @Override
  public void delete(IdentifiedEntity entity) {
    entityManager.remove(entity);
  }
}