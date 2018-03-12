package com.flipkart.loopback.connector;

import com.flipkart.loopback.constants.IDType;
import com.flipkart.loopback.filter.Filter;
import com.flipkart.loopback.filter.WhereFilter;
import com.flipkart.loopback.model.PersistedModel;
import io.dropwizard.hibernate.UnitOfWork;
import java.io.Serializable;
import java.util.List;
import java.util.Map;

/**
 * Created by akshaya.sharma on 02/03/18
 */

public interface Connector {
  public default IDType getDefaultIdType() {
    return IDType.NUMBER;
  }

  <M extends PersistedModel, F extends Filter>  int count(Class<M> modelClass, F filter);

  <M extends PersistedModel> M create(M model);

  <M extends PersistedModel> List<M> create(List<? extends PersistedModel> models);

  <M extends PersistedModel> M updateOrCreate(M model);

  <M extends PersistedModel> M patchOrCreateWithWhere(M model, Map<String, Object> data);

  <M extends PersistedModel, W extends WhereFilter> M upsertWithWhere(Class<M> modelClass, W filter,
                                                             Map<String, Object>
      data);

  <M extends PersistedModel, F extends Filter> M findOrCreate(Class<M> modelClass, F filter, Map<String, Object>
      data);

  <M extends PersistedModel> M save(M model);

  <M extends PersistedModel, W extends WhereFilter> int updateAll(Class<M> modelClass, W filter, Map<String, Object>
      data);

  <M extends PersistedModel, F extends Filter> M updateAttributes(M model, F filter, Map<String, Object>
      data);

  <M extends PersistedModel> M replaceById(M model, Object id);

  <M extends PersistedModel> M replaceOrCreate(M model);


  <M extends PersistedModel> boolean exists(Class<M> modelClass, Object id);

  @UnitOfWork
  <M extends PersistedModel, F extends Filter> List<M> find(Class<M> modelClass, F filter);

  <M extends PersistedModel> M findById(Class<M> modelClass, Filter filter, Serializable id);

  <M extends PersistedModel, F extends Filter> M findOne(Class<M> modelClass, F filter);




  <M extends PersistedModel> M destroy(M model);

  <M extends PersistedModel, F extends Filter> int destroyAll(M model, F filter);
}
