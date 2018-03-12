package com.flipkart.loopback.constants;

import com.flipkart.loopback.exception.LoopbackException;
import lombok.Getter;

/**
 * Created by akshaya.sharma on 08/03/18
 */

public enum FilterKeys {
  WHERE("where"),
  LIMIT("limit"),
  SKIP("skip"),
  FIELDS("fields");

  @Getter
  private String value;

  FilterKeys(String value) {
    this.value = value;
  }

  public static FilterKeys fromValue(String value) throws LoopbackException {
    for (FilterKeys c: FilterKeys.values()) {
      if(c.getValue().equals(value)) {
        return c;
      }
    }
    return null;
  }
}
