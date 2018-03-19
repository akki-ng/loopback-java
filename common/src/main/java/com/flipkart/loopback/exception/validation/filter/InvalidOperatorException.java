package com.flipkart.loopback.exception.validation.filter;

import com.flipkart.loopback.exception.LoopbackException;
import java.text.MessageFormat;
import lombok.AllArgsConstructor;

/**
 * Created by akshaya.sharma on 18/03/18
 */

@AllArgsConstructor
public class InvalidOperatorException extends LoopbackException {
  private final String operator;

  @Override
  public String getMessage() {
    return MessageFormat.format("No operator found with value {0}", operator);
  }
}
