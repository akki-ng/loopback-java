package com.flipkart.sample;

import com.flipkart.fdp.dws.wrapper.app.DWApp;
import com.flipkart.fdp.dws.wrapper.app.GuiceModulesProvider;
import com.flipkart.fdp.dws.wrapper.app.JerseyResourcesProvider;
import io.dropwizard.setup.Bootstrap;
import io.federecio.dropwizard.swagger.SwaggerBundle;
import io.federecio.dropwizard.swagger.SwaggerBundleConfiguration;
import java.io.IOException;

/**
 * Created by akshaya.sharma on 14/03/18
 */

public class AppService extends DWApp<AppDWConfig> {
  private AppService(GuiceModulesProvider guiceModulesProvider,
                    JerseyResourcesProvider jerseyResourcesProvider) throws IOException {
    super(guiceModulesProvider, jerseyResourcesProvider);
  }

  public static void main(final String[] args) throws Exception {
    final DWApp<AppDWConfig> application = new AppService(
        new AppModuleProvider(), new AppJerseyResourcesProvider());
    application.run();
  }

  public void initializeApp(Bootstrap<AppDWConfig> appHandle) {
  }
}
