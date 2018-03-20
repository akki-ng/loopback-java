package com.flipkart.loopback.constants;

import com.flipkart.loopback.exception.validation.filter.InvalidFilterException;
import lombok.Getter;
import org.jetbrains.annotations.NotNull;

/**
 * Created by akshaya.sharma on 08/03/18
 */

public enum FilterKeys {
  WHERE("where"),
  LIMIT("limit"),
  SKIP("skip"),
  FIELDS("fields"),
  ORDER("order");

  @Getter
  private String value;

  FilterKeys(String value) {
    this.value = value;
  }

  public static FilterKeys fromValue(@NotNull String value) throws InvalidFilterException {
    for (FilterKeys c: FilterKeys.values()) {
      if(c.getValue().equals(value)) {
        return c;
      }
    }
    throw new InvalidFilterException(value);
  }
}
