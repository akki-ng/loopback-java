package com.flipkart.loopback.exception;

import com.flipkart.loopback.model.PersistedModel;
import java.io.Serializable;
import java.text.MessageFormat;

/**
 * Created by akshaya.sharma on 17/03/18
 */

public class ReadOnlyPropertyException extends InvalidPropertyValueException {

  public ReadOnlyPropertyException(
      Class<? extends PersistedModel> modelClass,
      String propertyName, Serializable propertyValue) {
    super(modelClass, propertyName, propertyValue, "Read-Only property");
  }
}
