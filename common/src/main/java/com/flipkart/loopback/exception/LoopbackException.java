package com.flipkart.loopback.exception;

/**
 * Created by akshaya.sharma on 02/03/18
 */

public abstract class LoopbackException extends Throwable{
  @Override
  public String getLocalizedMessage() {
    return this.getMessage();
  }

  @Override
  public synchronized Throwable getCause() {
    return this;
  }

  @Override
  public String toString() {
    return this.getMessage();
  }
}
