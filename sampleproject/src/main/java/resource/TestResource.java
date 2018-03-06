package resource;

import com.flipkart.loopback.annotation.NonDB;
import entity.TempModel;
import entity.TestModel;
import io.swagger.annotations.Api;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import com.flipkart.loopback.resources.BaseResource;


/**
 * Created by akshaya.sharma on 01/03/18
 */

@Api(tags = {"Test"})
@Path("/test")
@Produces(MediaType.APPLICATION_JSON)
public class TestResource extends BaseResource<TestModel>{

  @Override
  public Class getModelClass() {
    return TestModel.class;
  }

  @Override
  public TestModel findById(String id) {
    return null;
  }
}
