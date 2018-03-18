package com.flipkart.loopback.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.flipkart.loopback.configuration.ModelConfiguration;
import com.flipkart.loopback.configuration.manager.ModelConfigurationManager;
import com.flipkart.loopback.connector.Connector;
import com.flipkart.loopback.exception.ConnectorNotFoundException;
import com.flipkart.loopback.exception.InternalError;
import com.flipkart.loopback.exception.ModelNotConfiguredException;
import com.flipkart.loopback.model.provider.ModelProvider;

/**
 * Created by akshaya.sharma on 02/03/18
 */
public abstract class Model<T extends Model<T, CM>, CM extends ModelConfigurationManager> {

  @JsonIgnore
  protected static Connector getConnector(
      Class<? extends PersistedModel> modelClass) throws InternalError {
    try {
      return ModelProvider.getInstance().getConnectorFor(modelClass);
    } catch (ConnectorNotFoundException | ModelNotConfiguredException e) {
      e.printStackTrace();
      throw new InternalError(modelClass, e);
    }
  }

  @JsonIgnore
  public Connector getConnector() throws InternalError {
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
  protected static ModelConfiguration getConfiguration(
      Class<? extends PersistedModel> modelClass) throws InternalError {
    try {
      return ModelProvider.getInstance().getConfigurationFor(modelClass);
    } catch (ModelNotConfiguredException e) {
      e.printStackTrace();
      throw new InternalError(modelClass, e);
    }
  }

  @JsonIgnore
  public static ModelProvider getProvider() {
    return ModelProvider.getInstance();
  }

}
