package com.flipkart.loopback.constants;

import java.io.Serializable;
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

  public boolean isConvertible(Serializable id) {
    if(id == null) {
      return false;
    }
    Class clazz = id.getClass();
    if(clazz.isAssignableFrom(javaType)) {
      return true;
    }
    if(javaType.equals(Number.class)) {
      try {
        Number num = Long.parseLong(id.toString());
      }catch (NumberFormatException e) {
        return false;
      }
    }
    return true;
  }
}
