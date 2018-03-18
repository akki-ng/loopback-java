package com.flipkart.loopback.constants;

import com.flipkart.loopback.exception.InvalidOperatorException;
import com.flipkart.loopback.exception.LoopbackException;
import lombok.Getter;
import org.jetbrains.annotations.NotNull;

/**
 * Created by akshaya.sharma on 07/03/18
 */

public enum LogicalOperator {
  OR("or"),
  AND("and");

  @Getter
  private String value;

  LogicalOperator(String value) {
    this.value = value;
  }

  public static LogicalOperator fromValue(@NotNull String value) throws InvalidOperatorException {
    for (LogicalOperator c: LogicalOperator.values()) {
      if(c.getValue().equals(value)) {
        return c;
      }
    }
    throw new InvalidOperatorException(value);
  }
}
