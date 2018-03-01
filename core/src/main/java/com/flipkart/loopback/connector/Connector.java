package com.flipkart.loopback.connector;

import com.flipkart.loopback.constants.IDType;
import com.flipkart.loopback.filter.Filter;
import com.flipkart.loopback.model.Model;
import java.util.List;
import java.util.Map;

/**
 * Created by akshaya.sharma on 02/03/18
 */

public interface Connector {
  public default IDType getDefaultIdType() {
    return IDType.NUMBER;
  }

  <M extends Model, F extends Filter>  int count(M model, F filter);

  <M extends Model> M create(M model);

  <M extends Model> List<M> create(List<? extends Model> models);

  <M extends Model> M updateOrCreate(M model);

  <M extends Model> M patchOrCreateWithWhere(M model, Map<String, Object> data);

  <M extends Model, F extends Filter> M upsertWithWhere(M model, F filter, Map<String, Object>
      data);

  <M extends Model, F extends Filter> M findOrCreate(M model, F filter, Map<String, Object>
      data);

  <M extends Model> M save(M model);

  <M extends Model, F extends Filter> List<M> updateAll(M model, F filter, Map<String, Object>
      data);

  <M extends Model, F extends Filter> M updateAttributes(M model, F filter, Map<String, Object>
      data);

  <M extends Model> boolean replaceById(M model, Object id);

  <M extends Model> boolean replaceOrCreate(M model, Object id);


  <M extends Model> boolean exists(M model, Object id);

  <M extends Model, F extends Filter> List<M> find(M model, F filter);

  <M extends Model> M findById(M model, Object id);

  <M extends Model, F extends Filter> M findOne(M model, F filter);




  <M extends Model> boolean destroy(M model, Object id);

  <M extends Model, F extends Filter> int destroyAll(M model, F filter);

  <M extends Model> int destroyById(M model, Object id);
}
