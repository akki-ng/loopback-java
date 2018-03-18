package com.flipkart.loopback.exception;

import java.text.MessageFormat;
import lombok.AllArgsConstructor;

/**
 * Created by akshaya.sharma on 18/03/18
 */

@AllArgsConstructor
public class InvalidFilterException extends LoopbackException {
  private final String reason;

  @Override
  public String getMessage() {
    return MessageFormat.format("Invalid filter cause - {0}", reason);
  }
}
