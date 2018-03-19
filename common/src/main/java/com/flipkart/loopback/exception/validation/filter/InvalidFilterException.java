package com.flipkart.loopback.exception.validation.filter;

import com.flipkart.loopback.exception.LoopbackException;
import com.flipkart.loopback.exception.LoopbackRuntimeException;
import java.text.MessageFormat;
import lombok.AllArgsConstructor;

/**
 * Created by akshaya.sharma on 18/03/18
 */

@AllArgsConstructor
public class InvalidFilterException extends LoopbackRuntimeException {
  private final String reason;

  @Override
  public String getMessage() {
    return MessageFormat.format("Invalid filter because - {0}",
        reason);
  }
}
