package com.flipkart.loopback.model;

import com.flipkart.loopback.connector.Connector;
import com.flipkart.loopback.connector.MysqlConnector;
import com.flipkart.loopback.filter.Filter;
import java.util.List;
import java.util.Map;

/**
 * Created by akshaya.sharma on 02/03/18
 */

public class Model<T extends Model<T>> {

  public static Connector getConnector() {
    return new MysqlConnector();
  }
}
