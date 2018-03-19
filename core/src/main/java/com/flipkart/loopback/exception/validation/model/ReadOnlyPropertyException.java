package com.flipkart.loopback.exception.validation.model;

import com.flipkart.loopback.exception.validation.model.InvalidPropertyValueException;
import com.flipkart.loopback.model.PersistedModel;
import java.io.Serializable;

/**
 * Created by akshaya.sharma on 17/03/18
 */

public class ReadOnlyPropertyException extends InvalidPropertyValueException {

  public ReadOnlyPropertyException(
      Class<? extends PersistedModel> modelClass,
      String propertyName, Object propertyValue) {
    super(modelClass, propertyName, propertyValue, "Read-Only property");
  }
}
