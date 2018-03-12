package com.flipkart.loopback.configuration.manager;

import com.flipkart.loopback.configuration.ModelConfiguration;
import com.flipkart.loopback.model.Model;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by akshaya.sharma on 09/03/18
 */

public class ModelConfigurationManager {
  private static ModelConfigurationManager instance;
  private Map<Class<? extends Model>, ModelConfiguration> config = new
      ConcurrentHashMap<Class<? extends Model>, ModelConfiguration>();

  public void configureModel(Class<? extends Model> modelClass, ModelConfiguration configuration) {
    config.put(modelClass, configuration);
  }

  public ModelConfiguration getModelConfiguration(Class<? extends Model> modelClass) {
    return config.get(modelClass);
  }

  protected ModelConfigurationManager() {

  }

  public static ModelConfigurationManager getInstance() {
    if (instance == null) {
      instance = new ModelConfigurationManager();
    }
    return instance;
  }
}
