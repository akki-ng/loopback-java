package com.flipkart.loopback.connector;

import com.flipkart.loopback.configuration.ModelConfiguration;
import com.flipkart.loopback.configuration.manager.ModelConfigurationManager;
import com.flipkart.loopback.connector.mysql.QueryGenerator;
import com.flipkart.loopback.exception.LoopbackException;
import com.flipkart.loopback.filter.Filter;
import com.flipkart.loopback.filter.WhereFilter;
import com.flipkart.loopback.model.PersistedModel;
import com.google.common.collect.Lists;
import java.io.Serializable;
import java.util.List;
import java.util.Map;

/**
 * Created by akshaya.sharma on 06/03/18
 */

public class MysqlConnector implements Connector {
  @Override
  public <M extends PersistedModel, F extends Filter> int count(Class<M> modelClass, F filter) {
    return 10;
  }

  @Override
  public <M extends PersistedModel> M create(M model) {
    return model;
  }

  @Override
  public <M extends PersistedModel> List<M> create(List<? extends PersistedModel> models) {
    return null;
  }

  @Override
  public <M extends PersistedModel> M updateOrCreate(M model) {
    return model;
  }

  @Override
  public <M extends PersistedModel> M patchOrCreateWithWhere(M model, Map<String, Object> data) {
    return null;
  }

  @Override
  public <M extends PersistedModel, W extends WhereFilter> M upsertWithWhere(Class<M> modelClass, W where,
                                                               Map<String,
        Object> data) {
    return null;
  }

  @Override
  public <M extends PersistedModel, F extends Filter> M findOrCreate(Class<M> modelClass, F filter, Map<String, Object
        > data) {
    return null;
  }

  @Override
  public <M extends PersistedModel> M save(M model) {
    return model;
  }

  @Override
  public <M extends PersistedModel, W extends WhereFilter> int updateAll(Class<M> modelClass, W filter, Map<String,
      Object>
      data) {
    try {
      return 10;
    }catch (Exception ex) {
      ex.printStackTrace();
    }
    return 0;
  }

  @Override
  public <M extends PersistedModel, F extends Filter> M updateAttributes(M model, F filter, Map<String, Object> data) {
    return null;
  }

  @Override
  public <M extends PersistedModel> M replaceById(M model, Object id) {
    try {
      return (M) model.setAttribute(model.getIdPropertyName(), id);
    } catch (LoopbackException e) {
      e.printStackTrace();
    }
    return null;
  }

  @Override
  public <M extends PersistedModel> M replaceOrCreate(M model) {
    return model;
  }

  @Override
  public <M extends PersistedModel> boolean exists(Class<M> modelClass, Object id) {
    return false;
  }

  @Override
  public <M extends PersistedModel, F extends Filter> List<M> find(Class<M> modelClass, F filter) {
    try {
      ModelConfiguration configuration = ModelConfigurationManager.getInstance()
          .getModelConfiguration(modelClass);
      String selectSql = QueryGenerator.getInstance().getSelectQuery(modelClass, (Filter) filter);
      return Lists.newArrayList(modelClass.newInstance());
    }catch (Exception ex) {
      ex.printStackTrace();
    }
    return null;
  }

  @Override
  public <M extends PersistedModel> M findById(Class<M> modelClass, Filter filter, Serializable id) {
    try {
      M model = modelClass.newInstance();
      return model;
    } catch (InstantiationException e) {
      e.printStackTrace();
    } catch (IllegalAccessException e) {
      e.printStackTrace();
    }
    return null;
  }

  @Override
  public <M extends PersistedModel, F extends Filter> M findOne(Class<M> modelClass, F filter) {
    try {
      return modelClass.newInstance();
    } catch (InstantiationException e) {
      e.printStackTrace();
    } catch (IllegalAccessException e) {
      e.printStackTrace();
    }
    return null;
  }

  @Override
  public <M extends PersistedModel> M destroy(M model) {
    return model;
  }

  @Override
  public <M extends PersistedModel, F extends Filter> int destroyAll(M model, F filter) {
    return 0;
  }

}
