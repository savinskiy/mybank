package core.dao.impl;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import core.dao.GenericDao;
import core.entities.IdentifiedEntity;
import java.util.List;
import java.util.Optional;
import javax.persistence.EntityExistsException;
import javax.persistence.EntityManager;

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
    entityManager.getTransaction().begin();
    entityManager.remove(entity);
    entityManager.getTransaction().commit();
  }
}