package com.flipkart.loopback.exception.model.persistence;

import com.flipkart.loopback.exception.LoopbackException;
import com.flipkart.loopback.model.PersistedModel;
import java.io.Serializable;
import java.text.MessageFormat;
import lombok.AllArgsConstructor;

/**
 * Created by akshaya.sharma on 17/03/18
 */

@AllArgsConstructor
public class ModelNotFoundException extends LoopbackException {
  protected final Class<? extends PersistedModel> modelClass;
  protected final Serializable id;

  @Override
  public String getMessage() {
    return MessageFormat.format("Model {0} not found for identifier {1}", modelClass.getSimpleName
            (), id);
  }
}

