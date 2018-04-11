package com.flipkart.loopback.model.provider;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.flipkart.loopback.configuration.ModelConfiguration;
import com.flipkart.loopback.configuration.manager.ModelConfigurationManager;
import com.flipkart.loopback.connector.Connector;
import com.flipkart.loopback.exception.external.ConnectorException;
import com.flipkart.loopback.exception.model.CouldNotPerformException;
import com.flipkart.loopback.exception.model.OperationNotAllowedException;
import com.flipkart.loopback.exception.model.persistence.ModelNotFoundException;
import com.flipkart.loopback.filter.Filter;
import com.flipkart.loopback.filter.WhereFilter;
import com.flipkart.loopback.model.Model;
import com.flipkart.loopback.model.PersistedModel;
import com.flipkart.loopback.relation.RelatedThroughEntity;
import com.flipkart.loopback.relation.Relation;
import java.io.Serializable;
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

  public <M extends PersistedModel> ModelConfiguration getConfigurationFor(Class<M> modelClass) {
    return getConfigurationManager().getModelConfiguration(modelClass);
  }

  public <M extends PersistedModel> Connector getConnectorFor(Class<M> modelClass) {
    return getConfigurationFor(modelClass).getConnector();
  }

  public <M extends PersistedModel, W extends WhereFilter> long count(Class<M> modelClass,
      W where) {
    try {
      return getConnectorFor(modelClass).count(modelClass, where);
    } catch (RuntimeException e) {
      if (!e.getClass().isAssignableFrom(ConnectorException.class)) {
        e = new ConnectorException(e);
      }
      throw e;
    }
  }

  public <M extends PersistedModel> M create(M model) throws CouldNotPerformException {
    if (model == null) {
      throw new CouldNotPerformException(PersistedModel.class, "create",
          new NullPointerException("model is null"));
    }
    try {
      return getConnectorFor(model.getClass()).create(model);
    } catch (RuntimeException e) {
      if (!e.getClass().isAssignableFrom(ConnectorException.class)) {
        e = new ConnectorException(e);
      }
      throw e;
    }
  }

  public <M extends PersistedModel> List<M> create(List<M> models) throws CouldNotPerformException {
    if (models == null && models.size() == 0) {
      throw new CouldNotPerformException(PersistedModel.class, "create",
          new NullPointerException("models are null or empty"));
    }
    Class<? extends PersistedModel> modelClass = models.get(0).getClass();

    try {
      return getConnectorFor(modelClass).create(models);
    } catch (RuntimeException e) {
      if (!e.getClass().isAssignableFrom(ConnectorException.class)) {
        e = new ConnectorException(e);
      }
      throw e;
    }
  }

  public <M extends PersistedModel> M updateOrCreate(Class<M> modelClass,
      Map<String, Serializable> patchData) throws CouldNotPerformException {
    ModelConfiguration configuration = Model.getConfiguration(modelClass);

    String idPropertyName = configuration.getIdPropertyName();
    if (patchData.containsKey(idPropertyName) && patchData.get(idPropertyName) != null) {
      // Id exists
      Filter idFilter = new Filter();
      idFilter.setWhere(PersistedModel.createIDFilter(modelClass, patchData.get(idPropertyName)));
      try {
        M model = findOne(modelClass, idFilter);
        return (M) model.updateAttributes(patchData);
      } catch (ModelNotFoundException e) {
        // continue create
      }
    }

    // Try create
    ObjectMapper mapper = new ObjectMapper();
    M model = mapper.convertValue(patchData, modelClass);
    return create(model);
  }

  public <M extends PersistedModel> long patchMultipleWithWhere(Class<M> modelClass,
      WhereFilter where, Map<String, Object> data) {
    try {
      return getConnectorFor(modelClass).patchMultipleWithWhere(modelClass, where, data);
    } catch (RuntimeException e) {
      if (!e.getClass().isAssignableFrom(ConnectorException.class)) {
        e = new ConnectorException(e);
      }
      throw e;
    }
  }

  public <M extends PersistedModel, W extends WhereFilter> M upsertWithWhere(Class<M> modelClass,
      W where,
      Map<String, Object> data) throws OperationNotAllowedException, CouldNotPerformException {
    long count = getConnectorFor(modelClass).count(modelClass, where);
    if (count > 1) {
      // Abort if multiple found
      throw new OperationNotAllowedException(modelClass, "upsertWithWhere",
          " more than one " + "entity exists. Aborting!");
    } else if (count == 1) {
      // update
      try {
        Filter filter = new Filter();
        filter.setWhere(where);
        M model = findOne(modelClass, filter);
        model = (M) model.setAttributes(data);
        model = replaceById(model, model.getId());
        return model;
      } catch (ModelNotFoundException e) {
        // continue create
      }
    }
    // create
    ObjectMapper mapper = new ObjectMapper();
    M model = (M) mapper.convertValue(data, modelClass);
    return create(model);
  }

  public <M extends PersistedModel, F extends Filter> M findOrCreate(Class<M> modelClass, F filter,
      Map<String, Object> data) throws CouldNotPerformException, OperationNotAllowedException {
    try {
      return findOne(modelClass, filter);
    } catch (ModelNotFoundException e) {
      return upsertWithWhere(modelClass, filter.getWhere(), data);
    }
  }

  public <M extends PersistedModel, W extends WhereFilter> long updateAll(Class<M> modelClass,
      W where, Map<String, Object> data) {
    return getConnectorFor(modelClass).updateAll(modelClass, where, data);
  }

  public <M extends PersistedModel> M replace(M model) {
//    model = (M) model.setAttribute(model.getIdPropertyName(), id);
    return getConnectorFor(model.getClass()).replaceById(model, model.getId());
  }

  public <M extends PersistedModel> M replaceById(M model, Serializable id) throws CouldNotPerformException, ModelNotFoundException {
//    model = (M) model.setAttribute(model.getIdPropertyName(), id);
    if (model == null) {
      throw new CouldNotPerformException(PersistedModel.class, "replaceOrCreate",
          new NullPointerException("model is null"));
    }
    M persisted = (M) findById(model.getClass(), model.getId());
    return replace(model);
  }

  public <M extends PersistedModel> M replaceOrCreate(
      M model) throws CouldNotPerformException {
    if (model == null) {
      throw new CouldNotPerformException(PersistedModel.class, "replaceOrCreate",
          new NullPointerException("model is null"));
    }
    try {
      M persisted = (M) findById(model.getClass(), model.getId());
      return replaceById(model, persisted.getId());
    } catch (ModelNotFoundException e) {
      // Continue create
    }
    model.setAttribute(model.getIdPropertyName(), null);
    return create(model);
  }


  public <M extends PersistedModel> boolean exists(Class<M> modelClass, Serializable id) {
    return getConnectorFor(modelClass).exists(modelClass, id);
  }

  public <M extends PersistedModel, F extends Filter> List<M> find(Class<M> modelClass, F filter) {
    return getConnectorFor(modelClass).find(modelClass, filter);
  }

  public <M extends PersistedModel, F extends Filter> M findOne(Class<M> modelClass,
      F filter) throws ModelNotFoundException {
    return getConnectorFor(modelClass).findOne(modelClass, filter);
  }

  public <M extends PersistedModel, F extends Filter> M findById(Class<M> modelClass,
      Serializable id) throws ModelNotFoundException {
    Filter idFilter = new Filter();
    idFilter.setWhere(PersistedModel.createIDFilter(modelClass, id));
    return (M) findOne(modelClass, idFilter);
  }

  public <M extends PersistedModel, W extends WhereFilter> long destroyAll(Class<M> modelClass,
      W where) {
    return getConnectorFor(modelClass).destroyAll(modelClass, where);
  }

  public <M extends PersistedModel> M destroy(M model) {
    return getConnectorFor(model.getClass()).destroy(model);
  }

  public <M extends PersistedModel> List<RelatedThroughEntity> findThroughRelatedEntities(WhereFilter relationScope, Relation relation,
      Filter throughFilter, Filter toFilter) {
    return getConnectorFor(relation.getThroughModelClass()).findThroughRelatedEntities(relationScope, relation,
        throughFilter, toFilter);
  }
}
