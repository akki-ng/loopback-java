package com.flipkart.loopback.dropwizard.exception;

import com.flipkart.loopback.exception.LoopbackException;

/**
 * Created by akshaya.sharma on 18/03/18
 */

public class WrapperException extends Throwable {
  public WrapperException() {
  }

  public WrapperException(String message) {
    super(message);
  }

  public WrapperException(String message, Throwable cause) {
    super(message, cause);
  }

  public WrapperException(Throwable cause) {
    super(cause);
  }

  public WrapperException(String message, Throwable cause, boolean enableSuppression,
      boolean writableStackTrace) {
    super(message, cause, enableSuppression, writableStackTrace);
  }
}
