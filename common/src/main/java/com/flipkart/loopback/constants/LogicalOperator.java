package com.flipkart.loopback.constants;

import com.flipkart.loopback.exception.LoopbackException;
import lombok.Getter;

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

  public static LogicalOperator fromValue(String value) throws LoopbackException {
    for (LogicalOperator c: LogicalOperator.values()) {
      if(c.getValue().equals(value)) {
        return c;
      }
    }
    return null;
//    throw new LoopbackException(value + " is not a valid condition");
  }
}
