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
  public abstract Connector getConnector();

  @JsonIgnore
  public abstract List<Relation> getRelations();

  @JsonIgnore
  public abstract Relation getRelationByName(String relationName) throws LoopbackException;

  @JsonIgnore
  public abstract Relation getRelationByRestPath(String restPath) throws LoopbackException;

  @JsonIgnore
  public String getTableName();

  @JsonIgnore
  public Class<? extends PersistedModel> getModelClass();

  @JsonIgnore
  public Map<String, Field> getProperties();

  @JsonIgnore
  public String getIdPropertyName();
}
