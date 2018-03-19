package com.flipkart.loopback.exception.model;

import com.flipkart.loopback.exception.LoopbackException;
import com.flipkart.loopback.exception.LoopbackRuntimeException;
import com.flipkart.loopback.model.PersistedModel;
import java.text.MessageFormat;
import lombok.AllArgsConstructor;

/**
 * Created by akshaya.sharma on 18/03/18
 */
@AllArgsConstructor
public class InternalError extends LoopbackRuntimeException {
  private final Class<? extends PersistedModel> modelClass;
  private Throwable error;

  @Override
  public String getMessage() {
    return MessageFormat.format("model {0} is internal error {1}", modelClass.getSimpleName(),
        error.getMessage());
  }

  @Override
  public synchronized Throwable getCause() {
    return error;
  }
}
