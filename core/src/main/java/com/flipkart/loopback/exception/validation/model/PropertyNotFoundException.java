package com.flipkart.loopback.exception.validation.model;

import com.flipkart.loopback.exception.LoopbackException;
import com.flipkart.loopback.exception.LoopbackRuntimeException;
import com.flipkart.loopback.model.PersistedModel;
import java.text.MessageFormat;
import lombok.AllArgsConstructor;

/**
 * Created by akshaya.sharma on 17/03/18
 */
@AllArgsConstructor
public class PropertyNotFoundException extends LoopbackRuntimeException {
  protected final Class<? extends PersistedModel> modelClass;
  protected final String propertyName;

  @Override
  public String getMessage() {
    return MessageFormat.format("No property found with name {0} of model {1}", propertyName,
        modelClass.getSimpleName());
  }
}
