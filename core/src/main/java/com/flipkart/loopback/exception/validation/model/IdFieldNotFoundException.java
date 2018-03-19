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
public class IdFieldNotFoundException extends LoopbackRuntimeException {
  protected final Class<? extends PersistedModel> modelClass;

  @Override
  public String getMessage() {
    return MessageFormat.format("No id field exists for model {0}", modelClass.getSimpleName());
  }
}
