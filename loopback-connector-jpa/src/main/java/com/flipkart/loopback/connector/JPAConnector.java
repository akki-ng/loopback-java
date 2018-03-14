package com.flipkart.loopback.connector;

import com.flipkart.loopback.configuration.ModelConfiguration;
import com.flipkart.loopback.configuration.manager.ModelConfigurationManager;
import com.flipkart.loopback.query.QueryGenerator;
import com.flipkart.loopback.exception.LoopbackException;
import com.flipkart.loopback.filter.Filter;
import com.flipkart.loopback.filter.WhereFilter;
import com.flipkart.loopback.model.PersistedModel;
import java.io.IOException;
import java.io.Serializable;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.NoResultException;
import javax.persistence.Persistence;
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
    if(!config.containsKey(persistenceUnit)) {
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

  @Override
  public <M extends PersistedModel, W extends WhereFilter> long count(Class<M> modelClass, W
      where) {
    EntityManager em = getEntityManager();
    TypedQuery<Long> typedQuery = QueryGenerator.getInstance().getCountTypedQuery(em, modelClass,
        where);
    return typedQuery.getSingleResult();
  }

  @Override
  public <M extends PersistedModel> M create(M model) {
    if(model.getId() != null) {

    }
    EntityManager em = getEntityManager();
//    em.getTransaction().begin();
    em.persist(model);
//    em.getTransaction().commit();
    return model;
  }

  @Override
  public <M extends PersistedModel> List<M> create(List<M> models) {
    if(models != null) {
      List<M> persisted =  models.stream()
          .map(model -> {
            return (M) this.create(model);
          })
          .collect(Collectors.toList());
      return persisted;
    }
    return models;
  }

  @Override
  public <M extends PersistedModel> long patchMultipleWithWhere(Class<M> modelClass, WhereFilter
      where, Map<String, Object> data) {
    return 0;
  }

  @Override
  public <M extends PersistedModel, W extends WhereFilter> long updateAll(Class<M> modelClass,
                                                                         W filter,
                                                                         Map<String, Object> data) {
    return 0;
  }

  @Override
  public <M extends PersistedModel, F extends Filter> M updateAttributes(M model, F filter,
                                                                         Map<String, Object> data) {
    // TODO must be implemented by connector only
    return null;
  }

  @Override
  public <M extends PersistedModel> M replaceById(M model, Object id) {
    EntityManager em = getEntityManager();
//    em.getTransaction().begin();
    em.merge(model);
//    em.getTransaction().commit();
    return model;
  }

  @Override
  public <M extends PersistedModel> boolean exists(Class<M> modelClass, Object id) {
    ModelConfiguration configuration = ModelConfigurationManager.getInstance()
        .getModelConfiguration(modelClass);
    EntityManager em = getEntityManager();
    try {
      WhereFilter where = new WhereFilter("{\"" + configuration.getIdPropertyName() + "\": "
          + id +  "}");
      long count = count(modelClass, where);
      return count > 0;
    } catch(NoResultException e) {
      e.printStackTrace();
    }
    return false;
  }

  @Override
  public <M extends PersistedModel, F extends Filter> List<M> find(Class<M> modelClass, F filter) {
    EntityManager em = getEntityManager();
    TypedQuery<M> typedQuery = QueryGenerator.getInstance().getSelectTypedQuery(em, modelClass,
        filter);
    List<M> data = typedQuery.getResultList();
    return data;
  }

  @Override
  public <M extends PersistedModel> M findById(Class<M> modelClass, Filter filter, Serializable id) {
    ModelConfiguration configuration = ModelConfigurationManager.getInstance()
        .getModelConfiguration(modelClass);
    EntityManager em = getEntityManager();
    try {

      Filter idFilter = new Filter("{\"where\": {\"" + configuration.getIdPropertyName() + "\": " +
          id + "}}");

      TypedQuery<M> typedQuery = QueryGenerator.getInstance().getSelectTypedQuery(em, modelClass,
          idFilter);
      return typedQuery.getSingleResult();
    }catch (Throwable e) {
      e.printStackTrace();
      return null;
    }
  }

  @Override
  public <M extends PersistedModel, F extends Filter> M findOne(Class<M> modelClass, F filter) {
    return null;
  }

  @Override
  public <M extends PersistedModel> M destroy(M model) {
    EntityManager em = getEntityManager();
//    em.getTransaction().begin();
    em.remove(model);
//    em.remove(em.contains(model) ? model : em.merge(model));
//    em.getTransaction().commit();
    return model;
  }

  @Override
  public <M extends PersistedModel, F extends Filter> long destroyAll(M model, F filter) {
    return 0;
  }
}
