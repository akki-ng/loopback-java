package com.flipkart.loopback.dropwizard.exception;

import com.flipkart.loopback.exception.LoopbackException;

/**
 * Created by akshaya.sharma on 18/03/18
 */

public class WrapperException extends Throwable {
  public WrapperException(LoopbackException cause) {
    super(cause);
  }
}
