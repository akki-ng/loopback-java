package entity;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.flipkart.loopback.annotation.NonDB;
import com.flipkart.loopback.model.PersistedModel;
import javax.persistence.Entity;
import javax.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

/**
 * Created by akshaya.sharma on 02/03/18
 */

@Entity
@AllArgsConstructor
@Builder
public class TestModel extends PersistedModel<TestModel> {

  @Id
  private Long id;

  @JsonProperty("BigName")
  private String name;

  private String oldName;

  @NonDB
  private String inVisible;

}
