package com.flipkart.loopback.exception;

/**
 * Created by akshaya.sharma on 19/03/18
 */

public class LoopbackRuntimeException extends RuntimeException {

  protected LoopbackRuntimeException() {
  }

  protected LoopbackRuntimeException(String message) {
    super(message);
  }

  protected LoopbackRuntimeException(String message, Throwable cause) {
    super(message, cause);
  }

  protected LoopbackRuntimeException(Throwable cause) {
    super(cause);
  }

  protected LoopbackRuntimeException(String message, Throwable cause, boolean enableSuppression,
      boolean writableStackTrace) {
    super(message, cause, enableSuppression, writableStackTrace);
  }

  @Override
  public String getLocalizedMessage() {
    return this.getMessage();
  }

  @Override
  public String toString() {
    return this.getMessage();
  }
}
