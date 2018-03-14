package com.flipkart.loopback.connector;

import com.flipkart.loopback.filter.Filter;
import com.flipkart.loopback.filter.WhereFilter;
import com.flipkart.loopback.model.PersistedModel;
import java.io.Serializable;
import java.util.List;
import java.util.Map;
import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;
import lombok.Getter;

/**
 * Created by akshaya.sharma on 02/03/18
 */

public abstract class Connector {
  public abstract EntityManager getEntityManager();
  public abstract EntityTransaction getCurrentTransaction();
  public abstract void clearEntityManager();

  @Getter
  protected String identifier;

  protected Connector(String identifier) {
    this.identifier = identifier;
  }

//  public abstract IDType getDefaultIdType() {
//    return IDType.NUMBER;
//  }

  public abstract <M extends PersistedModel, W extends WhereFilter>  long count(Class<M> modelClass, W
      where);

  public abstract <M extends PersistedModel> M create(M model);

  public abstract <M extends PersistedModel> List<M> create(List<M> models);

//  public abstract <M extends PersistedModel> M updateOrCreate(M model);
//  Handled at provider

  public abstract <M extends PersistedModel> long patchMultipleWithWhere(Class<M> modelClass,
                                                                      WhereFilter
      where, Map<String, Object> data);


  public abstract <M extends PersistedModel, W extends WhereFilter> long updateAll(Class<M> modelClass, W filter, Map<String, Object>
      data);

  public abstract <M extends PersistedModel, F extends Filter> M updateAttributes(M model, F filter, Map<String, Object>
      data);

  public abstract <M extends PersistedModel> M replaceById(M model, Object id);


  public abstract <M extends PersistedModel> boolean exists(Class<M> modelClass, Object id);

  public abstract <M extends PersistedModel, F extends Filter> List<M> find(Class<M> modelClass, F filter);

  public abstract <M extends PersistedModel> M findById(Class<M> modelClass, Filter filter, Serializable id);

  public abstract <M extends PersistedModel, F extends Filter> M findOne(Class<M> modelClass, F filter);




  public abstract <M extends PersistedModel> M destroy(M model);

  public abstract <M extends PersistedModel, F extends Filter> long destroyAll(M model, F filter);
}
