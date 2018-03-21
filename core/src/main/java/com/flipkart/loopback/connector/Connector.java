package com.flipkart.loopback.connector;

import com.flipkart.loopback.exception.external.ConnectorException;
import com.flipkart.loopback.exception.model.persistence.ModelNotFoundException;
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
      where) throws ConnectorException;

  public abstract <M extends PersistedModel> M create(M model) throws ConnectorException;

  public abstract <M extends PersistedModel> M replaceById(M model, Serializable id) throws ConnectorException;

  public abstract <M extends PersistedModel> List<M> create(List<M> models) throws ConnectorException;

//  public abstract <M extends PersistedModel> M updateOrCreate(M model);
//  Handled at provider

  public abstract <M extends PersistedModel> long patchMultipleWithWhere(Class<M> modelClass,
                                                                      WhereFilter
      where, Map<String, Object> data) throws ConnectorException;


  public abstract <M extends PersistedModel, W extends WhereFilter> long updateAll(Class<M> modelClass, W filter, Map<String, Object>
      data) throws ConnectorException;

  public abstract <M extends PersistedModel> boolean exists(Class<M> modelClass, Serializable id) throws
      ConnectorException;

  public abstract <M extends PersistedModel, F extends Filter> List<M> find(Class<M> modelClass, F filter) throws ConnectorException;

  public abstract <M extends PersistedModel, F extends Filter> M findOne(Class<M> modelClass, F
      filter) throws ConnectorException, ModelNotFoundException;

  public abstract <M extends PersistedModel> M destroy(M model) throws ConnectorException;

  public abstract <M extends PersistedModel, W extends WhereFilter> long destroyAll(Class<M> modelClass, W
      where) throws ConnectorException;


  /*
  Relation specific
   */


}
