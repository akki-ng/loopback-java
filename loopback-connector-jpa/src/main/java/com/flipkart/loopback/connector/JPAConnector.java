package com.flipkart.loopback.connector;

import com.flipkart.loopback.configuration.ModelConfiguration;
import com.flipkart.loopback.configuration.manager.ModelConfigurationManager;
import com.flipkart.loopback.exception.ConnectorException;
import com.flipkart.loopback.exception.InternalError;
import com.flipkart.loopback.exception.InvalidFilterException;
import com.flipkart.loopback.exception.InvalidPropertyValueException;
import com.flipkart.loopback.exception.LoopbackException;
import com.flipkart.loopback.exception.ModelNotConfiguredException;
import com.flipkart.loopback.filter.Filter;
import com.flipkart.loopback.filter.WhereFilter;
import com.flipkart.loopback.model.PersistedModel;
import com.flipkart.loopback.query.QueryGenerator;
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

  private <M extends PersistedModel> WhereFilter createIDFilter(Class<M> modelClass, Serializable
   id ) throws InvalidFilterException, InternalError, InvalidPropertyValueException {
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
    } catch (Throwable e) {
      throw new ConnectorException(e);
    }
  }

  @Override
  public <M extends PersistedModel> M create(M model) throws ConnectorException {
    try {
      if (model.getId() != null) {

      }
      EntityManager em = getEntityManager();
//    em.getTransaction().begin();
      em.persist(model);
//    em.getTransaction().commit();
      return model;
    } catch (Throwable e) {
      throw new ConnectorException(e);
    }
  }

  @Override
  public <M extends PersistedModel> List<M> create(List<M> models) throws ConnectorException {
    if (models != null) {
      for (int i = 0; i < models.size(); i++) {
        models.set(i, this.create(models.get(i)));
      }
    }
    return models;
  }

  @Override
  public <M extends PersistedModel> long patchMultipleWithWhere(Class<M> modelClass,
      WhereFilter where, Map<String, Object> data) throws ConnectorException {
    try {// TODO
      return 0;
    } catch (Throwable e) {
      throw new ConnectorException(e);
    }
  }

  @Override
  public <M extends PersistedModel, W extends WhereFilter> long updateAll(Class<M> modelClass,
      W filter, Map<String, Object> data) throws ConnectorException {
    try { // TODO
      return 0;
    } catch (Throwable e) {
      throw new ConnectorException(e);
    }
  }

  @Override
  public <M extends PersistedModel, F extends Filter> M updateAttributes(M model, F filter,
      Map<String, Object> data) throws ConnectorException {
    try {// TODO must be implemented by connector only
      return null;
    } catch (Throwable e) {
      throw new ConnectorException(e);
    }
  }

  @Override
  public <M extends PersistedModel> M replaceById(M model, Serializable id) throws ConnectorException {
    try {
      if(model.getId() == null || !model.getId().toString().equals(id.toString())) {
        throw new InvalidPropertyValueException(model.getClass(), model.getIdPropertyName(),
            model.getId(), " id must be a valid value to use replcaeById");
      }
      EntityManager em = getEntityManager();
//    em.getTransaction().begin();
      em.merge(model);
//    em.getTransaction().commit();
      return model;
    } catch (Throwable e) {
      throw new ConnectorException(e);
    }
  }

  @Override
  public <M extends PersistedModel> boolean exists(Class<M> modelClass,
      Serializable id) throws ConnectorException {
    try {
      ModelConfiguration configuration = ModelConfigurationManager.getInstance()
          .getModelConfiguration(
          modelClass);
      EntityManager em = getEntityManager();
      WhereFilter where = createIDFilter(modelClass, id);
      long count = count(modelClass, where);
      return count > 0;
    } catch (Throwable e) {
      throw new ConnectorException(e);
    }
  }

  @Override
  public <M extends PersistedModel, F extends Filter> List<M> find(Class<M> modelClass,
      F filter) throws ConnectorException {
    try {
      EntityManager em = getEntityManager();
      TypedQuery<M> typedQuery = QueryGenerator.getInstance().getSelectTypedQuery(em, modelClass,
          filter);
      List<M> data = typedQuery.getResultList();
      return data;
    } catch (Throwable e) {
      throw new ConnectorException(e);
    }
  }

  @Override
  public <M extends PersistedModel> M findById(Class<M> modelClass, Filter filter,
      Serializable id) throws ConnectorException {
    try {
      ModelConfiguration configuration = ModelConfigurationManager.getInstance()
          .getModelConfiguration(
          modelClass);
      EntityManager em = getEntityManager();

      Filter idFilter = new Filter(
          "{\"where\": {}}");
      idFilter.setWhere(createIDFilter(modelClass, id));

      TypedQuery<M> typedQuery = QueryGenerator.getInstance().getSelectTypedQuery(em, modelClass,
          idFilter);
      return typedQuery.getSingleResult();
    } catch (NoResultException e) {
      return null;
    } catch(Throwable e) {
      throw new ConnectorException(e);
    }
  }

  @Override
  public <M extends PersistedModel, F extends Filter> M findOne(Class<M> modelClass,
      F filter) throws ConnectorException {
    try {
      EntityManager em = getEntityManager();
      TypedQuery<M> typedQuery = QueryGenerator.getInstance().getSelectTypedQuery(em, modelClass,
          filter);
      return typedQuery.getSingleResult();
    } catch (Throwable e) {
      throw new ConnectorException(e);
    }
  }

  @Override
  public <M extends PersistedModel> M destroy(M model) throws ConnectorException {
    try {
      EntityManager em = getEntityManager();
//    em.getTransaction().begin();
      em.remove(model);
//    em.remove(em.contains(model) ? model : em.merge(model));
//    em.getTransaction().commit();
      return model;
    } catch (Throwable e) {
      throw new ConnectorException(e);
    }
  }

  @Override
  public <M extends PersistedModel, W extends WhereFilter> long destroyAll(Class<M> modelClass,
      W where) throws ConnectorException {
    try {// TODO
      EntityManager em = getEntityManager();
      Query query = QueryGenerator.getInstance().getDeleteQuery(em, modelClass, where);
      return query.executeUpdate();
    } catch (Throwable e) {
      throw new ConnectorException(e);
    }
  }
}
