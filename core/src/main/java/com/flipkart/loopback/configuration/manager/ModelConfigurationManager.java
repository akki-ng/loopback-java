package com.flipkart.loopback.configuration.manager;

import com.flipkart.loopback.configuration.ModelConfiguration;
import com.flipkart.loopback.exception.ModelNotConfiguredException;
import com.flipkart.loopback.model.Model;
import com.flipkart.loopback.model.PersistedModel;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by akshaya.sharma on 09/03/18
 */

public class ModelConfigurationManager {
  private static ModelConfigurationManager instance;
  private Map<Class<? extends PersistedModel>, ModelConfiguration> config = new
      ConcurrentHashMap<Class<? extends PersistedModel>, ModelConfiguration>();

  public void configureModel(Class<? extends PersistedModel> modelClass, ModelConfiguration configuration) {
    config.put(modelClass, configuration);
  }

  public ModelConfiguration getModelConfiguration(Class<? extends PersistedModel> modelClass) throws ModelNotConfiguredException {
    if(config.containsKey(modelClass)) {
      return config.get(modelClass);
    }
    throw new ModelNotConfiguredException(modelClass);
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
