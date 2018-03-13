package com.flipkart.sample.entity.configuration;

import com.flipkart.loopback.configuration.ModelConfigurationImpl;
import com.flipkart.loopback.connector.Connector;
import com.flipkart.loopback.connector.JPAConnector;
import com.flipkart.loopback.constants.RelationType;
import com.flipkart.loopback.exception.LoopbackException;
import com.flipkart.loopback.model.PersistedModel;
import com.flipkart.loopback.relation.Relation;
import com.flipkart.sample.entity.TempModel;
import com.flipkart.sample.entity.TestModel;

/**
 * Created by akshaya.sharma on 02/03/18
 */

public class TestModelConfiguration extends ModelConfigurationImpl<TestModelConfiguration> {
  public TestModelConfiguration() throws LoopbackException {
    super();
  }

  @Override
  protected void configure() throws LoopbackException {
    this.addRelation(
        Relation.builder()
            .name("temp")
            .relatedModelClass(TempModel.class)
            .relationType(RelationType.BELONGS_TO)
            .build()
    );
  }

  @Override
  public Connector getConnector() {
    return JPAConnector.getInstance("DEFAULT_LOCAL");
  }

  @Override
  public String getTableName() {
    return "test";
  }

  @Override
  public Class<? extends PersistedModel> getModelClass() {
    return TestModel.class;
  }
}