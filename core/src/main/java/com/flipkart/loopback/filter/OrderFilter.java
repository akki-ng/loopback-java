package com.flipkart.loopback.filter;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.flipkart.loopback.exception.validation.filter.InvalidFilterException;
import java.util.ArrayList;
import lombok.AllArgsConstructor;
import lombok.Data;
import java.util.List;
import org.jetbrains.annotations.NotNull;

/**
 * Created by akshaya.sharma on 20/03/18
 */

@Data
public class OrderFilter {
  private List<OrderBy> orderBy;

  public OrderFilter() {
    orderBy = null;
  }

  public OrderFilter(@NotNull JsonNode value) {
    _validateOrderBy(value);
  }

  private void _validateOrderBy(JsonNode value) {
    if(!value.isArray()) {
      throw new InvalidFilterException("Order expects a list of {property:.., type: "
          + "ASC|DESC}");
    }

    this.orderBy = new ObjectMapper().convertValue(value, new TypeReference<List<OrderBy>>(){});
  }
}
