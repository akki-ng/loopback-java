package com.flipkart.sample.entity.configuration;

import com.flipkart.loopback.configuration.ModelConfigurationImpl;
import com.flipkart.loopback.connector.Connector;
import com.flipkart.loopback.connector.JPAConnector;
import com.flipkart.loopback.constants.IDType;
import com.flipkart.loopback.exception.LoopbackException;
import com.flipkart.loopback.model.PersistedModel;
import com.flipkart.sample.entity.TempRoles;

/**
 * Created by akshaya.sharma on 26/03/18
 */

public class TempRoleConfiguration extends ModelConfigurationImpl<TempRoleConfiguration> {
  public TempRoleConfiguration() throws LoopbackException {
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
    return "temp_roles";
  }

  @Override
  public Class<? extends PersistedModel> getModelClass() {
    return TempRoles.class;
  }

  @Override
  public IDType getIDType() {
    return IDType.NUMBER;
  }
}
