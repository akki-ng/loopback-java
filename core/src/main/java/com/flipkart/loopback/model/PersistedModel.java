package com.flipkart.loopback.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.flipkart.loopback.annotation.NonDB;
import com.flipkart.loopback.exception.LoopbackException;
import com.flipkart.loopback.filter.Filter;
import com.flipkart.loopback.filter.WhereFilter;
import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import lombok.SneakyThrows;
import org.apache.commons.lang3.StringUtils;

/**
 * Created by akshaya.sharma on 02/03/18
 */

public abstract class PersistedModel<M extends PersistedModel<M>> extends Model<M>{
  @JsonIgnore
  public abstract <C extends PersistedModel> Class<C> getModelClass();

  public static <M extends Model, F extends Filter>  int count(Class<M> modelClass, F filter) {
    return getConnector().count(modelClass, filter);
  };

  public static <M extends PersistedModel> M create(M model) {
    return getConnector().create(model);
  }

  public static <M extends PersistedModel<M>> List<M> create(List<M> models) {
    return getConnector().create(models);
  }

  public static <M extends PersistedModel> M updateOrCreate(M model) {
    return getConnector().updateOrCreate(model);
  }

  public static <M extends PersistedModel<M>> M patchOrCreateWithWhere(M model, Map<String, Object> data) {
    return getConnector().patchOrCreateWithWhere(model, data);
  }

  public static <M extends PersistedModel, F extends WhereFilter> M upsertWithWhere(Class<M>
                                                                                       modelClass, F filter,
                                                                      Map<String, Object>
                                                                          data) {
    return getConnector().upsertWithWhere(modelClass, filter, data);
  }

  public static <M extends PersistedModel<M>, F extends Filter> M findOrCreate(Class<M> modelClass, F filter, Map<String,
      Object>
      data) {
    return getConnector().findOrCreate(modelClass, filter, data);
  }

  public static <M extends PersistedModel<M>, W extends WhereFilter> int updateAll(M model, W
      where) {
    Map<String, Object> data = model.getFieldMap();
    return getConnector().updateAll(model, where, data);
  }


  public static <M extends PersistedModel> M replaceById(M model, Object id) {
    return getConnector().replaceById(model, id);
  }

  public static <M extends PersistedModel> M replaceOrCreate(M model) {
    return getConnector().replaceOrCreate(model);
  }


  public static <M extends PersistedModel> boolean exists(Class<M> modelClass, Object id) {
    return getConnector().exists(modelClass, id);
  }

  public static <M extends PersistedModel<M>, F extends Filter> List<M> find(Class<M> modelClass, F
      filter) {
    return getConnector().find(modelClass, filter);
  }

  public static <M extends PersistedModel> M findById(Class<M> modelClass, Object id) {
    return getConnector().findById(modelClass, id);
  }

  public static <M extends PersistedModel, F extends Filter> M findOne(Class<M> modelClass, F
      filter) {
    return getConnector().findOne(modelClass, filter);
  }

  public static <M extends PersistedModel<M>, F extends Filter> int destroyAll(M model, F filter) {
    return getConnector().destroyAll(model, filter);
  }

  public static <M extends PersistedModel> void destroyById(Class<M> modelClass, Object id) {
    getConnector().destroyById(modelClass, id);
  }

  public boolean destroy() {
    return getConnector().destroy(this, this.getId());
  }

  @JsonIgnore
  public abstract Object getId();

  @JsonIgnore
  public String getStringifiedId() {
    return String.valueOf(getId());
  }

  @JsonIgnore
  public abstract String getIdName();

  public <M extends PersistedModel> M save() {
    return getConnector().save((M)this);
  }

  public M reload() {
    return getConnector().save((M)this);
  }

  @JsonIgnore
  public boolean isNewRecord() {
    return false;
  }

  public <M extends PersistedModel> M updateAttributes()
      throws LoopbackException {
    String idName = this.getIdName();
    if(StringUtils.isBlank(idName)) {
      // Model requires an id field name
      throw new LoopbackException("ID field not defined for " + this
          .getClass());
    }
    Map<String, Object> data = this.getFieldMap();
    if(data.containsKey(idName)) {
      M model = this.findOne(this.getModelClass(), null);
      for (Map.Entry<String, Object> e : data.entrySet()) {
        model.setAttribute(e.getKey(), e.getValue());
      }
      return (M)model.save();
    }else {
      // Patch on an instance needs idField
      throw new LoopbackException(idName + " is required to patch an instance of " + this
          .getClass());
    }
  }

  private M updateAttribute(String attributeName, Object
  attributeValue) throws LoopbackException {
    return this.setAttribute(attributeName, attributeValue).save();
  }

  public <F extends Filter> M setAttribute(String attributeName, Object
      attributeValue) throws LoopbackException {
    Field declaredField =  null;
    try {

      declaredField = this.getClass().getDeclaredField(attributeName);
      NonDB nonDB = declaredField.getAnnotation(NonDB.class);
      if(nonDB != null) {
        throw new LoopbackException("Attribute " + attributeName + " can be updated");
      }
      boolean accessible = declaredField.isAccessible();
      declaredField.setAccessible(true);
      Class toCast = declaredField.getType();
      if(!toCast.isPrimitive()) {
        declaredField.set(this, declaredField.getType().cast(attributeValue));
      }else {
        if("int".equals(toCast.getName())) {
          int val = 0;
          try {
            val = Integer.parseInt(String.valueOf(attributeValue));
          }catch(Exception e) {

          }
          declaredField.set(this, val);
        }
      }
      declaredField.setAccessible(accessible);
      return (M)this;
    } catch (NoSuchFieldException
        | SecurityException
        | IllegalArgumentException
        | IllegalAccessException e) {
      throw new LoopbackException(e);
    }
  }

  @JsonIgnore
  private String getFieldName(Field f) {
    JsonProperty property = f.getAnnotation(JsonProperty.class);
    if(property != null)
      System.out.println(property);
    if(property != null && StringUtils.isNotEmpty(property.value())) {
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
}
