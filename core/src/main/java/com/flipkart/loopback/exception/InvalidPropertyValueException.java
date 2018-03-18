package com.flipkart.loopback.exception;

import com.flipkart.loopback.model.PersistedModel;
import java.io.Serializable;
import java.text.MessageFormat;
import lombok.AllArgsConstructor;

/**
 * Created by akshaya.sharma on 17/03/18
 */

@AllArgsConstructor
public class InvalidPropertyValueException extends LoopbackException {
  protected final Class<? extends PersistedModel> modelClass;
  protected final String propertyName;
  protected final Serializable propertyValue;
  protected final String reason;

  @Override
  public String getMessage() {
    return  MessageFormat.format("Property {0} in Model {1} is not valid with value {2} because "
            + "of {3}", propertyName, modelClass.getSimpleName(), propertyValue, reason);
  }
}
