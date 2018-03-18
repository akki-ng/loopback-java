package com.flipkart.loopback.exception;

import lombok.AllArgsConstructor;

/**
 * Created by akshaya.sharma on 18/03/18
 */

@AllArgsConstructor
public class ConfigurationException extends LoopbackException {
  private final String message;

  @Override
  public String getMessage() {
    return message;
  }
}
