package br.com.landing.repository;

import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;
import javax.persistence.TypedQuery;
import java.util.List;

public class Repository<T> {
    private final EntityManager entityManager;
    private final Class<T> entityClass;

    public Repository(EntityManager entityManager, Class<T> entityClass) {
        this.entityManager = entityManager;
        this.entityClass = entityClass;
    }

    public void persist(T entity) {
        EntityTransaction transaction = entityManager.getTransaction();
        try {
            transaction.begin();
            entityManager.persist(entity);
            transaction.commit();
        } catch (Exception e) {
            if (transaction.isActive()) {
                transaction.rollback();
            }
            throw e; // Rethrow the exception to be handled by the caller
        }
    }

    public void remove(T entity) {
        EntityTransaction transaction = entityManager.getTransaction();
        try {
            transaction.begin();
            if (entityManager.contains(entity)) {
                entityManager.remove(entity);
            }
            transaction.commit();
        } catch (Exception e) {
            if (transaction.isActive()) {
                transaction.rollback();
            }
            throw e;
        }
    }

    public T merge(T entity) {
        EntityTransaction transaction = entityManager.getTransaction();
        try {
            transaction.begin();
            T mergedEntity = null;
            if (entityManager.contains(entity)) {
                mergedEntity = entityManager.merge(entity);
            }
            transaction.commit();
            return mergedEntity;
        } catch (Exception e) {
            if (transaction.isActive()) {
                transaction.rollback();
            }
            throw e;
        }
    }

    public T findById(Object id) {
        return entityManager.find(entityClass, id);
    }

    public List<T> findAll() {
        TypedQuery<T> query = entityManager.createQuery("SELECT e FROM " + entityClass.getSimpleName() + " e", entityClass);
        return query.getResultList();
    }

    public List<T> findByNamedQuery(String namedQuery, Object... parameters) {
        TypedQuery<T> query = entityManager.createNamedQuery(namedQuery, entityClass);
        for (int i = 0; i < parameters.length; i++) {
            query.setParameter(i + 1, parameters[i]);
        }
        return query.getResultList();
    }
}