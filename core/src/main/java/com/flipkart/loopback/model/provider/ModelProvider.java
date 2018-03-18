package com.flipkart.loopback.model.provider;

import com.flipkart.loopback.configuration.ModelConfiguration;
import com.flipkart.loopback.configuration.manager.ModelConfigurationManager;
import com.flipkart.loopback.connector.Connector;
import com.flipkart.loopback.exception.ConnectorException;
import com.flipkart.loopback.exception.ConnectorNotFoundException;
import com.flipkart.loopback.exception.CouldNotPerformException;
import com.flipkart.loopback.exception.IdFieldNotFoundException;
import com.flipkart.loopback.exception.InternalError;
import com.flipkart.loopback.exception.InvalidFilterException;
import com.flipkart.loopback.exception.InvalidPropertyValueException;
import com.flipkart.loopback.exception.ModelNotConfiguredException;
import com.flipkart.loopback.exception.ModelNotFoundException;
import com.flipkart.loopback.exception.OperationNotAllowedException;
import com.flipkart.loopback.filter.Filter;
import com.flipkart.loopback.filter.WhereFilter;
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

  public <M extends PersistedModel> ModelConfiguration getConfigurationFor(
      Class<M> modelClass) throws ModelNotConfiguredException {
    return getConfigurationManager().getModelConfiguration(modelClass);
  }

  public <M extends PersistedModel> Connector getConnectorFor(
      Class<M> modelClass) throws ConnectorNotFoundException, ModelNotConfiguredException {
    return getConfigurationFor(modelClass).getConnector();
  }

  public <M extends PersistedModel, W extends WhereFilter> long count(Class<M> modelClass,
      W where) throws ConnectorNotFoundException, ModelNotConfiguredException, ConnectorException {
    return getConnectorFor(modelClass).count(modelClass, where);
  }

  public <M extends PersistedModel> M create(
      M model) throws ConnectorNotFoundException, ModelNotConfiguredException, ConnectorException {
    return getConnectorFor(model.getClass()).create(model);
  }

  public <M extends PersistedModel> List<M> create(
      List<M> models) throws ConnectorNotFoundException, ModelNotConfiguredException,
      ConnectorException {
    if (models.size() > 0) {
      return getConnectorFor(models.get(0).getClass()).create(models);
    }
    return models;
  }

//  public <M extends PersistedModel> M updateOrCreate(Map<String, Object> data) {
//    return getConnectorFor(model.getClass()).updateOrCreate(model);
//  }

  public <M extends PersistedModel> long patchMultipleWithWhere(Class<M> modelClass,
      WhereFilter where,
      Map<String, Object> data) throws ConnectorNotFoundException, ModelNotConfiguredException,
      ConnectorException {
    return getConnectorFor(modelClass).patchMultipleWithWhere(modelClass, where, data);
  }

  public <M extends PersistedModel, W extends WhereFilter> M upsertWithWhere(Class<M> modelClass,
      W where,
      Map<String, Object> data) throws ConnectorNotFoundException, ModelNotConfiguredException,
      ConnectorException, OperationNotAllowedException, CouldNotPerformException, InternalError {
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
        model.setAttributes(data);
        model = getConnectorFor(modelClass).replaceById(model, model.getId());
        return model;
      } catch (InvalidFilterException | ModelNotFoundException e) {
        e.printStackTrace();
        throw new CouldNotPerformException(modelClass, "upsertWithWhere", e);
      } catch (IdFieldNotFoundException | InvalidPropertyValueException e) {
        e.printStackTrace();
        throw new OperationNotAllowedException(modelClass, "upsertWithWhere", e.getMessage());
      }
    } else {
      // create
      try {
        M model = modelClass.getConstructor().newInstance();
        model = (M) model.setAttributes(data);
        model = M.create(model);
        return model;
      } catch (InstantiationException | InvocationTargetException | NoSuchMethodException |
          IllegalAccessException e) {
        e.printStackTrace();
        throw new CouldNotPerformException(modelClass, "upsertWithWhere", e);
      } catch (IdFieldNotFoundException | InvalidPropertyValueException e) {
        e.printStackTrace();
        throw new OperationNotAllowedException(modelClass, "upsertWithWhere", e.getMessage());
      }
    }
  }

  public <M extends PersistedModel, F extends Filter> M findOrCreate(Class<M> modelClass, F filter,
      Map<String, Object> data) throws ConnectorNotFoundException, ModelNotConfiguredException,
      ConnectorException, CouldNotPerformException, OperationNotAllowedException, InternalError {
    try {
      try {
        M model = findOne(modelClass, filter);
        return model;
      } catch (ModelNotFoundException e) {
        e.printStackTrace();
        M model = modelClass.getConstructor().newInstance();
        model = (M) model.setAttributes(data);
        model = M.create(model);
        return model;
      }
    } catch (InstantiationException | InvocationTargetException | NoSuchMethodException |
        IllegalAccessException e) {
      e.printStackTrace();
      throw new CouldNotPerformException(modelClass, "findOrCreate", e);
    } catch (IdFieldNotFoundException | InvalidPropertyValueException e) {
      e.printStackTrace();
      throw new OperationNotAllowedException(modelClass, "upsertWithWhere", e.getMessage());
    }
  }

  public <M extends PersistedModel, W extends WhereFilter> long updateAll(Class<M> modelClass,
      W where,
      Map<String, Object> data) throws ConnectorNotFoundException, ModelNotConfiguredException,
      ConnectorException {
    return getConnectorFor(modelClass).updateAll(modelClass, where, data);
  }


  public <M extends PersistedModel> M replaceById(M model,
      Serializable id) throws ConnectorNotFoundException, ModelNotConfiguredException,
      ConnectorException {
    return getConnectorFor(model.getClass()).replaceById(model, id);
  }

  public <M extends PersistedModel> M replaceOrCreate(
      M model) throws ConnectorNotFoundException, ModelNotConfiguredException,
      ConnectorException, CouldNotPerformException, InternalError {
    try {
      try {
        M persisted = (M) findById(model.getClass(), null, model.getId());
        persisted = getConnectorFor(model.getClass()).replaceById(model, persisted.getId());
        return persisted;
      } catch (ModelNotFoundException e) {
        e.printStackTrace();
        model.setAttribute(model.getIdPropertyName(), null);
        M persisted = getConnectorFor(model.getClass()).create(model);
        return persisted;
      }
    } catch (InvalidPropertyValueException e) {
      e.printStackTrace();
      throw new CouldNotPerformException(model.getClass(), "replaceOrCreate", e);
    }
  }


  public <M extends PersistedModel> boolean exists(Class<M> modelClass,
      Serializable id) throws ConnectorNotFoundException, ModelNotConfiguredException,
      ConnectorException {
    return getConnectorFor(modelClass).exists(modelClass, id);
  }

  public <M extends PersistedModel, F extends Filter> List<M> find(Class<M> modelClass,
      F filter) throws ConnectorNotFoundException, ModelNotConfiguredException, ConnectorException {
    return getConnectorFor(modelClass).find(modelClass, filter);
  }

  public <M extends PersistedModel> M findById(Class<M> modelClass, Filter filter,
      Serializable id) throws ConnectorNotFoundException, ModelNotConfiguredException,
      ConnectorException, ModelNotFoundException {
    M model = getConnectorFor(modelClass).findById(modelClass, filter, id);
    if (model == null) {
      throw new ModelNotFoundException(modelClass, id);
    }
    return model;
  }

  public <M extends PersistedModel, F extends Filter> M findOne(Class<M> modelClass,
      F filter) throws ConnectorNotFoundException, ModelNotConfiguredException,
      ConnectorException, ModelNotFoundException {
    M model = getConnectorFor(modelClass).findOne(modelClass, filter);
    if (model == null) {
      throw new ModelNotFoundException(modelClass, filter.getWhere().toString());
    }
    return model;
  }

  public <M extends PersistedModel, W extends WhereFilter> long destroyAll(Class<M> modelClass,
      W where) throws ConnectorNotFoundException, ModelNotConfiguredException, ConnectorException {
    return getConnectorFor(modelClass).destroyAll(modelClass, where);
  }

  public <M extends PersistedModel> M destroy(
      M model) throws ConnectorNotFoundException, ModelNotConfiguredException, ConnectorException {
    return getConnectorFor(model.getClass()).destroy(model);
  }
}
