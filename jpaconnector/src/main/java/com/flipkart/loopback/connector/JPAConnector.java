package com.flipkart.loopback.connector;

import com.flipkart.loopback.configuration.ModelConfiguration;
import com.flipkart.loopback.configuration.manager.ModelConfigurationManager;
import com.flipkart.loopback.connector.mysql.QueryGenerator;
import com.flipkart.loopback.exception.LoopbackException;
import com.flipkart.loopback.filter.Filter;
import com.flipkart.loopback.filter.WhereFilter;
import com.flipkart.loopback.model.PersistedModel;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.NoResultException;
import javax.persistence.Persistence;
import javax.persistence.TypedQuery;

/**
 * Created by akshaya.sharma on 11/03/18
 */

public class JPAConnector implements Connector {
  EntityManagerFactory entityManagerFactory =  Persistence.createEntityManagerFactory("DEFAULT_LOCAL");
  EntityManager em = entityManagerFactory.createEntityManager();

  @Override
  public <M extends PersistedModel, F extends Filter> int count(Class<M> modelClass, F filter) {
    return 0;
  }

  @Override
  public <M extends PersistedModel> M create(M model) {
    if(model.getId() != null) {

    }
    em.getTransaction().begin();
    em.persist(model);
    em.getTransaction().commit();
    return model;
  }

  @Override
  public <M extends PersistedModel> List<M> create(List<? extends PersistedModel> models) {
    return null;
  }

  @Override
  public <M extends PersistedModel> M updateOrCreate(M model) {
    return null;
  }

  @Override
  public <M extends PersistedModel> M patchOrCreateWithWhere(M model, Map<String, Object> data) {
    return null;
  }

  @Override
  public <M extends PersistedModel, W extends WhereFilter> M upsertWithWhere(Class<M> modelClass,
                                                                             W filter,
                                                                             Map<String, Object> data) {
    return null;
  }

  @Override
  public <M extends PersistedModel, F extends Filter> M findOrCreate(Class<M> modelClass, F filter,
                                                                     Map<String, Object> data) {
    return null;
  }

  @Override
  public <M extends PersistedModel> M save(M model) {
    return null;
  }

  @Override
  public <M extends PersistedModel, W extends WhereFilter> int updateAll(Class<M> modelClass,
                                                                         W filter,
                                                                         Map<String, Object> data) {
    return 0;
  }

  @Override
  public <M extends PersistedModel, F extends Filter> M updateAttributes(M model, F filter,
                                                                         Map<String, Object> data) {
    return null;
  }

  @Override
  public <M extends PersistedModel> M replaceById(M model, Object id) {
    em.getTransaction().begin();
    em.merge(model);
    em.getTransaction().commit();
    return model;
  }

  @Override
  public <M extends PersistedModel> M replaceOrCreate(M model) {
    if(model.getId() == null) {
      return create(model);
    }else {
      return replaceById(model, model.getId());
    }
  }

  @Override
  public <M extends PersistedModel> boolean exists(Class<M> modelClass, Object id) {
    ModelConfiguration configuration = ModelConfigurationManager.getInstance()
        .getModelConfiguration(modelClass);
    try {
      Filter filter = new Filter("{\"where\": {\"" + configuration.getIdPropertyName() + "\": "
          + id +  "}}");

      TypedQuery<M> typedQuery = QueryGenerator.getInstance().getSelectTypedQuery(em, modelClass,
          filter);
      return typedQuery.getSingleResult() != null;
    } catch(NoResultException e) {
      e.printStackTrace();
      return false;
    } catch(IOException e) {
      e.printStackTrace();
    } catch (LoopbackException e) {
      e.printStackTrace();
    }
    return false;
  }

  @Override
  public <M extends PersistedModel, F extends Filter> List<M> find(Class<M> modelClass, F filter) {
    TypedQuery<M> typedQuery = QueryGenerator.getInstance().getSelectTypedQuery(em, modelClass,
        filter);
    List<M> data = typedQuery.getResultList();
    return data;
  }

  @Override
  public <M extends PersistedModel> M findById(Class<M> modelClass, Filter filter, Object id) {
    ModelConfiguration configuration = ModelConfigurationManager.getInstance()
        .getModelConfiguration(modelClass);
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
    em.getTransaction().begin();
    em.remove(model);
//    em.remove(model) ? model : em.merge(model));
    em.getTransaction().commit();
    return model;
  }

  @Override
  public <M extends PersistedModel, F extends Filter> int destroyAll(M model, F filter) {
    return 0;
  }
}
