package com.github.savinskiy.core.dao.impl;

import com.github.savinskiy.core.dao.GenericDao;
import com.github.savinskiy.core.entities.IdentifiedEntity;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import java.util.List;
import java.util.Optional;
import javax.persistence.EntityManager;
import javax.persistence.EntityNotFoundException;

@Singleton
public class GenericDaoTransactionedImpl<T extends IdentifiedEntity> implements GenericDao<T> {

  private final EntityManager entityManager;

  @Inject
  public GenericDaoTransactionedImpl(EntityManager entityManager) {
    this.entityManager = entityManager;
  }

  @Override
  public void save(IdentifiedEntity object) {
    entityManager.getTransaction().begin();
    entityManager.persist(object);
    entityManager.getTransaction().commit();
  }

  @Override
  public List<T> getAll(Class<T> clazz) {
    // TODO: 02.09.2018 check why sometimes it couldn't be executed for the first time
    for (int i = 0; i < 5; i++) {
      try {
        return entityManager.createQuery(
            "Select t from " + clazz.getSimpleName() + " t").getResultList();
      } catch (Exception e) {
        System.out.println(e);
        System.out.println("Repeat number: " + (i + 1));
      }
    }
    return null;
  }

  @Override
  public T getById(Class<T> clazz, long id) {
    return entityManager.find(clazz, id);
  }

  @Override
  public T getByIdOrThrowException(Class<T> clazz, long id) {
    return Optional.ofNullable(entityManager.find(clazz, id))
        .orElseThrow(() -> new EntityNotFoundException("Entity with id=" + id + " was not found"));
  }

  @Override
  public void delete(IdentifiedEntity entity) {
    entityManager.getTransaction().begin();
    entityManager.remove(entity);
    entityManager.getTransaction().commit();
  }

  @Override
  public void update(IdentifiedEntity entity) {
    entityManager.getTransaction().begin();
    entityManager.merge(entity);
    entityManager.getTransaction().commit();
  }
}