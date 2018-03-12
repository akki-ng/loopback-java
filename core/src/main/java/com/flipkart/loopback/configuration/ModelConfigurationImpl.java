package com.flipkart.loopback.configuration;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.flipkart.loopback.exception.LoopbackException;
import com.flipkart.loopback.relation.Relation;
import com.google.common.collect.Maps;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import javax.persistence.Id;
import javax.persistence.Transient;

/**
 * Created by akshaya.sharma on 08/03/18
 */

public abstract class ModelConfigurationImpl<T extends ModelConfiguration>
    implements
    ModelConfiguration {
  private List<Relation> relations = new ArrayList<Relation>();
  protected abstract void configure() throws LoopbackException;

  public ModelConfigurationImpl() throws LoopbackException {
    this.configure();
  }

  protected T addRelation(Relation r) throws LoopbackException {
    if(getRelationByName(r.getName()) != null || getRelationByRestPath(r.getRestPath()) != null) {
      throw new LoopbackException("Relation already exists");
    }
    relations.add(r);

    return (T)this;
  }

  @Override
  public List<Relation> getRelations() {
    return Collections.unmodifiableList(relations);
  }

  @Override
  public Relation getRelationByName(String relationName) {
    Optional<Relation> relOp = getRelations().stream()
        .filter(rel -> rel.getName().equals(relationName))
        .findFirst();
    if(relOp.isPresent()) {
      return relOp.get();
    }
    return null;
  }

  @Override
  public Relation getRelationByRestPath(String restPath) throws LoopbackException {
    Optional<Relation> relOp = getRelations().stream()
        .filter(rel -> rel.getRestPath().equals(restPath))
        .findFirst();
    if(relOp.isPresent()) {
      return relOp.get();
    }
    return null;
  }

  @Override
  public Map<String, Field> getProperties() {
    Map<String, Field> properties = Maps.newConcurrentMap();
    for (Field declaredField : getModelClass().getDeclaredFields()) {
      Transient aTransient = declaredField.getAnnotation(Transient.class);
      if(aTransient != null) {
        continue;
      }
      String propertyName = declaredField.getName();

      JsonProperty jsonProperty = declaredField.getAnnotation(JsonProperty.class);
      if(jsonProperty != null) {
        propertyName = jsonProperty.value();
      }

      properties.put(propertyName, declaredField);
    }
    // TODO return immutable map
    return properties;
  }

  @Override
  public String getIdPropertyName() {
    for (Field declaredField : getModelClass().getDeclaredFields()) {
      Transient aTransient = declaredField.getAnnotation(Transient.class);
      Id id = declaredField.getAnnotation(Id.class);
      if(aTransient != null || id == null) {
        continue;
      }
      String propertyName = declaredField.getName();

      JsonProperty jsonProperty = declaredField.getAnnotation(JsonProperty.class);
      if(jsonProperty != null) {
        propertyName = jsonProperty.value();
      }
      return propertyName;
    }
    return null;
  }
}
