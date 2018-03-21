package com.flipkart.loopback.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.flipkart.loopback.annotation.Transaction;
import com.flipkart.loopback.configuration.ModelConfiguration;
import com.flipkart.loopback.configuration.manager.ModelConfigurationManager;
import com.flipkart.loopback.connector.Connector;
import com.flipkart.loopback.constants.IDType;
import com.flipkart.loopback.exception.model.CouldNotPerformException;
import com.flipkart.loopback.exception.model.InternalError;
import com.flipkart.loopback.exception.model.OperationNotAllowedException;
import com.flipkart.loopback.exception.model.persistence.ModelNotFoundException;
import com.flipkart.loopback.exception.model.relation.RelationNotFound;
import com.flipkart.loopback.exception.validation.model.IdFieldNotFoundException;
import com.flipkart.loopback.exception.validation.model.InvalidPropertyValueException;
import com.flipkart.loopback.exception.validation.model.PropertyNotFoundException;
import com.flipkart.loopback.exception.validation.model.ReadOnlyPropertyException;
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
import java.util.concurrent.ConcurrentHashMap;
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

  protected static <M extends PersistedModel> void beginTransaction(@NotNull Class<M> modelClass) {
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
  public IDType getIDType() {
    return getConfiguration().getIDType();
  }

  @JsonIgnore
  public static <M extends PersistedModel> WhereFilter createIDFilter(Class<M> modelClass,
      Serializable id) {
    ModelConfiguration configuration = getConfiguration(modelClass);
    String idPropertyName = configuration.getIdPropertyName();

    if (id == null) {
      throw new InvalidPropertyValueException(modelClass, configuration.getIdPropertyName(),
          String.valueOf(id), " can not be null");
    }

    IDType idType = configuration.getIDType();
    if (!idType.isConvertible(id)) {
      throw new InvalidPropertyValueException(modelClass, configuration.getIdPropertyName(),
          String.valueOf(id), " expected a " + idType);
    }

    if (idType == IDType.NUMBER) {
      id = Long.parseLong(String.valueOf(id));
      return new WhereFilter("{\"" + configuration.getIdPropertyName() + "\": " + id + "}");
    } else {
      return new WhereFilter("{\"" + configuration.getIdPropertyName() + "\": \"" + id + "\"}");
    }
  }

  protected static <M extends PersistedModel> void commitTransaction(@NotNull Class<M> modelClass) {
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
  public ModelConfiguration getConfiguration() {
    return getConfiguration(this.getClass());
  }

  @Transaction
  public static <M extends PersistedModel, W extends WhereFilter> long count(
      @NotNull Class<M> modelClass, W where) {
    return getProvider().count(modelClass, where);
  }

  @Transaction
  public static <M extends PersistedModel> M create(
      @NotNull M model) throws CouldNotPerformException {
    beginTransaction(model.getClass());
    model = getProvider().create(model);
    commitTransaction(model.getClass());
    return model;
  }

  @Transaction
  public static <M extends PersistedModel> List<M> create(
      @NotNull List<M> models) throws CouldNotPerformException {
    beginTransaction(models.get(0).getClass());
    models = getProvider().create(models);
    commitTransaction(models.get(0).getClass());
    return models;
  }

  @Transaction
  public static <M extends PersistedModel> M updateOrCreate(@NotNull Class<M> modelClass,
      @NotNull Map<String, Serializable> patchData) throws InternalError, ModelNotFoundException,
      InvalidPropertyValueException, CouldNotPerformException, IdFieldNotFoundException {
    beginTransaction(modelClass);
    M model = getProvider().updateOrCreate(modelClass, patchData);
    commitTransaction(modelClass);
    return model;
  }

  @Transaction
  public static <M extends PersistedModel> long patchMultipleWithWhere(@NotNull Class<M> modelClass,
      @NotNull WhereFilter where, @NotNull Map<String, Object> data) {
    beginTransaction(modelClass);
    long count = getProvider().patchMultipleWithWhere(modelClass, where, data);
    commitTransaction(modelClass);
    return count;
  }

  @Transaction
  public static <M extends PersistedModel, W extends WhereFilter> M upsertWithWhere(
      @NotNull Class<M> modelClass, @NotNull W where,
      @NotNull Map<String, Object> data) throws OperationNotAllowedException,
      CouldNotPerformException {
    beginTransaction(modelClass);
    M model = getProvider().upsertWithWhere(modelClass, where, data);
    commitTransaction(modelClass);
    return model;
  }

  @Transaction
  public static <M extends PersistedModel, F extends Filter> M findOrCreate(
      @NotNull Class<M> modelClass, @NotNull F filter,
      @NotNull Map<String, Object> data) throws OperationNotAllowedException,
      CouldNotPerformException {
    beginTransaction(modelClass);
    M model = getProvider().findOrCreate(modelClass, filter, data);
    commitTransaction(modelClass);
    return model;
  }

  @Transaction
  public static <M extends PersistedModel, W extends WhereFilter> long updateAll(
      Class<M> modelClass, W where, Map<String, Object> data) {
    beginTransaction(modelClass);
    long count = getProvider().updateAll(modelClass, where, data);
    commitTransaction(modelClass);
    return count;
  }

  @Transaction
  public static <M extends PersistedModel> M replaceById(M model,
      Serializable id) throws ModelNotFoundException, CouldNotPerformException {
    beginTransaction(model.getClass());
    M existingModel = (M) findById(model.getClass(), null, id);
    model = (M) model.setAttribute(model.getIdPropertyName(), existingModel.getId());
    model = getProvider().replaceById(model, id);
    commitTransaction(model.getClass());
    return model;
  }

  @Transaction
  public static <M extends PersistedModel> M replaceOrCreate(
      M model) throws CouldNotPerformException {
    beginTransaction(model.getClass());
    model = getProvider().replaceOrCreate(model);
    commitTransaction(model.getClass());
    return model;
  }

  @Transaction
  public static <M extends PersistedModel> boolean exists(Class<M> modelClass, Serializable id) {
    beginTransaction(modelClass);
    boolean exists = getProvider().exists(modelClass, id);
    commitTransaction(modelClass);
    return exists;
  }

  @Transaction
  public static <M extends PersistedModel, F extends Filter> List<M> find(Class<M> modelClass,
      F filter) {
    beginTransaction(modelClass);
    List<M> models = getProvider().find(modelClass, filter);
    commitTransaction(modelClass);
    return models;
  }

  @Transaction
  public static <M extends PersistedModel> M findById(Class<M> modelClass, Filter filter,
      Serializable id) throws ModelNotFoundException {
    beginTransaction(modelClass);
    Filter idFilter = new Filter();
    idFilter.setWhere(createIDFilter(modelClass, id));
    M model = getProvider().findOne(modelClass, idFilter);
    commitTransaction(modelClass);
    return model;
  }

  @Transaction
  public static <M extends PersistedModel, F extends Filter> M findOne(Class<M> modelClass,
      F filter) throws ModelNotFoundException {
    beginTransaction(modelClass);
    M model = getProvider().findOne(modelClass, filter);
    commitTransaction(modelClass);
    return model;
  }

  @Transaction
  public static <M extends PersistedModel, W extends WhereFilter> long destroyAll(
      Class<M> modelClass, W where) {
    beginTransaction(modelClass);
    long count = getProvider().destroyAll(modelClass, where);
    commitTransaction(modelClass);
    return count;
  }

  @Transaction
  public static <M extends PersistedModel> M destroyById(Class<M> modelClass,
      Serializable id) throws ModelNotFoundException {
    beginTransaction(modelClass);
    M model = M.findById(modelClass, null, id);
    model = (M) model.destroy();
    commitTransaction(modelClass);
    return model;
  }

  @Transaction
  public <M extends PersistedModel> M destroy() {
    return (M) getProvider().destroy(this);
  }

  @JsonIgnore
  public abstract Serializable getId();

  @Transaction
  public <M extends PersistedModel> M save() throws CouldNotPerformException {
    return (M) getProvider().replaceOrCreate(this);
  }

  @Transaction
  public M reload() throws InternalError, ModelNotFoundException {
    return (M) findById(this.getClass(), null, this.getId());
  }

  @JsonIgnore
  public String getIdPropertyName() {
    ModelConfiguration configuration = this.getConfiguration();
    return configuration.getIdPropertyName();
  }

  @Transaction
  public <M extends PersistedModel> M updateAttributes(
      Map<String, Serializable> data) throws CouldNotPerformException {
    return (M) this.setAttributes(data).save();
  }

  public <M extends PersistedModel> M setAttributes(
      Map<String, Serializable> data) throws InvalidPropertyValueException, InternalError {
    String idName = this.getIdPropertyName();
    if (StringUtils.isBlank(idName)) {
      // Model requires an id field name
      throw new IdFieldNotFoundException(this.getClass());
    }

    Serializable id = data.containsKey(idName) ? data.get(idName) : null;
    IDType idType = getConfiguration().getIDType();
    if ((id != null) && !idType.isConvertible(id)) {
      throw new InvalidPropertyValueException(getClass(), getConfiguration().getIdPropertyName(),
          String.valueOf(id), " expected a " + idType);
    }

    if ((id != null) && (getId() != null) && !getId().toString().equals(id.toString())) {
      // Model can not update ID`
      throw new ReadOnlyPropertyException(this.getClass(), idName, data.get(idName));
    }

    ObjectMapper mapper = new ObjectMapper();
    Map<String, Object> modelAsMap = mapper.convertValue(this,
        new TypeReference<Map<String, Object>>() {
        });
    modelAsMap.putAll(data);
    return (M) mapper.convertValue(modelAsMap, this.getClass());
  }

  @Transaction
  private M updateAttribute(String attributeName,
      Serializable attributeValue) throws CouldNotPerformException {
    return this.setAttribute(attributeName, attributeValue).save();
  }

  public <F extends Filter> M setAttribute(String attributeName, Serializable attributeValue) {

    Map<String, Serializable> dataMap = new ConcurrentHashMap<>();
    dataMap.put(attributeName, attributeValue);
    return (M) setAttributes(dataMap);
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
  public Object getFieldValue(Field field) {
    try {
      boolean accessible = field.isAccessible();
      field.setAccessible(true);
      Object value = field.get(this);
      field.setAccessible(accessible);
      return value;
    } catch (IllegalAccessException e) {
      throw new InternalError(this.getClass(), e);
    }
  }

  @JsonIgnore
  public Object getPropertyValue(
      String propertyName) throws PropertyNotFoundException {
    Map<String, Field> properties = this.getProperties();
    if (properties.containsKey(propertyName)) {
      Field propertyField = properties.get(propertyName);
      return getFieldValue(propertyField);
    }
    throw new PropertyNotFoundException(this.getClass(), propertyName);
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
