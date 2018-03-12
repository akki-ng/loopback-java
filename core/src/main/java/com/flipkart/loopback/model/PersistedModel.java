package com.flipkart.loopback.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.flipkart.loopback.configuration.ModelConfiguration;
import com.flipkart.loopback.configuration.manager.ModelConfigurationManager;
import com.flipkart.loopback.exception.LoopbackException;
import com.flipkart.loopback.filter.Filter;
import com.flipkart.loopback.filter.WhereFilter;
import com.flipkart.loopback.relation.Relation;
import java.beans.Transient;
import java.io.IOException;
import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import lombok.SneakyThrows;
import org.apache.commons.lang3.StringUtils;

/**
 * Created by akshaya.sharma on 02/03/18
 */

public abstract class PersistedModel<M extends PersistedModel<M, CM>, CM extends
    ModelConfigurationManager> extends Model<M, CM> {

  @Override
  public ModelConfiguration getConfiguration() {
    return M.getConfigurationManager().getModelConfiguration(this.getClass());
  }


  public static <M extends PersistedModel, F extends Filter> int count(Class<M> modelClass, F filter) {
    return getProvider().count(modelClass, filter);
  }


  public static <M extends PersistedModel> M create(M model) {
    return getProvider().create(model);
  }


  public static <M extends PersistedModel> List<M> create(List<M> models) {
    return getProvider().create(models);
  }


  public static <M extends PersistedModel> M updateOrCreate(Class<M> modelClass, Map<String,
      Object> patchData) throws LoopbackException {
    ModelConfiguration configuration = M.getConfigurationManager().getModelConfiguration(modelClass);
    String idPropertyName = configuration.getIdPropertyName();
    if(patchData.containsKey(idPropertyName) && patchData.get(idPropertyName) != null) {
      // Id exists
      M model = M.findById(modelClass,null, patchData.get(idPropertyName));
      return (M) model.updateAttributes(patchData);
    }else {
      // Try create
      ObjectMapper mapper = new ObjectMapper();
      M model = mapper.convertValue(patchData, modelClass);
      return M.create(model);
    }
  }


  public static <M extends PersistedModel> M patchOrCreateWithWhere(M model, Map<String,
      Object> data) {
    return getProvider().patchOrCreateWithWhere(model, data);
  }


  public static <M extends PersistedModel, F extends WhereFilter> M upsertWithWhere(Class<M>
                                                                                        modelClass, F filter,
                                                                                    Map<String,
                                                                                        Object>
                                                                                        data) {
    return getProvider().upsertWithWhere(modelClass, filter, data);
  }


  public static <M extends PersistedModel, F extends Filter> M findOrCreate(Class<M>
                                                                                   modelClass, F
      filter, Map<String,
      Object>
      data) {
    return getProvider().findOrCreate(modelClass, filter, data);
  }


  public static <M extends PersistedModel, W extends WhereFilter> int updateAll(Class<M> modelClass, W
      where, Map<String, Object> data) {
    return getProvider().updateAll(modelClass, where, data);
  }


  public static <M extends PersistedModel> M replaceById(M model, Object id) {
    return getProvider().replaceById(model, id);
  }


  public static <M extends PersistedModel> M replaceOrCreate(M model) {
    return getProvider().replaceOrCreate(model);
  }


  public static <M extends PersistedModel> boolean exists(Class<M> modelClass, Object id) {
    return getProvider().exists(modelClass, id);
  }


  public static <M extends PersistedModel, F extends Filter> List<M> find(Class<M> modelClass, F
      filter) {
    return getProvider().find(modelClass, filter);
  }


  public static <M extends PersistedModel> M findById(Class<M> modelClass, Filter filter, Object id) {
    return getProvider().findById(modelClass, filter, id);
  }


  public static <M extends PersistedModel, F extends Filter> M findOne(Class<M> modelClass, F
      filter) {
    return getProvider().findOne(modelClass, filter);
  }


  public static <M extends PersistedModel, F extends Filter> int destroyAll(M model, F filter) {
    return getProvider().destroyAll(model, filter);
  }


  public static <M extends PersistedModel> M destroyById(Class<M> modelClass, Object id) {
    M model = M.findById(modelClass, null, id);
    return (M) model.destroy();
  }


  public <M extends PersistedModel> M  destroy() {
    return (M)getProvider().destroy(this);
  }

  @JsonIgnore
  public abstract Object getId();

  @JsonIgnore
  public String getStringifiedId() {
    return String.valueOf(getId());
  }


  public <M extends PersistedModel> M save() {
    return (M) getProvider().replaceOrCreate(this);
  }


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
    return false;
  }


  public <M extends PersistedModel> M updateAttributes(Map<String, Object> data)
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
    return this.save();
  }


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

  public Relation getRelationByName(String relationName) throws LoopbackException {
    return getConfiguration().getRelationByName(relationName);
  }

  public Relation getRelationByRestPath(String restPath) throws LoopbackException {
    return getConfiguration().getRelationByRestPath(restPath);
  }

  private Filter getScopeForRelatedMode(Relation relation) throws IOException, LoopbackException {
    Class<? extends PersistedModel> relatedModelClass = relation.getRelatedModelClass();
    return new Filter(null);
  }

  public <M extends PersistedModel> M getHasOneRelatedModel(String relationName) throws
      LoopbackException {
    Relation relation = getRelationByName(relationName);
    return getHasOneRelatedModel(relation);
  }

  public <M extends PersistedModel> M getHasOneRelatedModel(Relation relation) throws
      LoopbackException {
    Class<? extends PersistedModel> relatedModelClass = relation.getRelatedModelClass();
    Filter scope = null;
    return (M) relatedModelClass.cast(getProvider().findOne(relatedModelClass, scope));
  }

  public <M extends PersistedModel> List<M> getHasManyRelatedModel(String relationName, Filter filter)
      throws
      LoopbackException {
    Relation relation = getRelationByName(relationName);
    return getHasManyRelatedModel(relation, filter);
  }

  public <M extends PersistedModel> List<M> getHasManyRelatedModel(Relation relation, Filter filter)
      throws
      LoopbackException {
    Class<? extends PersistedModel> relatedModelClass = relation.getRelatedModelClass();
    Filter scope = null;
    /*
      merge scope with filter
     */
    return (List<M>) getProvider().find(relatedModelClass, scope);
  }
}
