package com.flipkart.loopback.exception;

import com.flipkart.loopback.model.PersistedModel;
import java.io.Serializable;

/**
 * Created by akshaya.sharma on 17/03/18
 */

public class TransientPropertyException extends InvalidPropertyValueException {
  public TransientPropertyException(
      Class<? extends PersistedModel> modelClass,
      String propertyName, Serializable propertyValue) {
    super(modelClass, propertyName, propertyValue, " Transient Property");
  }
}
