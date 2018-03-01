package com.flipkart.loopback.exception;

/**
 * Created by akshaya.sharma on 02/03/18
 */

public class LoopbackException extends Throwable{
  public LoopbackException(String message) {
    super(message);
  }

  public LoopbackException(String message, Throwable cause) {
    super(message, cause);
  }

  public LoopbackException(Throwable cause) {
    super(cause);
  }

  public LoopbackException(String message, Throwable cause, boolean enableSuppression, boolean
      writableStackTrace) {
    super(message, cause, enableSuppression, writableStackTrace);
  }
}
