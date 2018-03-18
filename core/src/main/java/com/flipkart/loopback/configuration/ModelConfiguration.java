package com.flipkart.loopback.configuration;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.flipkart.loopback.connector.Connector;
import com.flipkart.loopback.exception.LoopbackException;
import com.flipkart.loopback.model.PersistedModel;
import com.flipkart.loopback.relation.Relation;
import java.lang.reflect.Field;
import java.util.List;
import java.util.Map;

/**
 * Created by akshaya.sharma on 08/03/18
 */

public interface ModelConfiguration {
  @JsonIgnore
  public abstract Connector getConnector ();

  @JsonIgnore
  public String getTableName();

  @JsonIgnore
  public Class<? extends PersistedModel> getModelClass();

  @JsonIgnore
  public String getIdPropertyName();
}
