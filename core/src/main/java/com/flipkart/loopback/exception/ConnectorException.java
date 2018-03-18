package com.flipkart.loopback.exception;

import lombok.AllArgsConstructor;

/**
 * Created by akshaya.sharma on 17/03/18
 */

@AllArgsConstructor
public class ConnectorException extends LoopbackException {
  protected final Throwable exception;

  @Override
  public String getMessage() {
    return exception.getMessage();
  }
}
