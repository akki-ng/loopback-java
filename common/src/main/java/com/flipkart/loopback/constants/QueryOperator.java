package com.flipkart.loopback.constants;

import com.flipkart.loopback.exception.LoopbackException;
import lombok.Getter;

/**
 * Created by akshaya.sharma on 07/03/18
 */

public enum QueryOperator {
  GT("gt"),
  GTE("gte"),
  LT("lt"),
  LTE("lte"),
  BETWEEN("between"),
  INQ("inq"),
  NIN("nin"),
  NEAR("near"),
  NEQ("neq"),
  LIKE("like"),
  NLIKE("nlike"),
  ILIKE("ilike"),
  INLIKE("inlike"),
  REGEXP("regexp")
  ;

  @Getter
  private String value;

  QueryOperator(String value) {
    this.value = value;
  }

  public static QueryOperator fromValue(String value) throws LoopbackException {
    for (QueryOperator c: QueryOperator.values()) {
      if(c.getValue().equals(value)) {
        return c;
      }
    }
    return null;
//    throw new LoopbackException(value + " is not a valid condition");
  }
}
