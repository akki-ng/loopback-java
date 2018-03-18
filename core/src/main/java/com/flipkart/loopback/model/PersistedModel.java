package com.flipkart.loopback.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.flipkart.loopback.annotation.Transaction;
import com.flipkart.loopback.configuration.ModelConfiguration;
import com.flipkart.loopback.configuration.manager.ModelConfigurationManager;
import com.flipkart.loopback.connector.Connector;
import com.flipkart.loopback.constants.IDType;
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
import com.flipkart.loopback.exception.PropertyNotFoundException;
import com.flipkart.loopback.exception.ReadOnlyPropertyException;
import com.flipkart.loopback.exception.RelationNotFound;
import com.flipkart.loopback.exception.TransientPropertyException;
import com.flipkart.loopback.filter.Filter;
import com.flipkart.loopback.filter.WhereFilter;
import com.flipkart.loopback.relation.RelatedModel;
import com.flipkart.loopback.relation.Relation;
import com.google.common.collect.Maps;
import java.beans.Transient;
import java.io.Serializable;
import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;
import lombok.SneakyThrows;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;

/**
 * Created by akshaya.sharma on 02/03/18
 */

public abstract class PersistedModel<M extends PersistedModel<M, CM>, CM extends
    ModelConfigurationManager> extends Model<M, CM> {

  @JsonIgnore
  protected abstract List<Relation> getRelations();

  protected static <M extends PersistedModel> void beginTransaction(
      @NotNull Class<M> modelClass) throws InternalError {
    Connector connector = getConnector(modelClass);
    EntityManager em = connector.getEntityManager();
    EntityTransaction tx = em.getTransaction();
    boolean newEm = !tx.isActive() || !em.isOpen();
    if (!newEm) {
      em.joinTransaction();
    } else {
      em.getTransaction().begin();
    }
  }

  @JsonIgnore
  public IDType getIDType() throws InternalError {
    return getConfiguration().getIDType();
  }

  @JsonIgnore
  public static <M extends PersistedModel> WhereFilter createIDFilter(Class<M> modelClass,
      Serializable id) throws InternalError, InvalidFilterException, InvalidPropertyValueException {
    ModelConfiguration configuration = getConfiguration(modelClass);
    String idPropertyName = configuration.getIdPropertyName();
    IDType idType = configuration.getIDType();
    if(idType == IDType.NUMBER) {
      try {
        id = Long.parseLong(String.valueOf(id));
        return new WhereFilter("{\"" + configuration.getIdPropertyName() + "\": " + id + "}");
      }catch (NumberFormatException e) {
        throw new InvalidPropertyValueException(modelClass, configuration.getIdPropertyName
            (), String.valueOf(id), " expected a number");
      }
    }else {
      return new WhereFilter(
          "{\"" + configuration.getIdPropertyName() + "\": \"" + id + "\"}");
    }
  }

  protected static <M extends PersistedModel> void commitTransaction(
      @NotNull Class<M> modelClass) throws InternalError {
    Connector connector = getConnector(modelClass);
    EntityManager em = connector.getEntityManager();
    EntityTransaction tx = em.getTransaction();
    boolean newEm = !tx.isActive() || !em.isOpen();

    try {
      if (em.isOpen() && tx.isActive()) {
        tx.commit();
      }
    } catch (Throwable e) {
      if (tx.isActive()) {
        em.getTransaction().rollback();
      }
      throw e;
    } finally {
      if (newEm) {
        connector.clearEntityManager();
        if (em.isOpen()) {
          em.close();
        }
      }
    }
  }

  @JsonIgnore
  public ModelConfiguration getConfiguration() throws InternalError {
    return getConfiguration(this.getClass());
  }

  @Transaction
  public static <M extends PersistedModel, W extends WhereFilter> long count(
      @NotNull Class<M> modelClass, W where) throws InternalError {
    try {
      return getProvider().count(modelClass, where);
    } catch (ConnectorNotFoundException | ModelNotConfiguredException | ConnectorException e) {
      e.printStackTrace();
      throw new InternalError(modelClass, e);
    }
  }

  @Transaction
  public static <M extends PersistedModel> M create(
      @NotNull M model) throws CouldNotPerformException, InternalError {
    try {
      beginTransaction(model.getClass());
      model = getProvider().create(model);
      commitTransaction(model.getClass());
      return model;
    } catch (ConnectorNotFoundException | ConnectorException | ModelNotConfiguredException e) {
      e.printStackTrace();
      throw new InternalError(model.getClass(), e);
    }
  }


  @Transaction
  public static <M extends PersistedModel> List<M> create(
      @NotNull List<M> models) throws InternalError, CouldNotPerformException {
    try {
      if (models != null && models.size() > 0) {
        beginTransaction(models.get(0).getClass());
        models = getProvider().create(models);
        commitTransaction(models.get(0).getClass());
        return models;
      }
      // TODO - type errasue
      throw new CouldNotPerformException(PersistedModel.class, "create",
          new NullPointerException("models are null or empty"));
    } catch (ConnectorNotFoundException | ConnectorException | ModelNotConfiguredException e) {
      e.printStackTrace();
      throw new InternalError(PersistedModel.class, e);
    }
  }

  @Transaction
  public static <M extends PersistedModel> M updateOrCreate(@NotNull Class<M> modelClass,
      @NotNull Map<String, Object> patchData) throws InternalError, ModelNotFoundException,
      InvalidPropertyValueException, CouldNotPerformException, IdFieldNotFoundException {
    beginTransaction(modelClass);
    ModelConfiguration configuration = getConfiguration(modelClass);
    String idPropertyName = configuration.getIdPropertyName();
    M model = null;
    if (patchData.containsKey(idPropertyName) && patchData.get(idPropertyName) != null) {
      // Id exists
      model = M.findById(modelClass, null, (Serializable) patchData.get(idPropertyName));
      model = (M) model.updateAttributes(patchData);
    } else {
      // Try create
      ObjectMapper mapper = new ObjectMapper();
      model = mapper.convertValue(patchData, modelClass);
      model = M.create(model);
    }
    commitTransaction(modelClass);
    return model;
  }

  @Transaction
  public static <M extends PersistedModel> long patchMultipleWithWhere(@NotNull Class<M> modelClass,
      @NotNull WhereFilter where, @NotNull Map<String, Object> data) throws InternalError {
    try {
      beginTransaction(modelClass);
      long count = getProvider().patchMultipleWithWhere(modelClass, where, data);
      commitTransaction(modelClass);
      return count;
    } catch (ConnectorNotFoundException | ConnectorException | ModelNotConfiguredException e) {
      e.printStackTrace();
      throw new InternalError(modelClass, e);
    }
  }

  @Transaction
  public static <M extends PersistedModel, W extends WhereFilter> M upsertWithWhere(
      @NotNull Class<M> modelClass, @NotNull W where,
      @NotNull Map<String, Object> data) throws InternalError, OperationNotAllowedException,
      CouldNotPerformException {
    try {
      beginTransaction(modelClass);
      M model = getProvider().upsertWithWhere(modelClass, where, data);
      commitTransaction(modelClass);
      return model;
    } catch (ConnectorNotFoundException | ConnectorException | ModelNotConfiguredException e) {
      e.printStackTrace();
      throw new InternalError(modelClass, e);
    }
  }

  @Transaction
  public static <M extends PersistedModel, F extends Filter> M findOrCreate(
      @NotNull Class<M> modelClass, @NotNull F filter,
      @NotNull Map<String, Object> data) throws InternalError, OperationNotAllowedException,
      CouldNotPerformException {
    try {
      beginTransaction(modelClass);
      M model = getProvider().findOrCreate(modelClass, filter, data);
      commitTransaction(modelClass);
      return model;
    } catch (ConnectorNotFoundException | ConnectorException | ModelNotConfiguredException e) {
      e.printStackTrace();
      throw new InternalError(modelClass, e);
    }
  }

  @Transaction
  public static <M extends PersistedModel, W extends WhereFilter> long updateAll(
      Class<M> modelClass, W where, Map<String, Object> data) throws InternalError {
    try {
      beginTransaction(modelClass);
      long count = getProvider().updateAll(modelClass, where, data);
      commitTransaction(modelClass);
      return count;
    } catch (ConnectorNotFoundException | ConnectorException | ModelNotConfiguredException e) {
      e.printStackTrace();
      throw new InternalError(modelClass, e);
    }
  }

  @Transaction
  public static <M extends PersistedModel> M replaceById(M model,
      Serializable id) throws InternalError {
    try {
      beginTransaction(model.getClass());
      model = getProvider().replaceById(model, id);
      commitTransaction(model.getClass());
      return model;
    } catch (ConnectorNotFoundException | ConnectorException | ModelNotConfiguredException e) {
      e.printStackTrace();
      throw new InternalError(model.getClass(), e);
    }
  }

  @Transaction
  public static <M extends PersistedModel> M replaceOrCreate(
      M model) throws CouldNotPerformException, InternalError {
    try {
      beginTransaction(model.getClass());
      model = getProvider().replaceOrCreate(model);
      commitTransaction(model.getClass());
      return model;
    } catch (ConnectorNotFoundException | ConnectorException | ModelNotConfiguredException e) {
      e.printStackTrace();
      throw new InternalError(model.getClass(), e);
    }
  }

  @Transaction
  public static <M extends PersistedModel> boolean exists(Class<M> modelClass,
      Serializable id) throws InternalError {
    try {
      beginTransaction(modelClass);
      boolean exists = getProvider().exists(modelClass, id);
      commitTransaction(modelClass);
      return exists;
    } catch (ConnectorNotFoundException | ConnectorException | ModelNotConfiguredException e) {
      e.printStackTrace();
      throw new InternalError(modelClass, e);
    }
  }

  @Transaction
  public static <M extends PersistedModel, F extends Filter> List<M> find(Class<M> modelClass,
      F filter) throws InternalError {
    try {
      beginTransaction(modelClass);
      List<M> models = getProvider().find(modelClass, filter);
      commitTransaction(modelClass);
      return models;
    } catch (ConnectorNotFoundException | ConnectorException | ModelNotConfiguredException e) {
      e.printStackTrace();
      throw new InternalError(modelClass, e);
    }
  }

  @Transaction
  public static <M extends PersistedModel> M findById(Class<M> modelClass, Filter filter,
      Serializable id) throws ModelNotFoundException, InternalError {
    try {
      beginTransaction(modelClass);
      M model = getProvider().findById(modelClass, filter, id);
      commitTransaction(modelClass);
      return model;
    } catch (ConnectorNotFoundException | ConnectorException | ModelNotConfiguredException e) {
      e.printStackTrace();
      throw new InternalError(modelClass, e);
    }
  }

  @Transaction
  public static <M extends PersistedModel, F extends Filter> M findOne(Class<M> modelClass,
      F filter) throws InternalError, ModelNotFoundException {
    try {
      beginTransaction(modelClass);
      M model = getProvider().findOne(modelClass, filter);
      commitTransaction(modelClass);
      return model;
    } catch (ConnectorNotFoundException | ConnectorException | ModelNotConfiguredException e) {
      e.printStackTrace();
      throw new InternalError(modelClass, e);
    }
  }

  @Transaction
  public static <M extends PersistedModel, W extends WhereFilter> long destroyAll(
      Class<M> modelClass, W where) throws InternalError {
    try {
      beginTransaction(modelClass);
      long count = getProvider().destroyAll(modelClass, where);
      commitTransaction(modelClass);
      return count;
    } catch (ConnectorNotFoundException | ConnectorException | ModelNotConfiguredException e) {
      e.printStackTrace();
      throw new InternalError(modelClass, e);
    }
  }

  @Transaction
  public static <M extends PersistedModel> M destroyById(Class<M> modelClass,
      Serializable id) throws ModelNotFoundException, InternalError {
    beginTransaction(modelClass);
    M model = M.findById(modelClass, null, id);
    model = (M) model.destroy();
    commitTransaction(modelClass);
    return model;
  }

  @Transaction
  public <M extends PersistedModel> M destroy() throws InternalError {
    try {
      return (M) getProvider().destroy(this);
    } catch (ConnectorNotFoundException | ConnectorException | ModelNotConfiguredException e) {
      e.printStackTrace();
      throw new InternalError(this.getClass(), e);
    }
  }

  @JsonIgnore
  public abstract Serializable getId();

  @JsonIgnore
  public String getStringifiedId() {
    return String.valueOf(getId());
  }

  @Transaction
  public <M extends PersistedModel> M save() throws CouldNotPerformException, InternalError {
    try {
      return (M) getProvider().replaceOrCreate(this);
    } catch (ConnectorNotFoundException | ConnectorException | ModelNotConfiguredException e) {
      e.printStackTrace();
      throw new InternalError(this.getClass(), e);
    }
  }

  @Transaction
  public M reload() throws InternalError, ModelNotFoundException {
    try {
      return (M) getProvider().findById(this.getClass(), null, this.getId());
    } catch (ConnectorNotFoundException | ConnectorException | ModelNotConfiguredException e) {
      e.printStackTrace();
      throw new InternalError(this.getClass(), e);
    }
  }

  @JsonIgnore
  public String getIdPropertyName() throws InternalError {
    ModelConfiguration configuration = this.getConfiguration();
    return configuration.getIdPropertyName();
  }

  @JsonIgnore
  public boolean isNewRecord() {
    // TODO -> modelProvider will set
    return false;
  }

  @Transaction
  public <M extends PersistedModel> M updateAttributes(
      Map<String, Serializable> data) throws InvalidPropertyValueException,
      IdFieldNotFoundException, CouldNotPerformException, InternalError {
    return (M) this.setAttributes(data).save();
  }

  public <M extends PersistedModel> M setAttributes(
      Map<String, Serializable> data) throws IdFieldNotFoundException,
      InvalidPropertyValueException, InternalError {
    String idName = this.getIdPropertyName();
    if (StringUtils.isBlank(idName)) {
      // Model requires an id field name
      throw new IdFieldNotFoundException(this.getClass());
    }

    if (data.containsKey(idName) && !getId().toString().equals(data.get(idName).toString())) {
      // Model can not update ID
      throw new ReadOnlyPropertyException(this.getClass(), idName, data.get(idName));
    }

    for (Map.Entry<String, Serializable> e : data.entrySet()) {
      this.setAttribute(e.getKey(), e.getValue());
    }
    return (M) this;
  }

  @Transaction
  private M updateAttribute(String attributeName,
      Serializable attributeValue) throws InvalidPropertyValueException, InternalError, CouldNotPerformException {
    return this.setAttribute(attributeName, attributeValue).save();
  }

  public <F extends Filter> M setAttribute(String attributeName,
      Serializable attributeValue) throws InvalidPropertyValueException {

    for (Field declaredField : this.getClass().getDeclaredFields()) {
      String fieldName = declaredField.getName();
      JsonProperty property = declaredField.getAnnotation(JsonProperty.class);
      if (property != null) {
        fieldName = property.value();
      }

      try {
        Transient aTransient = declaredField.getAnnotation(Transient.class);
        if (aTransient != null && fieldName.equals(attributeName)) {
          throw new TransientPropertyException(this.getClass(), attributeName, attributeValue);
        }

        if (fieldName.equals(attributeName)) {
          boolean accessible = declaredField.isAccessible();
          declaredField.setAccessible(true);
          Class toCast = declaredField.getType();
          if (!toCast.isPrimitive()) {
            if (attributeValue != null) {
              try {
                if (toCast == Integer.class) {
                  attributeValue = new Integer(attributeValue.toString());
                } else if (toCast == Long.class) {
                  attributeValue = new Long(attributeValue.toString());
                } else if (toCast == Float.class) {
                  attributeValue = new Float(attributeValue.toString());
                } else if (toCast == Double.class) {
                  attributeValue = new Double(attributeValue.toString());
                } else if (toCast == String.class) {
                  attributeValue = attributeValue.toString();
                } else {
                  // TODO should throw Exception??
                }
              } catch (Exception e) {
                throw new InvalidPropertyValueException(this.getClass(), attributeName,
                    attributeValue, "expected " + toCast.getSimpleName());
              }
            }
            declaredField.set(this, declaredField.getType().cast(attributeValue));
          } else {
            if ("int".equals(toCast.getName())) {
              int val = 0;
              try {
                val = Integer.parseInt(String.valueOf(attributeValue));
              } catch (Exception e) {
                throw new InvalidPropertyValueException(this.getClass(), attributeName,
                    attributeValue, "expected int");
              }
              declaredField.set(this, val);
            } else if ("long".equals(toCast.getName())) {
              long val = 0L;
              try {
                val = Long.parseLong(String.valueOf(attributeValue));
              } catch (Exception e) {
                throw new InvalidPropertyValueException(this.getClass(), attributeName,
                    attributeValue, "expected long");
              }
              declaredField.set(this, val);
            } else if ("float".equals(toCast.getName())) {
              float val = 0;
              try {
                val = Float.parseFloat(String.valueOf(attributeValue));
              } catch (Exception e) {
                throw new InvalidPropertyValueException(this.getClass(), attributeName,
                    attributeValue, "expected float");
              }
              declaredField.set(this, val);
            } else if ("double".equals(toCast.getName())) {
              double val = 0;
              try {
                val = Double.parseDouble(String.valueOf(attributeValue));
              } catch (Exception e) {
                throw new InvalidPropertyValueException(this.getClass(), attributeName,
                    attributeValue, "expected double");
              }
              declaredField.set(this, val);
            }
          }
          declaredField.setAccessible(accessible);
        }
      } catch (IllegalAccessException e) {
        throw new InvalidPropertyValueException(this.getClass(), attributeName, attributeValue,
            e.getMessage());
      }
    }
    return (M) this;
  }

  @JsonIgnore
  private String getFieldName(Field f) {
    JsonProperty property = f.getAnnotation(JsonProperty.class);
    if (property != null && StringUtils.isNotEmpty(property.value())) {
      return property.value();
    }
    return f.getName();
  }

  @SneakyThrows
  @JsonIgnore
  public Map<String, Object> getFieldMap() {
    Map<String, Object> fieldData = new HashMap<String, Object>();
    for (Field declaredField : this.getClass().getDeclaredFields()) {
      boolean accessible = declaredField.isAccessible();
      declaredField.setAccessible(true);
      Object fieldValue = declaredField.get(this);
      fieldData.put(getFieldName(declaredField), fieldValue);
      declaredField.setAccessible(accessible);
    }
    return fieldData;
  }

  @JsonIgnore
  public Object getFieldValue(Field field) throws IllegalAccessException {
    boolean accessible = field.isAccessible();
    field.setAccessible(true);
    Object value = field.get(this);
    field.setAccessible(accessible);
    return value;
  }

  @JsonIgnore
  public Object getPropertyValue(
      String propertyName) throws ReadOnlyPropertyException, PropertyNotFoundException {
    try {
      Map<String, Field> properties = this.getProperties();
      if (properties.containsKey(propertyName)) {
        Field propertyField = properties.get(propertyName);
        return getFieldValue(propertyField);
      }
      throw new PropertyNotFoundException(this.getClass(), propertyName);
    } catch (IllegalAccessException e) {
      // TODO will never happen -- correct later
      throw new ReadOnlyPropertyException(this.getClass(), propertyName, null);
    }
  }

  public Relation getRelationByName(String relationName) throws RelationNotFound {
    List<Relation> relations = getRelations();
    if (relations != null) {
      Optional<Relation> relationOp = relations.stream().filter(
          rel -> rel.getName().equals(relationName)).findFirst();
      if (relationOp.isPresent()) {
        return (Relation) relationOp.get();
      }
    }
    throw new RelationNotFound(this.getClass(), relationName, "name");
  }


  public Relation getRelationByRestPath(String restPath) throws RelationNotFound {
    List<Relation> relations = getRelations();
    if (relations != null) {
      Optional<Relation> relationOp = this.getRelations().stream().filter(
          rel -> rel.getRestPath().equals(restPath)).findFirst();
      if (relationOp.isPresent()) {
        return (Relation) relationOp.get();
      }
    }
    throw new RelationNotFound(this.getClass(), restPath, "rest path");
  }

  @JsonIgnore
  public static <M extends PersistedModel> Map<String, Field> getProperties(Class<M> modelCass) {
    Map<String, Field> properties = Maps.newConcurrentMap();
    Field[] var2 = modelCass.getDeclaredFields();
    int var3 = var2.length;

    for (int var4 = 0; var4 < var3; ++var4) {
      Field declaredField = var2[var4];
      Transient aTransient = (Transient) declaredField.getAnnotation(Transient.class);
      if (aTransient == null) {
        String propertyName = declaredField.getName();
        JsonProperty jsonProperty = (JsonProperty) declaredField.getAnnotation(JsonProperty.class);
        if (jsonProperty != null) {
          propertyName = jsonProperty.value();
        }

        properties.put(propertyName, declaredField);
      }
    }

    return properties;
  }

  @JsonIgnore
  public Map<String, Field> getProperties() {
    return getProperties(this.getClass());
  }

  @JsonIgnore
  public RelatedModel getRelatedModel(String relationName) throws RelationNotFound {
    Relation relation = getRelationByName(relationName);
    return getRelatedModel(relation);
  }

  @JsonIgnore
  private RelatedModel getRelatedModel(Relation relation) {
    return new RelatedModel(this, relation);
  }
}
