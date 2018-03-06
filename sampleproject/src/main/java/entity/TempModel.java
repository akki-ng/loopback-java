package entity;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.flipkart.loopback.model.PersistedModel;
import lombok.Data;

/**
 * Created by akshaya.sharma on 02/03/18
 */
public class TempModel extends PersistedModel<TempModel> {

  @JsonProperty("id")
  private int id;

  @JsonProperty("akshay")
  private String akshay;

  @Override
  public Class getModelClass() {
    return this.getClass();
  }

  @Override
  public Object getId() {
    return id;
  }

  @Override
  public String getIdName() {
    return "id";
  }
}
