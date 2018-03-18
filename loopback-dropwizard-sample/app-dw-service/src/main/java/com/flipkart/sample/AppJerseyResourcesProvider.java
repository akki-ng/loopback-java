package com.flipkart.sample;

import com.flipkart.fdp.dws.wrapper.app.JerseyResourcesProvider;
import com.flipkart.fdp.utils.cfg.ConfigService;
import com.flipkart.loopback.dropwizard.exception.WrapperExceptionMapper;
import com.flipkart.sample.resources.TempResource;
import com.flipkart.sample.resources.TestResource;
import com.google.common.collect.Lists;
import java.util.List;

/**
 * Created by akshaya.sharma on 14/03/18
 */

public class AppJerseyResourcesProvider implements JerseyResourcesProvider {
  @Override
  public List<Class> getJerseyResources(ConfigService configService) {
    return Lists.<Class>newArrayList(WrapperExceptionMapper.class, TempResource.class,
        TestResource.class);
  }
}
