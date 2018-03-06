package entity;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.flipkart.loopback.filter.Filter;
import com.flipkart.loopback.model.PersistedModel;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
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
