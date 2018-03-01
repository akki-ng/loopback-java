package resource;

/**
 * Created by akshaya.sharma on 01/03/18
 */

import com.codahale.metrics.annotation.Timed;
import entity.Saying;
import io.swagger.annotations.Api;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import java.util.concurrent.atomic.AtomicLong;
import java.util.Optional;

@Api(tags = {"Open"})
@Path("/hello-world")
@Produces(MediaType.APPLICATION_JSON)
public class HelloResource implements DWResource{
  private final String template;
  private final String defaultName;
  private final AtomicLong counter;
  public HelloResource(String template, String defaultName) {
    this.template = template;
    this.defaultName = defaultName;
    this.counter = new AtomicLong();
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
