package com.flipkart.loopback.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.flipkart.loopback.configuration.ModelConfiguration;
import com.flipkart.loopback.configuration.manager.ModelConfigurationManager;
import com.flipkart.loopback.connector.Connector;
import com.flipkart.loopback.exception.model.InternalError;
import com.flipkart.loopback.model.provider.ModelProvider;
import java.io.Serializable;

/**
 * Created by akshaya.sharma on 02/03/18
 */
public abstract class Model<T extends Model<T, CM>, CM extends ModelConfigurationManager>
    implements Serializable {

  @JsonIgnore
  public static Connector getConnector(Class<? extends PersistedModel> modelClass) {
    return ModelProvider.getInstance().getConnectorFor(modelClass);
  }

  @JsonIgnore
  public Connector getConnector() {
    if (this instanceof PersistedModel) {
      return getConnector((Class<PersistedModel>) this.getClass());
    }
    throw new InternalError(PersistedModel.class, new Throwable("Connector not found"));
  }

  @JsonIgnore
  public static <CM extends ModelConfigurationManager> CM getConfigurationManager() {
    return (CM) CM.getInstance();
  }

  @JsonIgnore
  public static ModelConfiguration getConfiguration(Class<? extends PersistedModel> modelClass) {
    return ModelProvider.getInstance().getConfigurationFor(modelClass);
  }

  @JsonIgnore
  public static ModelProvider getProvider() {
    return ModelProvider.getInstance();
  }

}
