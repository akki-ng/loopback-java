package com.flipkart.loopback.exception;

import com.flipkart.loopback.model.PersistedModel;
import java.text.MessageFormat;
import lombok.AllArgsConstructor;

/**
 * Created by akshaya.sharma on 18/03/18
 */

@AllArgsConstructor
public class CouldNotPerformException extends LoopbackException {
  private final Class<? extends PersistedModel> modelClass;
  private final String operationName;
  private final Throwable e;

  @Override
  public String getMessage() {
    return MessageFormat.format("Error while performing {0} on model {1} with message {2}",
        operationName, modelClass.getSimpleName(), e.getMessage());
  }
}
