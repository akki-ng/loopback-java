package com.flipkart.loopback.filter;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.flipkart.loopback.constants.FilterKeys;
import com.flipkart.loopback.exception.LoopbackException;
import com.flipkart.loopback.exception.validation.filter.InvalidFilterException;
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

  public Filter() throws InvalidFilterException {
    this("{\"where\": {}}");
  }

  public Filter(String data) throws InvalidFilterException {

    try {
      ObjectMapper mapper = new ObjectMapper();
      JsonNode value = mapper.readTree(data);
      _validateFilter(value);
    } catch (IOException e) {
      e.printStackTrace();
      throw new InvalidFilterException(e.getMessage());
    }
  }

  private void _validateFilter(JsonNode value) throws InvalidFilterException {
    if(value == null) {
      return;
    }
    if(value.isObject()) {
      Iterator<Map.Entry<String,JsonNode>> nodeItr = value.fields();
      while(nodeItr.hasNext()) {
        Map.Entry<String,JsonNode> entry = nodeItr.next();
        // check if key is a condition
        try {
          FilterKeys filterKey = FilterKeys.fromValue(entry.getKey());
          if(filterKey == FilterKeys.WHERE) {
            this.where = new WhereFilter(entry.getValue());
          }else if(filterKey == FilterKeys.LIMIT) {
            if(!entry.getValue().isInt()) {
              throw new InvalidFilterException("value for limit must be a number");
            }
            this.limit = entry.getValue().asInt();
          }else if(filterKey == FilterKeys.SKIP) {
            if(!entry.getValue().isInt()) {
              throw new InvalidFilterException("value for skip must be a number");
            }
            this.skip = entry.getValue().asInt();
          }else if(filterKey == FilterKeys.FIELDS) {
            if(!entry.getValue().isArray()) {
              throw new InvalidFilterException("Fields expects a list of strings");
            }
            this.fields = new ObjectMapper().convertValue(entry.getValue(), ArrayList.class);
          }
        } catch (InvalidFilterException e) {
          e.printStackTrace();
          throw e;
        }
      }
    }else {
      throw new InvalidFilterException("Invalid filter object");
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
