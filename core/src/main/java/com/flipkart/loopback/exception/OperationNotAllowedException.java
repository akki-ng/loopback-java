package com.flipkart.loopback.exception;

import com.flipkart.loopback.model.PersistedModel;
import java.text.MessageFormat;
import lombok.AllArgsConstructor;

/**
 * Created by akshaya.sharma on 18/03/18
 */

@AllArgsConstructor
public class OperationNotAllowedException extends LoopbackException {
  private final Class<? extends PersistedModel> modelClass;
  private final String operationName;
  private final String reason;

  @Override
  public String getMessage() {
    return MessageFormat.format("Operation {0} is not allowed on model {1} because {2}",
        operationName, modelClass.getSimpleName(), reason);
  }
}
