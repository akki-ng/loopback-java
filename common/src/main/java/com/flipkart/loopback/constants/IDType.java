package com.flipkart.loopback.constants;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * Created by akshaya.sharma on 02/03/18
 */
@AllArgsConstructor
public enum IDType {
  NUMBER(Number.class), STRING(String.class), OBJECT_ID(String.class);

  @Getter
  private Class javaType;

  public boolean isValidSubclass(Class clazz) {
    return clazz.isAssignableFrom(javaType);
  }
}
