package com.flipkart.sample;

import com.flipkart.loopback.configuration.manager.ModelConfigurationManager;
import com.flipkart.loopback.exception.LoopbackException;
import com.flipkart.sample.entity.TempModel;
import com.flipkart.sample.entity.TestModel;
import com.flipkart.sample.entity.configuration.TempModelConfiguration;
import com.flipkart.sample.entity.configuration.TestModelConfiguration;
import com.google.common.collect.Lists;
import com.google.inject.AbstractModule;
import com.google.inject.Module;
import java.util.List;

/**
 * Created by akshaya.sharma on 14/03/18
 */

public class AppModule extends AbstractModule {
  private List<? extends Module> modules;

  public AppModule() {
    this.modules = Lists.newArrayList(this);
  }

  @Override
  protected void configure() {
    ModelConfigurationManager cm = ModelConfigurationManager.getInstance();
    try {
      cm.configureModel(TempModel.class, new TempModelConfiguration());
      cm.configureModel(TestModel.class, new TestModelConfiguration());
    } catch (LoopbackException e) {
      e.printStackTrace();
    }
  }

  public List<? extends Module> getModules() {
    return modules;
  }
}
