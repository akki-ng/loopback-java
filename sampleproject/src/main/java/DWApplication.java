import com.flipkart.loopback.configuration.manager.ModelConfigurationManager;
import com.flipkart.loopback.exception.LoopbackException;
import entity.TempModel;
import entity.TestModel;
import entity.configuration.TempModelConfiguration;
import entity.configuration.TestModelConfiguration;
import io.dropwizard.Application;
import io.dropwizard.setup.Bootstrap;
import io.dropwizard.setup.Environment;
import io.federecio.dropwizard.swagger.SwaggerBundle;
import io.federecio.dropwizard.swagger.SwaggerBundleConfiguration;
import java.util.List;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.core.MediaType;
import org.glassfish.jersey.process.Inflector;
import org.glassfish.jersey.server.model.Resource;
import org.glassfish.jersey.server.model.ResourceMethod;
import resource.HelloResource;
import resource.TempResource;
import resource.TestResource;

/**
 * Created by akshaya.sharma on 01/03/18
 */

public class DWApplication extends Application<DWConfiguration> {
  public static void main(String[] args) throws Exception, LoopbackException {
    new DWApplication().run(args);
//    TestModel a = TestModel.builder()
//        .id(22L)
//        .inVisible("IamInvisible")
//        .name("Akshay")
//        .oldName("Sharma")
//        .build();
//    System.out.println(a);
//
//    a.setAttribute("id", 24L);
//    System.out.println(a);
  }

  @Override
  public String getName() {
    return "Sample-App";
  }
  @Override
  public void initialize(Bootstrap<DWConfiguration> bootstrap) {
// nothing to do yet
    bootstrap.addBundle(new SwaggerBundle<DWConfiguration>() {
      @Override
      protected SwaggerBundleConfiguration getSwaggerBundleConfiguration(DWConfiguration configuration) {
        return configuration.swaggerBundleConfiguration;
      }
    });
  }

  @Override
  public void run(DWConfiguration dwConfiguration, Environment environment) throws Exception {
    final HelloResource resource = new HelloResource(
        dwConfiguration.getTemplate(),
        dwConfiguration.getDefaultName()
    );
    ModelConfigurationManager cm = ModelConfigurationManager.getInstance();
    try {
      cm.configureModel(TempModel.class, new TempModelConfiguration());
      cm.configureModel(TestModel.class, new TestModelConfiguration());
    } catch (LoopbackException e) {
      e.printStackTrace();
    }


    final TestResource t1 = new TestResource();
    final TempResource t2 = new TempResource();

//    Resource.Builder rb = Resource.builder();
////    rb.path()
//
//    ResourceMethod.Builder rmb = rb.addMethod("GET").produces(MediaType.TEXT_PLAIN).handledBy
//        (new Inflector<ContainerRequestContext, Integer>() {
//          @Override
//          public Integer apply(ContainerRequestContext containerRequestContext) {
//            return null;
//          }
//        })
    environment.jersey().register(resource);
    environment.jersey().register(t1);
    environment.jersey().register(t2);
  }
}
