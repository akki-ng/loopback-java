package com.flipkart.loopback.constants;

import com.flipkart.loopback.exception.validation.filter.InvalidOrderFilter;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * Created by akshaya.sharma on 20/03/18
 */

@AllArgsConstructor
public enum OrderType {
  ASC("ASC"),
  DESC("DESC");

  @Getter
  private String value;

  public static OrderType fromValue(String value) {
    for(int i = 0; i < OrderType.values().length; i++) {
      if(OrderType.values()[i].equals(value)) {
        return OrderType.values()[i];
      }
    }
    throw new InvalidOrderFilter();
  }
}
