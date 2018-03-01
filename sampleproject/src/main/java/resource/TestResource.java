package resource;

import io.swagger.annotations.Api;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

/**
 * Created by akshaya.sharma on 01/03/18
 */

@Api(tags = {"Test"})
@Path("/test")
@Produces(MediaType.APPLICATION_JSON)
public class TestResource implements DWResource{
  public TestResource(String template, String defaultName) {
//    super(template, defaultName);
  }

  @Override
  public String getTemplate() {
    return null;
  }

  @Override
  public String getDefaultName() {
    return null;
  }
}
