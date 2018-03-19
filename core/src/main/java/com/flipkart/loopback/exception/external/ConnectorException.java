package com.flipkart.loopback.exception.external;

import com.flipkart.loopback.exception.LoopbackException;
import com.flipkart.loopback.exception.LoopbackRuntimeException;
import lombok.AllArgsConstructor;

/**
 * Created by akshaya.sharma on 17/03/18
 */

public class ConnectorException extends LoopbackRuntimeException {
  public ConnectorException() {
    super();
  }

  public ConnectorException(String message) {
    super(message);
  }

  public ConnectorException(String message, Throwable cause) {
    super(message, cause);
  }

  public ConnectorException(Throwable cause) {
    super(cause);
  }

  public ConnectorException(String message, Throwable cause, boolean enableSuppression,
      boolean writableStackTrace) {
    super(message, cause, enableSuppression, writableStackTrace);
  }
}
