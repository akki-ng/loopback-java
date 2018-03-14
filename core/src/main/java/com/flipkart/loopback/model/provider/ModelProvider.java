package com.flipkart.loopback.model.provider;

import com.flipkart.loopback.configuration.ModelConfiguration;
import com.flipkart.loopback.configuration.manager.ModelConfigurationManager;
import com.flipkart.loopback.connector.Connector;
import com.flipkart.loopback.exception.LoopbackException;
import com.flipkart.loopback.filter.Filter;
import com.flipkart.loopback.filter.WhereFilter;
import com.flipkart.loopback.model.Model;
import com.flipkart.loopback.model.PersistedModel;
import java.io.Serializable;
import java.lang.reflect.InvocationTargetException;
import java.util.List;
import java.util.Map;
import lombok.Getter;

/**
 * Created by akshaya.sharma on 10/03/18
 */

public class ModelProvider {
  private static ModelProvider instance;

  public static ModelProvider getInstance() {
    if (instance == null) {
      instance = new ModelProvider(ModelConfigurationManager.getInstance());
    }
    return instance;
  }

  @Getter
  private final ModelConfigurationManager configurationManager;

  private ModelProvider(ModelConfigurationManager configurationManager) {
    this.configurationManager = configurationManager;
  }

  public <M extends Model> ModelConfiguration getConfigurationFor
      (Class<M> modelClass) {
    return getConfigurationManager().getModelConfiguration(modelClass);
  }

  public <M extends Model> Connector getConnectorFor(Class<M> modelClass) {
    return getConfigurationFor(modelClass).getConnector();
  }

  public <M extends PersistedModel, F extends Filter> long count(Class<M> modelClass, F filter) {
    return getConnectorFor(modelClass).count(modelClass, filter);
  }

  public <M extends PersistedModel> M create(M model) {
    return getConnectorFor(model.getClass()).create(model);
  }

  public <M extends PersistedModel> List<M> create(List<M> models) {
    if(models.size() > 0) {
      return getConnectorFor(models.get(0).getClass()).create(models);
    }
    return models;
  }

//  public <M extends PersistedModel> M updateOrCreate(Map<String, Object> data) {
//    return getConnectorFor(model.getClass()).updateOrCreate(model);
//  }

  public <M extends PersistedModel> int patchMultipleWithWhere(Class<M> modelClass, WhereFilter
      where, Map<String, Object> data) {
    return getConnectorFor(modelClass).patchMultipleWithWhere(modelClass, where, data);
  }

  public <M extends PersistedModel, F extends WhereFilter> M upsertWithWhere(Class<M> modelClass,
                                                                             F filter, Map<String,
      Object>
                                                                                 data) {
    return getConnectorFor(modelClass).upsertWithWhere(modelClass, filter, data);
  }

  public <M extends PersistedModel, F extends Filter> M findOrCreate(Class<M> modelClass, F
      filter, Map<String, Object> data) {
    M model = null;

    try {
        model = getConnectorFor(modelClass).findOne(modelClass, filter);
        if(model == null) {
          model = modelClass.getConstructor().newInstance();
          model = (M) model.setAttributes(data);
          model = M.create(model);
        }
      } catch (InstantiationException e) {
        e.printStackTrace();
      } catch (IllegalAccessException e) {
        e.printStackTrace();
      } catch (InvocationTargetException e) {
        e.printStackTrace();
      } catch (NoSuchMethodException e) {
        e.printStackTrace();
      } catch (LoopbackException e) {
      e.printStackTrace();
    }
    return model;
  }

  public <M extends PersistedModel, W extends WhereFilter> int updateAll(Class<M> modelClass, W
      where, Map<String, Object> data) {
    return getConnectorFor(modelClass).updateAll(modelClass, where, data);
  }


  public <M extends PersistedModel> M replaceById(M model, Object id) {
    return getConnectorFor(model.getClass()).replaceById(model, id);
  }

  public <M extends PersistedModel> M replaceOrCreate(M model) {
    M persisted = (M) getConnectorFor(model.getClass()).findById(model.getClass(), null, model
        .getId());
    try {
      if(persisted != null) {
        persisted = getConnectorFor(model.getClass()).replaceById(model, persisted.getId());
      }else {
        model.setAttribute(model.getIdPropertyName(), null);
        persisted = getConnectorFor(model.getClass()).create(model);
      }
    }catch (Throwable e) {
      e.printStackTrace();
    }
    return persisted;
  }


  public <M extends PersistedModel> boolean exists(Class<M> modelClass, Object id) {
    return getConnectorFor(modelClass).exists(modelClass, id);
  }

  public <M extends PersistedModel, F extends Filter> List<M> find(Class<M> modelClass, F
      filter) {
    return getConnectorFor(modelClass).find(modelClass, filter);
  }

  public <M extends PersistedModel> M findById(Class<M> modelClass, Filter filter, Serializable id) {
    return getConnectorFor(modelClass).findById(modelClass, filter, id);
  }

  public <M extends PersistedModel, F extends Filter> M findOne(Class<M> modelClass, F
      filter) {
    return getConnectorFor(modelClass).findOne(modelClass, filter);
  }

  public <M extends PersistedModel, F extends Filter> int destroyAll(M model, F filter) {
    return getConnectorFor(model.getClass()).destroyAll(model, filter);
  }

  public <M extends PersistedModel> M destroy(M model) {
    return getConnectorFor(model.getClass()).destroy(model);
  }
}
