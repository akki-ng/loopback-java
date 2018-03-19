package com.flipkart.loopback.connector;

import com.flipkart.loopback.exception.external.ConnectorException;
import com.flipkart.loopback.exception.model.persistence.ModelNotFoundException;
import com.flipkart.loopback.filter.Filter;
import com.flipkart.loopback.filter.WhereFilter;
import com.flipkart.loopback.model.PersistedModel;
import com.flipkart.loopback.query.QueryGenerator;
import com.google.common.collect.Lists;
import java.io.Serializable;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;
import javax.persistence.NoResultException;
import javax.persistence.Query;
import javax.persistence.TypedQuery;

/**
 * Created by akshaya.sharma on 11/03/18
 */

public class JPAConnector extends Connector {
  private static Map<String, JPAConnector> config = new ConcurrentHashMap<String, JPAConnector>();

  private JPAConnector(String persistenceUnit) {
    super(persistenceUnit);
  }

  public static JPAConnector getInstance(String persistenceUnit) {
    if (!config.containsKey(persistenceUnit)) {
      config.put(persistenceUnit, new JPAConnector(persistenceUnit));
    }
    return config.get(persistenceUnit);
  }

  @Override
  public EntityManager getEntityManager() {
    return EMProvider.getEm(getIdentifier());
  }

  @Override
  public EntityTransaction getCurrentTransaction() {
    return getEntityManager().getTransaction();
  }

  @Override
  public void clearEntityManager() {
    EMProvider.clear(getIdentifier());
  }

  private <M extends PersistedModel> WhereFilter createIDFilter(Class<M> modelClass,
      Serializable id) {
    return PersistedModel.createIDFilter(modelClass, id);
  }

  @Override
  public <M extends PersistedModel, W extends WhereFilter> long count(Class<M> modelClass,
      W where) throws ConnectorException {
    try {
      EntityManager em = getEntityManager();
      TypedQuery<Long> typedQuery = QueryGenerator.getInstance().getCountTypedQuery(em, modelClass,
          where);
      return typedQuery.getSingleResult();
    } catch (NoResultException e) {
      return 0;
    }
  }

  @Override
  public <M extends PersistedModel> M create(M model) throws ConnectorException {
    EntityManager em = getEntityManager();
    em.persist(model);
    return model;
  }

  @Override
  public <M extends PersistedModel> List<M> create(List<M> models) throws ConnectorException {
    List<M> persistedModels = Lists.newArrayList();
    for (int i = 0; i < models.size(); i++) {
      persistedModels.add(create(models.get(i)));
    }
    return persistedModels;
  }

  @Override
  public <M extends PersistedModel> long patchMultipleWithWhere(Class<M> modelClass,
      WhereFilter where, Map<String, Object> data) throws ConnectorException {
    return 0;
  }

  @Override
  public <M extends PersistedModel, W extends WhereFilter> long updateAll(Class<M> modelClass,
      W filter, Map<String, Object> data) throws ConnectorException {
    return 0;
  }

  @Override
  public <M extends PersistedModel> boolean exists(Class<M> modelClass,
      Serializable id) throws ConnectorException {
    EntityManager em = getEntityManager();
    WhereFilter where = createIDFilter(modelClass, id);
    long count = count(modelClass, where);
    return count > 0;
  }

  @Override
  public <M extends PersistedModel, F extends Filter> List<M> find(Class<M> modelClass,
      F filter) throws ConnectorException {
    EntityManager em = getEntityManager();
    TypedQuery<M> typedQuery = QueryGenerator.getInstance().getSelectTypedQuery(em, modelClass,
        filter);
    return typedQuery.getResultList();
  }

  @Override
  public <M extends PersistedModel, F extends Filter> M findOne(Class<M> modelClass,
      F filter) throws ConnectorException, ModelNotFoundException {
    try {
      EntityManager em = getEntityManager();

      TypedQuery<M> typedQuery = QueryGenerator.getInstance().getSelectTypedQuery(em, modelClass,
          filter);
      return typedQuery.getSingleResult();
    } catch (NoResultException e) {
      throw new ModelNotFoundException(modelClass, filter);
    }
  }

  @Override
  public <M extends PersistedModel> M destroy(M model) throws ConnectorException {
    EntityManager em = getEntityManager();
    em.refresh(model);
    em.remove(model);
    return model;
  }

  @Override
  public <M extends PersistedModel, W extends WhereFilter> long destroyAll(Class<M> modelClass,
      W where) throws ConnectorException {
    EntityManager em = getEntityManager();
    Query query = QueryGenerator.getInstance().getDeleteQuery(em, modelClass, where);
    return query.executeUpdate();
  }
}
