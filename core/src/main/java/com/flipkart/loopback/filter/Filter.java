package com.flipkart.loopback.filter;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.flipkart.loopback.constants.FilterKeys;
import com.flipkart.loopback.exception.LoopbackException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Map;
import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * Created by akshaya.sharma on 02/03/18
 */

@Data
@AllArgsConstructor
public class Filter {

  private WhereFilter where;

  private Integer limit;

  private Integer skip;

  // TODO List is not visible
  private ArrayList<String> fields;

  public Filter(String data) throws IOException, LoopbackException {
    System.out.println("asdas");
    ObjectMapper mapper = new ObjectMapper();
    JsonNode value = mapper.readTree(data);
    _validateFilter(value);
  }

  private void _validateFilter(JsonNode value) throws LoopbackException {
    if(value == null) {
      return;
    }
    if(value.isObject()) {
      Iterator<Map.Entry<String,JsonNode>> nodeItr = value.fields();
      while(nodeItr.hasNext()) {
        Map.Entry<String,JsonNode> entry = nodeItr.next();
        // check if key is a condition
        FilterKeys filterKey = FilterKeys.fromValue(entry.getKey());
        if(filterKey != null) {
          if(filterKey == FilterKeys.WHERE) {
            this.where = new WhereFilter(entry.getValue());
          }else if(filterKey == FilterKeys.LIMIT) {
            if(!entry.getValue().isInt()) {
              throw new LoopbackException("value for limit must be a number");
            }
            this.limit = entry.getValue().asInt();
          }else if(filterKey == FilterKeys.SKIP) {
            if(!entry.getValue().isInt()) {
              throw new LoopbackException("value for limit must be a number");
            }
            this.skip = entry.getValue().asInt();
          }else if(filterKey == FilterKeys.FIELDS) {
            if(!entry.getValue().isArray()) {
              throw new LoopbackException("Fields expects a list of strings");
            }
            this.fields = new ObjectMapper().convertValue(entry.getValue(), ArrayList
                .class);
          }
        }else {
          throw new LoopbackException("Invalid key in filter");
        }
      }
    }else {
      throw new LoopbackException("Invalid filter");
    }
  }

  public Filter merge(Filter obj) throws IOException, LoopbackException {
    // TODO deep merge
    return null;
  }

  public static void main(String[] args) throws IOException, LoopbackException {
    String str1 = "{\"where\": {\"a\": {\"gt\": 0}}, \"limit\": 1, \"fields\": [\"id\"]}";
    Filter f1 = new Filter(str1);


//    String str2 = "{\"where\": {\"b\": {\"gt\": 1}}}";
//    Filter f2 = new Filter(str2);
//
//    Filter f3 = f1.merge(f2);
    System.out.println(f1);
//    System.out.println(f2.getValue());
//    System.out.println(f3.getValue());
  }
}
