package com.flipkart.sample;

import com.flipkart.fdp.dws.wrapper.app.GuiceModulesProvider;
import com.flipkart.fdp.utils.cfg.ConfigService;
import com.google.inject.Module;
import java.util.List;

/**
 * Created by akshaya.sharma on 14/03/18
 */

public class AppModuleProvider implements GuiceModulesProvider {
  @Override
  public List<? extends Module> getModules(ConfigService configService) {
    return new AppModule().getModules();
  }
}
