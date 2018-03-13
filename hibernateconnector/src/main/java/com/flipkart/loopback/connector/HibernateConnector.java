package com.flipkart.loopback.connector;

import com.flipkart.loopback.filter.Filter;
import com.flipkart.loopback.filter.WhereFilter;
import com.flipkart.loopback.model.PersistedModel;
import io.dropwizard.hibernate.UnitOfWork;
import java.io.Serializable;
import java.util.List;
import java.util.Map;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.Persistence;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.springframework.transaction.annotation.Transactional;

/**
 * Created by akshaya.sharma on 12/03/18
 */
public class HibernateConnector extends Connector {
  private static HibernateConnector instance = null;
  private static SessionFactory sessionFactory;

  public HibernateConnector(String persistenceUnit) {
    super(persistenceUnit);
  }

  public static HibernateConnector getInstance(final String persistenceUnit) {
    if (instance == null) {
      EntityManagerFactory entityManagerFactory = Persistence.createEntityManagerFactory
          ("DEFAULT_LOCAL");
      instance = new HibernateConnector(persistenceUnit);
    }
    return instance;
  }

  @Override
  public EntityManager getEntityManager() {
    return null;
  }

  @Override
  public EntityTransaction getCurrentTransaction() {
    return null;
  }

  @Override
  public void clearEntityManager() {

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
  @UnitOfWork
  @Transactional
  public <M extends PersistedModel, F extends Filter> List<M> find(Class<M> modelClass, F filter) {
    Transaction tx = sessionFactory.getCurrentSession().beginTransaction();
    List<M> data = sessionFactory.getCurrentSession().createCriteria(modelClass).list();
    tx.commit();
    return data;
  }

  @Override
  public <M extends PersistedModel> M findById(Class<M> modelClass, Filter filter, Serializable id) {
    Transaction tx = sessionFactory.getCurrentSession().beginTransaction();
    M model = sessionFactory.getCurrentSession().get(modelClass, Integer.valueOf(id.toString()));
    tx.commit();
    return model;
  }

  @Override
  public <M extends PersistedModel, F extends Filter> M findOne(Class<M> modelClass, F filter) {
    return null;
  }

  @Override
  public <M extends PersistedModel> M destroy(M model) {
    Transaction tx = sessionFactory.getCurrentSession().beginTransaction();
//    sessionFactory.getCurrentSession().delete(model);
    tx.commit();
    return model;
  }

  @Override
  public <M extends PersistedModel, F extends Filter> int destroyAll(M model, F filter) {
    return 0;
  }
}
