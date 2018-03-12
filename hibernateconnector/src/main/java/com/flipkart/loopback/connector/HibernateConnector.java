package com.flipkart.loopback.connector;

import com.flipkart.loopback.filter.Filter;
import com.flipkart.loopback.filter.WhereFilter;
import com.flipkart.loopback.model.PersistedModel;
import java.util.List;
import java.util.Map;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import org.hibernate.SessionFactory;

/**
 * Created by akshaya.sharma on 12/03/18
 */

public class HibernateConnector implements Connector {
  private static HibernateConnector instance = null;
  private static SessionFactory sessionFactory;

  public HibernateConnector(SessionFactory sessionFactory) {
    this.sessionFactory = sessionFactory;
  }

  public static HibernateConnector getInstance() {
    if (instance == null) {
      EntityManagerFactory entityManagerFactory = Persistence.createEntityManagerFactory
          ("DEFAULT_LOCAL");
      instance = new HibernateConnector(entityManagerFactory.unwrap(SessionFactory.class));
    }
    return instance;
  }

  @Override
  public <M extends PersistedModel, F extends Filter> int count(Class<M> modelClass, F filter) {
    return 0;
  }

  @Override
  public <M extends PersistedModel> M create(M model) {
    return null;
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
                                                                             Map<String, Object>
                                                                                 data) {
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
    return null;
  }

  @Override
  public <M extends PersistedModel> M replaceOrCreate(M model) {
    return null;
  }

  @Override
  public <M extends PersistedModel> boolean exists(Class<M> modelClass, Object id) {
    return false;
  }

  @Override
  public <M extends PersistedModel, F extends Filter> List<M> find(Class<M> modelClass, F filter) {
    return sessionFactory.getCurrentSession().createCriteria(modelClass).list();
  }

  @Override
  public <M extends PersistedModel> M findById(Class<M> modelClass, Filter filter, Object id) {
    return null;
  }

  @Override
  public <M extends PersistedModel, F extends Filter> M findOne(Class<M> modelClass, F filter) {
    return null;
  }

  @Override
  public <M extends PersistedModel> M destroy(M model) {
    return null;
  }

  @Override
  public <M extends PersistedModel, F extends Filter> int destroyAll(M model, F filter) {
    return 0;
  }
}
