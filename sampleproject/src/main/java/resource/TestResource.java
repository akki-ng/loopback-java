package resource;

import com.flipkart.loopback.filter.Filter;
import entity.TestModel;
import io.swagger.annotations.Api;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
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


  /*
  Count instances of the model matched by where from the data source.
 */
  @GET
  @Path("/extraRemote")
  public int extraRemlte(@QueryParam("filter") Filter filter, @Context HttpServletRequest request) {
    return 0;
  }
}
