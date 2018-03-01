package com.flipkart.loopback.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.flipkart.loopback.annotation.NonDB;
import com.flipkart.loopback.exception.LoopbackException;
import com.flipkart.loopback.filter.Filter;
import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import lombok.SneakyThrows;
import org.apache.commons.lang3.StringUtils;

/**
 * Created by akshaya.sharma on 02/03/18
 */

public abstract class PersistedModel<M extends PersistedModel<M>> extends Model<M>{

  public static <M extends Model, F extends Filter>  int count(M model, F filter) {
    return getConnector().count(model, filter);
  };

  public static <M extends PersistedModel<M>> M create(M model) {
    return getConnector().create(model);
  }

  public static <M extends PersistedModel<M>> List<M> create(List<M> models) {
    return getConnector().create(models);
  }

  public static <M extends PersistedModel<M>> M updateOrCreate(M model) {
    return getConnector().updateOrCreate(model);
  }

  public static <M extends PersistedModel<M>> M patchOrCreateWithWhere(M model, Map<String, Object> data) {
    return getConnector().patchOrCreateWithWhere(model, data);
  }

  public static <M extends PersistedModel<M>, F extends Filter> M upsertWithWhere(M model, F filter,
                                                                      Map<String, Object>
                                                                          data) {
    return getConnector().upsertWithWhere(model, filter, data);
  }

  public static <M extends PersistedModel<M>, F extends Filter> M findOrCreate(M model, F filter, Map<String,
      Object>
      data) {
    return getConnector().findOrCreate(model, filter, data);
  }

  public static <M extends PersistedModel<M>, F extends Filter> List<M> updateAll(M model, F filter,
                                                                      Map<String, Object>
                                                                          data) {
    return getConnector().updateAll(model, filter, data);
  }


  public static <M extends PersistedModel<M>> boolean replaceById(M model, Object id) {
    return getConnector().replaceById(model, id);
  }

  public static <M extends PersistedModel<M>> boolean replaceOrCreate(M model, Object id) {
    return getConnector().replaceOrCreate(model, id);
  }


  public static <M extends PersistedModel<M>> boolean exists(M model, Object id) {
    return getConnector().exists(model, id);
  }

  public static <M extends PersistedModel<M>, F extends Filter> List<M> find(M model, F filter) {
    return getConnector().find(model, filter);
  }

  public static <M extends PersistedModel<M>> M findById(M model, Object id) {
    return getConnector().findById(model, id);
  }

  public static <M extends PersistedModel<M>, F extends Filter> M findOne(M model, F filter) {
    return getConnector().findOne(model, filter);
  }

  public static <M extends PersistedModel<M>, F extends Filter> int destroyAll(M model, F filter) {
    return getConnector().destroyAll(model, filter);
  }

  public static <M extends PersistedModel<M>> int destroyById(M model, Object id) {
    return getConnector().destroyById(model, id);
  }

  public boolean destroy() {
    return getConnector().destroy(this, this.getId());
  }

  public Object getId() {
    return null;
  }

  public String getIdName() {
    return null;
  }

  public M save() {
    return getConnector().save((M)this);
  }

  public M reload() {
    return getConnector().save((M)this);
  }

  public boolean isNewRecord() {
    return false;
  }

  public <F extends Filter> M updateAttributes(M model, F filter, Map<String, Object> data)
      throws LoopbackException {
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
    Field declaredField =  null;
    try {

      declaredField = this.getClass().getDeclaredField(attributeName);
      NonDB nonDB = declaredField.getAnnotation(NonDB.class);
      if(nonDB != null) {
        throw new LoopbackException("Attribute " + attributeName + " can be updated");
      }
      boolean accessible = declaredField.isAccessible();
      declaredField.setAccessible(true);
      declaredField.set(this, declaredField.getType().cast(attributeValue));
      declaredField.setAccessible(accessible);
      return (M)this;
    } catch (NoSuchFieldException
        | SecurityException
        | IllegalArgumentException
        | IllegalAccessException e) {
      throw new LoopbackException(e);
    }

  }

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

  @SneakyThrows
  public String toString() {
    return String.valueOf(getFieldMap());
  }
}
