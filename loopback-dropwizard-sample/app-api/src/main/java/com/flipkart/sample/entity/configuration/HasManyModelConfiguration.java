package com.flipkart.sample.entity.configuration;

import com.flipkart.loopback.configuration.ModelConfigurationImpl;
import com.flipkart.loopback.connector.Connector;
import com.flipkart.loopback.connector.JPAConnector;
import com.flipkart.loopback.exception.LoopbackException;
import com.flipkart.loopback.model.PersistedModel;
import com.flipkart.sample.entity.HasManyModel;

/**
 * Created by akshaya.sharma on 15/03/18
 */

public class HasManyModelConfiguration extends ModelConfigurationImpl<HasManyModelConfiguration> {
  public HasManyModelConfiguration() throws LoopbackException {
    super();
  }

  @Override
  protected void configure() throws LoopbackException {

  }

  @Override
  public Connector getConnector() {
    return JPAConnector.getInstance("DEFAULT_LOCAL");
  }

  @Override
  public String getTableName() {
    return "many";
  }

  @Override
  public Class<? extends PersistedModel> getModelClass() {
    return HasManyModel.class;
  }
}
