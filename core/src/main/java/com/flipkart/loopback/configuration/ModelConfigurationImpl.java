package com.flipkart.loopback.configuration;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.flipkart.loopback.exception.ConfigurationException;
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
  protected abstract void configure() throws LoopbackException;

  public ModelConfigurationImpl() throws LoopbackException {
    this.configure();
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
