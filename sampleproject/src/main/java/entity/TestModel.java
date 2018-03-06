package entity;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.flipkart.loopback.annotation.NonDB;
import com.flipkart.loopback.connector.Connector;
import com.flipkart.loopback.connector.MysqlConnector;
import com.flipkart.loopback.model.PersistedModel;
import javax.persistence.Entity;
import javax.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Created by akshaya.sharma on 02/03/18
 */

@Entity
@AllArgsConstructor
@Builder
@NoArgsConstructor
public class TestModel extends PersistedModel<TestModel> {

  @Id
  private Long id;

  @JsonProperty("BigName")
  private String name;

  @JsonProperty("OldName")
  private String oldName;

  @NonDB
  private String inVisible;

  @Override
  public Class getModelClass() {
    return this.getClass();
  }

  @Override
  public String getIdName() {
    return "id";
  }

  @Override
  public Object getId() {
    return id;
  }
}
