package resource;

import com.codahale.metrics.annotation.Timed;
import entity.Saying;
import java.util.Optional;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.QueryParam;

/**
 * Created by akshaya.sharma on 01/03/18
 */

public interface DWResource {
  String getTemplate();
  String getDefaultName();
  //  public String getTemplate();
  //  public String getDefaultName();

  @GET
  @Path("/asd")
  public default Saying sayHello(@QueryParam("name") Optional<String> name) {
    final String value = "Aksh";//String.format(getTemplate(), name.orElse(defaultName));
    return new Saying(0, value);
  }
}
