package com.flipkart.loopback.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.flipkart.loopback.configuration.ModelConfiguration;
import com.flipkart.loopback.configuration.manager.ModelConfigurationManager;
import com.flipkart.loopback.connector.Connector;
import com.flipkart.loopback.connector.MysqlConnector;
import com.flipkart.loopback.exception.LoopbackException;
import com.flipkart.loopback.model.provider.ModelProvider;
import com.flipkart.loopback.relation.Relation;
import java.util.List;
import javax.persistence.Id;
import lombok.NoArgsConstructor;

/**
 * Created by akshaya.sharma on 02/03/18
 */
  public abstract class Model<T extends Model<T, CM>, CM extends ModelConfigurationManager> {

  @JsonIgnore
  private static Connector getConnector(Class<? extends Model> modelClass) {
    return ModelProvider.getInstance().getConnectorFor(modelClass);
  }

  @JsonIgnore
  public Connector getConnector() {
    return getConnector(this.getClass());
  }

  @JsonIgnore
  public static <CM extends ModelConfigurationManager> CM getConfigurationManager() {
    return (CM) CM.getInstance();
  }

  @JsonIgnore
  private static ModelConfiguration getConfiguration(Class<? extends Model> modelClass) {
    return ModelProvider.getInstance().getConfigurationFor(modelClass);
  }

  @JsonIgnore
  public ModelConfiguration getConfiguration() {
    return getConfiguration(this.getClass());
  }

  @JsonIgnore
  public static ModelProvider getProvider() {
    return ModelProvider.getInstance();
  }

}
