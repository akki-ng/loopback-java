package com.flipkart.loopback.filter;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.flipkart.loopback.constants.OrderType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Created by akshaya.sharma on 20/03/18
 */

@AllArgsConstructor
@Data
@NoArgsConstructor
public class OrderBy {
  @JsonProperty
  private String property;

  @JsonProperty
  private OrderType type;
}
