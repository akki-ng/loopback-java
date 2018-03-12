package com.flipkart.loopback.filter;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.flipkart.loopback.constants.LogicalOperator;
import com.flipkart.loopback.constants.QueryOperator;
import com.flipkart.loopback.exception.LoopbackException;
import java.util.Iterator;
import java.util.Map;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * Created by akshaya.sharma on 07/03/18
 */
@NoArgsConstructor
public class WhereFilter {
  @Getter
  private String raw;

  @Getter
  private JsonNode value;

  public WhereFilter(String whr) {
    this.raw = whr;
    try {
      ObjectMapper mapper = new ObjectMapper();
      value = mapper.readTree(this.raw);
      _validateWhereFilter();
    }catch (Throwable e) {
      e.printStackTrace();
      // throw e
    }
  }

  public WhereFilter(JsonNode whereNode) {
    this(whereNode.toString());
  }

  private void _validateWhereFilter() throws LoopbackException {
    if(value == null) {
      return;
    }
    if(value.isObject()) {
      Iterator<Map.Entry<String,JsonNode>> nodeItr = value.fields();
      while(nodeItr.hasNext()) {
        Map.Entry<String,JsonNode> entry = nodeItr.next();
        // check if key is a condition
        LogicalOperator operator = LogicalOperator.fromValue(entry.getKey());
        if(operator != null) {
          JsonNode value = entry.getValue();
          if(value.isArray()) {
            Iterator<JsonNode> condItr = value.iterator();
            while (condItr.hasNext()){
              JsonNode cond = condItr.next();
              _validatePrimitive(cond.fields());
            }
          }else {
            throw new LoopbackException("Invalid where filter, " + operator.getValue() + " expects an"
                + " array of conditions");
          }
        }else {
          // validate primitive
          JsonNode value = entry.getValue();
          _validatePrimitive(entry);
        }
      }
    }else {
      throw new LoopbackException("Invalid where filter");
    }
  }

  private void _validatePrimitive(Iterator<Map.Entry<String,JsonNode>> keyValueItr) throws LoopbackException {
    while(keyValueItr.hasNext()) {
      Map.Entry<String,JsonNode> entry = keyValueItr.next();
      _validatePrimitive(entry);
    }
  }

  private void _validatePrimitive(Map.Entry<String,JsonNode> entry) throws LoopbackException {
    JsonNode value = entry.getValue();
    if(value.isArray()) {
      throw new LoopbackException("Invalid where filter");
    }else if(value.isObject()) {
      Iterator<Map.Entry<String,JsonNode>> keyValueItr = value.fields();
      while(keyValueItr.hasNext()) {
        Map.Entry<String,JsonNode> fieldEntry = keyValueItr.next();
        QueryOperator queryOp = QueryOperator.fromValue(fieldEntry.getKey());
        if(queryOp == null) {
          throw new LoopbackException("Invalid query op in where filter");
        }
        if(fieldEntry.getValue().isArray() || fieldEntry.getValue().isObject()) {
          throw new LoopbackException("Invalid query op value in where filter");
        }
      }
    }
  }

  public static void main(String[] args) {
    String str1 = "{\"a\": {\"gt\": {}}}";
    WhereFilter f1 = new WhereFilter(str1);
    System.out.println(f1.getValue());
  }
}
