package com.flipkart.loopback.exception.configuration;

import com.flipkart.loopback.exception.LoopbackException;
import com.flipkart.loopback.exception.LoopbackRuntimeException;
import lombok.AllArgsConstructor;

/**
 * Created by akshaya.sharma on 18/03/18
 */

@AllArgsConstructor
public class ConfigurationException extends LoopbackRuntimeException {
  private final String message;

  @Override
  public String getMessage() {
    return message;
  }
}
