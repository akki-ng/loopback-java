package com.flipkart.loopback.exception.configuration;

import com.flipkart.loopback.exception.LoopbackRuntimeException;
import com.flipkart.loopback.model.PersistedModel;
import java.text.MessageFormat;
import lombok.AllArgsConstructor;

/**
 * Created by akshaya.sharma on 17/03/18
 */

@AllArgsConstructor
public class ConnectorNotFoundException extends LoopbackRuntimeException {
  protected final Class<? extends PersistedModel> modelClass;

  @Override
  public String getMessage() {
    return MessageFormat.format("No connector defined for model {0}", modelClass.getSimpleName());
  }
}
