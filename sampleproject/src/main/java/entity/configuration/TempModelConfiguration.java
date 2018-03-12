package entity.configuration;

import com.flipkart.loopback.configuration.ModelConfigurationImpl;
import com.flipkart.loopback.connector.Connector;
import com.flipkart.loopback.connector.JPAConnector;
import com.flipkart.loopback.constants.RelationType;
import com.flipkart.loopback.exception.LoopbackException;
import com.flipkart.loopback.model.PersistedModel;
import com.flipkart.loopback.relation.Relation;
import entity.TempModel;
import entity.TestModel;

/**
 * Created by akshaya.sharma on 02/03/18
 */

public class TempModelConfiguration extends ModelConfigurationImpl<TempModelConfiguration> {
  public TempModelConfiguration() throws LoopbackException {
    super();
  }

  @Override
  protected void configure() throws LoopbackException {
    this.addRelation(
        Relation.builder()
            .name("test")
            .relatedModelClass(TestModel.class)
            .relationType(RelationType.HAS_MANY)
            .build()
    );
  }

  @Override
  public Connector getConnector() {
    return new JPAConnector();
  }

  @Override
  public String getTableName() {
    return "temp";
  }

  @Override
  public Class<? extends PersistedModel> getModelClass() {
    return TempModel.class;
  }
}
