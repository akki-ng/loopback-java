package com.flipkart.loopback.filter;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.flipkart.loopback.constants.LogicalOperator;
import com.flipkart.loopback.constants.QueryOperator;
import com.flipkart.loopback.exception.InvalidFilterException;
import com.flipkart.loopback.exception.InvalidOperatorException;
import com.flipkart.loopback.exception.LoopbackException;
import java.io.IOException;
import java.io.Serializable;
import java.util.Iterator;
import java.util.Map;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.jetbrains.annotations.NotNull;

/**
 * Created by akshaya.sharma on 07/03/18
 */
public class WhereFilter{
  @Getter
  private JsonNode value;

  public WhereFilter() throws InvalidFilterException {
    this("{}");
  }

  public WhereFilter(@NotNull String whr) throws InvalidFilterException {
    ObjectMapper mapper = new ObjectMapper();
    try {
      value = mapper.readTree(whr);
    } catch (IOException e) {
      throw new InvalidFilterException(e.getMessage());
    }
    _validateWhereFilter();
  }

  public WhereFilter(@NotNull  JsonNode whereNode) throws InvalidFilterException {
    this(whereNode.toString());
  }

  private void _validateWhereFilter() throws InvalidFilterException {
    if(value == null) {
      return;
    }
    if(value.isObject()) {
      Iterator<Map.Entry<String,JsonNode>> nodeItr = value.fields();
      while(nodeItr.hasNext()) {
        Map.Entry<String,JsonNode> entry = nodeItr.next();

        try {
          // check if key is a condition
          LogicalOperator operator = LogicalOperator.fromValue(entry.getKey());
          JsonNode value = entry.getValue();
          if(value.isArray()) {
            Iterator<JsonNode> condItr = value.iterator();
            while (condItr.hasNext()){
              JsonNode cond = condItr.next();
              _validatePrimitive(cond.fields());
            }
          }else {
            throw new InvalidFilterException(operator.getValue() + " expects an array of "
                + "conditions");
          }
        } catch (InvalidOperatorException e) {
          e.printStackTrace();

          // validate primitive
          JsonNode value = entry.getValue();
          _validatePrimitive(entry);
        } catch (InvalidFilterException e) {
          e.printStackTrace();
          throw e;
        }
      }
    }else {
      throw new InvalidFilterException("Invalid where filter");
    }
  }

  private void _validatePrimitive(Iterator<Map.Entry<String,JsonNode>> keyValueItr) throws InvalidFilterException {
    while(keyValueItr.hasNext()) {
      Map.Entry<String,JsonNode> entry = keyValueItr.next();
      _validatePrimitive(entry);
    }
  }

  private void _validatePrimitive(Map.Entry<String,JsonNode> entry) throws InvalidFilterException {
    JsonNode value = entry.getValue();
    if(value.isArray()) {
      throw new InvalidFilterException(entry.getKey() + " can not be an array");
    }else if(value.isObject()) {
      Iterator<Map.Entry<String,JsonNode>> keyValueItr = value.fields();
      while(keyValueItr.hasNext()) {
        Map.Entry<String,JsonNode> fieldEntry = keyValueItr.next();
        try {
          QueryOperator queryOp = QueryOperator.fromValue(fieldEntry.getKey());
          if(fieldEntry.getValue().isArray() || fieldEntry.getValue().isObject()) {
            throw new InvalidFilterException(fieldEntry.getKey() + " can not be " + String
                .valueOf(fieldEntry.getValue()) + " in where filter");
          }
        } catch (InvalidOperatorException e) {
          e.printStackTrace();
          throw new InvalidFilterException("Invalid key on filter " + e.getMessage());
        }
      }
    }
  }

  public static void main(String[] args) throws InvalidFilterException {
    String str1 = "{\"a\": [\"gt\"]}";
    WhereFilter f1 = new WhereFilter(str1);

    String str2 = "{\"a\": \"aa\", \"b\": \"akshay\"}";
    WhereFilter f2 = new WhereFilter(str2);

    WhereFilter f3 = f1.merge(f2);

    System.out.println(f3.getValue());
  }

  public WhereFilter copy() throws InvalidFilterException {
    return new WhereFilter(this.value.toString());
  }

  public WhereFilter merge(WhereFilter where) {
    JsonNode mergedNode = _mergeNodes((ObjectNode) this.getValue(), (ObjectNode) where.getValue());
    value = mergedNode;
    return this;
  }

  private ObjectNode _mergeNodes(@NotNull final ObjectNode mainNode,@NotNull final ObjectNode updateNode) {
//    ObjectNode result = null;
//    if(mainNode != null) {
//      result = mainNode.deepCopy();
//    }
//    if(result == null && updateNode != null) {
//      result = updateNode.deepCopy();
//      return result;
//    }

    Iterator<String> fieldNames = updateNode.fieldNames();
    while (fieldNames.hasNext()) {
      String key = fieldNames.next();
      JsonNode value = updateNode.get(key);
      if(!mainNode.has(key)) {
        mainNode.set(key, value);
      } else {
        if(value.isObject()) {
          JsonNode existingVal = mainNode.get(key);
          if(existingVal.isObject()) {
            existingVal = _mergeNodes((ObjectNode) mainNode.get(key), (ObjectNode) value);
          }else if(existingVal.isArray()) {
            existingVal = ((ArrayNode) existingVal).add(value);
          }else if(existingVal.isValueNode()) {
            existingVal = value;
          }
          mainNode.set(key, existingVal);
        } else if(value.isArray()) {
          ArrayNode arrayNode = (ArrayNode) value;
          JsonNode existingVal = mainNode.get(key);
          if(existingVal.isObject()) {
            arrayNode = arrayNode.add(existingVal);
            existingVal = arrayNode;
          }else if(existingVal.isArray()) {
            existingVal = ((ArrayNode) existingVal).addAll(arrayNode);
          }else if(existingVal.isValueNode()) {
            existingVal = arrayNode;
          }
          mainNode.set(key, existingVal);
        } else if(value.isValueNode()) {
          JsonNode existingVal = mainNode.get(key);
          if(existingVal.isObject() || existingVal.isValueNode()) {
            existingVal = value;
          }else if(existingVal.isArray()) {
            existingVal = ((ArrayNode) existingVal).add(value);
          }
          mainNode.set(key, existingVal);
        }
      }
    }
    return mainNode;
  }
}
