package com.flipkart.loopback.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.flipkart.loopback.annotation.Transaction;
import com.flipkart.loopback.configuration.ModelConfiguration;
import com.flipkart.loopback.configuration.manager.ModelConfigurationManager;
import com.flipkart.loopback.connector.Connector;
import com.flipkart.loopback.constants.RelationType;
import com.flipkart.loopback.exception.LoopbackException;
import com.flipkart.loopback.filter.Filter;
import com.flipkart.loopback.filter.WhereFilter;
import com.flipkart.loopback.relation.RelatedModel;
import com.flipkart.loopback.relation.Relation;
import com.google.common.collect.Maps;
import java.beans.Transient;
import java.io.IOException;
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

/**
 * Created by akshaya.sharma on 02/03/18
 */

public abstract class PersistedModel<M extends PersistedModel<M, CM>, CM extends
    ModelConfigurationManager> extends Model<M, CM> {

  @JsonIgnore
  protected abstract List<Relation> getRelations();


  protected static <M extends PersistedModel> void beginTransaction(Class<M> modelClass) {
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

  protected static <M extends PersistedModel> void commitTransaction(Class<M> modelClass) {
    Connector connector = getConnector(modelClass);
    EntityManager em = connector.getEntityManager();
    EntityTransaction tx = em.getTransaction();
    boolean newEm = !tx.isActive() || !em.isOpen();

    try
    {
      if (em.isOpen() && tx.isActive()) {
        tx.commit();
      }
    }
    catch (Throwable e)
    {
      if (tx.isActive()) {
        em.getTransaction().rollback();
      }
      throw e;
    }
    finally
    {
      if (newEm)
      {
        connector.clearEntityManager();
        if (em.isOpen())
          em.close();
      }
    }
  }

  @Override
  public ModelConfiguration getConfiguration() {
    return M.getConfigurationManager().getModelConfiguration(this.getClass());
  }

  @Transaction
  public static <M extends PersistedModel, W extends WhereFilter> long count(Class<M> modelClass, W
      where) {
    return getProvider().count(modelClass, where);
  }

  @Transaction
  public static <M extends PersistedModel> M create(M model) {
    beginTransaction(model.getClass());
    model = getProvider().create(model);
    commitTransaction(model.getClass());
    return model;
  }


  @Transaction
  public static <M extends PersistedModel> List<M> create(List<M> models) {
    if(models != null && models.size() > 0) {
      beginTransaction(models.get(0).getClass());
      models = getProvider().create(models);
      commitTransaction(models.get(0).getClass());
    }
    return models;
  }

  @Transaction
  public static <M extends PersistedModel> M updateOrCreate(Class<M> modelClass, Map<String,
      Object> patchData) throws LoopbackException {
    beginTransaction(modelClass);
    ModelConfiguration configuration = M.getConfigurationManager().getModelConfiguration(modelClass);
    String idPropertyName = configuration.getIdPropertyName();
    M model = null;
    if(patchData.containsKey(idPropertyName) && patchData.get(idPropertyName) != null) {
      // Id exists
      model = M.findById(modelClass,null, (Serializable) patchData.get(idPropertyName));
      model = (M) model.updateAttributes(patchData);
    }else {
      // Try create
      ObjectMapper mapper = new ObjectMapper();
      model = mapper.convertValue(patchData, modelClass);
      model = M.create(model);
    }
    commitTransaction(modelClass);
    return model;
  }

  @Transaction
  public static <M extends PersistedModel> long patchMultipleWithWhere(Class<M> modelClass,
                                                                    WhereFilter where, Map<String,
      Object> data) {
    beginTransaction(modelClass);
    long count = getProvider().patchMultipleWithWhere(modelClass, where, data);
    commitTransaction(modelClass);
    return count;
  }

  @Transaction
  public static <M extends PersistedModel, W extends WhereFilter> M upsertWithWhere(Class<M>
                                                                                        modelClass, W where,
                                                                                    Map<String,
                                                                                        Object>
                                                                                        data) {
    beginTransaction(modelClass);
    M model = getProvider().upsertWithWhere(modelClass, where, data);
    commitTransaction(modelClass);
    return model;
  }

  @Transaction
  public static <M extends PersistedModel, F extends Filter> M findOrCreate(Class<M>
                                                                                   modelClass, F
      filter, Map<String,
      Object>
      data) {
    beginTransaction(modelClass);
    M model = getProvider().findOrCreate(modelClass, filter, data);
    commitTransaction(modelClass);
    return model;
  }

  @Transaction
  public static <M extends PersistedModel, W extends WhereFilter> long updateAll(Class<M> modelClass, W
      where, Map<String, Object> data) {
    beginTransaction(modelClass);
    long count =  getProvider().updateAll(modelClass, where, data);
    commitTransaction(modelClass);
    return count;
  }

  @Transaction
  public static <M extends PersistedModel> M replaceById(M model, Serializable id) {
    beginTransaction(model.getClass());
    model = getProvider().replaceById(model, id);
    commitTransaction(model.getClass());
    return model;
  }

  @Transaction
  public static <M extends PersistedModel> M replaceOrCreate(M model) {
    beginTransaction(model.getClass());
    model = getProvider().replaceOrCreate(model);
    commitTransaction(model.getClass());
    return model;
  }

  @Transaction
  public static <M extends PersistedModel> boolean exists(Class<M> modelClass, Object id) {
    beginTransaction(modelClass);
    boolean exists = getProvider().exists(modelClass, id);
    commitTransaction(modelClass);
    return exists;
  }

  @Transaction
  public static <M extends PersistedModel, F extends Filter> List<M> find(Class<M> modelClass, F
      filter) {
    beginTransaction(modelClass);
    List<M> models = getProvider().find(modelClass, filter);
    commitTransaction(modelClass);
    return models;
  }

  @Transaction
  public static <M extends PersistedModel> M findById(Class<M> modelClass, Filter filter, Serializable id) {
    beginTransaction(modelClass);
    M model = getProvider().findById(modelClass, filter, id);
    commitTransaction(modelClass);
    return model;
  }

  @Transaction
  public static <M extends PersistedModel, F extends Filter> M findOne(Class<M> modelClass, F
      filter) {
    beginTransaction(modelClass);
    M model = getProvider().findOne(modelClass, filter);
    commitTransaction(modelClass);
    return model;
  }

  @Transaction
  public static <M extends PersistedModel, W extends WhereFilter> long destroyAll(Class<M> modelClass, W
      where) {
    beginTransaction(modelClass);
    long count = getProvider().destroyAll(modelClass, where);
    commitTransaction(modelClass);
    return count;
  }

  @Transaction
  public static <M extends PersistedModel> M destroyById(Class<M> modelClass, Serializable id) {
    beginTransaction(modelClass);
    M model = M.findById(modelClass, null, id);
    model = (M) model.destroy();
    commitTransaction(modelClass);
    return model;
  }

  @Transaction
  public <M extends PersistedModel> M  destroy() {
    return (M)getProvider().destroy(this);
  }

  @JsonIgnore
  public abstract Serializable getId();

  @JsonIgnore
  public String getStringifiedId() {
    return String.valueOf(getId());
  }

  @Transaction
  public <M extends PersistedModel> M save() {
    return (M) getProvider().replaceOrCreate(this);
  }

  @Transaction
  public M reload() {
    return (M) getProvider().findById(this.getClass(), null, this.getId());
  }

  @JsonIgnore
  public String getIdPropertyName() {
    ModelConfiguration configuration = ModelConfigurationManager.getInstance()
        .getModelConfiguration(this.getClass());
    return configuration.getIdPropertyName();
  }

  @JsonIgnore
  public boolean isNewRecord() {
    // TODO -> modelProvider will set
    return false;
  }

  @Transaction
  public <M extends PersistedModel> M updateAttributes(Map<String, Object> data)
      throws LoopbackException {
    return (M) this.setAttributes(data).save();
  }

  public <M extends PersistedModel> M setAttributes(Map<String, Object> data)
      throws LoopbackException {
    String idName = this.getIdPropertyName();
    if (StringUtils.isBlank(idName)) {
      // Model requires an id field name
      throw new LoopbackException("ID field not defined for " + this
          .getClass());
    }
    System.out.println(getId());

    if(data.containsKey(idName) && !getId().equals(data.get(idName))) {
      // Model can not update ID
      throw new LoopbackException("ID can not be updated " + this
          .getClass());
    }

    for (Map.Entry<String, Object> e : data.entrySet()) {
      this.setAttribute(e.getKey(), e.getValue());
    }
    return (M) this;
  }

  @Transaction
  private M updateAttribute(String attributeName, Object
      attributeValue) throws LoopbackException {
    return this.setAttribute(attributeName, attributeValue).save();
  }

  public <F extends Filter> M setAttribute(String attributeName, Object
      attributeValue) throws LoopbackException {

    try {
      for(Field declaredField : this.getClass().getDeclaredFields()) {
        Transient aTransient = declaredField.getAnnotation(Transient.class);
        if (aTransient != null) {
          throw new LoopbackException("Attribute " + attributeName + " can be updated");
        }
        String fieldName = declaredField.getName();
        JsonProperty property = declaredField.getAnnotation(JsonProperty.class);
        if(property != null) {
          fieldName = property.value();
        }

        if(fieldName.equals(attributeName)) {
          boolean accessible = declaredField.isAccessible();
          declaredField.setAccessible(true);
          Class toCast = declaredField.getType();
          if (!toCast.isPrimitive()) {
            if(attributeValue != null) {
              if(toCast == Integer.class) {
                attributeValue = new Integer(attributeValue.toString());
              }else if(toCast == Long.class) {
                attributeValue = new Long(attributeValue.toString());
              }else if(toCast == Float.class) {
                attributeValue = new Float(attributeValue.toString());
              }else if(toCast == Double.class) {
                attributeValue = new Double(attributeValue.toString());
              }else if(toCast == String.class) {
                attributeValue = attributeValue.toString();
              }
            }

            declaredField.set(this, declaredField.getType().cast(attributeValue));
          } else {
            if ("int".equals(toCast.getName())) {
              int val = 0;
              try {
                val = Integer.parseInt(String.valueOf(attributeValue));
              } catch (Exception e) {

              }
              declaredField.set(this, val);
            }
            // TODO
          }
          declaredField.setAccessible(accessible);
        }
      }
      return (M) this;
    } catch (SecurityException
        | IllegalArgumentException
        | IllegalAccessException e) {
      throw new LoopbackException(e);
    }
  }

  @JsonIgnore
  private String getFieldName(Field f) {
    JsonProperty property = f.getAnnotation(JsonProperty.class);
    if (property != null) {
      System.out.println(property);
    }
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
  public Object getPropertyValue(String propertyName) throws IllegalAccessException {
    Map<String, Field> properties = this.getProperties();
    Field propertyField = properties.get(propertyName);
    return getFieldValue(propertyField);
  }

  public Relation getRelationByName(String relationName) throws
      LoopbackException {
    List<Relation> relations = getRelations();
    if(relations == null) {
      return null;
    }
    Optional<Relation> relationOp = relations.stream()
        .filter(rel -> rel.getName().equals(relationName))
        .findFirst();
    return relationOp.isPresent() ? relationOp.get() : null;
  }


  public Relation getRelationByRestPath(String restPath) throws LoopbackException {
    List<Relation> relations = getRelations();
    if(relations == null) {
      return null;
    }
    Optional<Relation> relOp = this.getRelations().stream()
        .filter(rel -> rel.getRestPath().equals(restPath))
        .findFirst();
    return relOp.isPresent() ? (Relation)relOp.get() : null;
  }

  @JsonIgnore
  public static <M extends PersistedModel> Map<String, Field> getProperties(Class<M> modelCass) {
    Map<String, Field> properties = Maps.newConcurrentMap();
    Field[] var2 = modelCass.getDeclaredFields();
    int var3 = var2.length;

    for(int var4 = 0; var4 < var3; ++var4) {
      Field declaredField = var2[var4];
      Transient aTransient = (Transient)declaredField.getAnnotation(Transient.class);
      if (aTransient == null) {
        String propertyName = declaredField.getName();
        JsonProperty jsonProperty = (JsonProperty)declaredField.getAnnotation(JsonProperty.class);
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
  public RelatedModel getRelatedModel(String relationName) throws LoopbackException {
    Relation relation = getRelationByName(relationName);
    return getRelatedModel(relation);
  }

  @JsonIgnore
  public RelatedModel getRelatedModel(Relation relation) {
    return new RelatedModel(this, relation);
  }
}
