package com.flipkart.loopback.connector;

import com.flipkart.loopback.constants.IDType;
import com.flipkart.loopback.filter.Filter;
import com.flipkart.loopback.filter.WhereFilter;
import com.flipkart.loopback.model.Model;
import com.flipkart.loopback.model.PersistedModel;
import java.util.List;
import java.util.Map;

/**
 * Created by akshaya.sharma on 02/03/18
 */

public interface Connector {
  public default IDType getDefaultIdType() {
    return IDType.NUMBER;
  }

  <M extends Model, F extends Filter>  int count(Class<M> modelClass, F filter);

  <M extends Model> M create(M model);

  <M extends Model> List<M> create(List<? extends Model> models);

  <M extends Model> M updateOrCreate(M model);

  <M extends Model> M patchOrCreateWithWhere(M model, Map<String, Object> data);

  <M extends Model, W extends WhereFilter> M upsertWithWhere(Class<M> modelClass, W filter,
                                                             Map<String, Object>
      data);

  <M extends Model, F extends Filter> M findOrCreate(Class<M> modelClass, F filter, Map<String, Object>
      data);

  <M extends Model> M save(M model);

  <M extends Model, W extends WhereFilter> int updateAll(M model, W filter, Map<String, Object>
      data);

  <M extends Model, F extends Filter> M updateAttributes(M model, F filter, Map<String, Object>
      data);

  <M extends PersistedModel> M replaceById(M model, Object id);

  <M extends Model> M replaceOrCreate(M model);


  <M extends Model> boolean exists(Class<M> modelClass, Object id);

  <M extends Model, F extends Filter> List<M> find(Class<M> modelClass, F filter);

  <M extends Model> M findById(Class<M> modelClass, Object id);

  <M extends Model, F extends Filter> M findOne(Class<M> modelClass, F filter);




  <M extends Model> boolean destroy(M model, Object id);

  <M extends Model, F extends Filter> int destroyAll(M model, F filter);

  <M extends Model> void destroyById(Class<M> modelClass, Object id);
}
