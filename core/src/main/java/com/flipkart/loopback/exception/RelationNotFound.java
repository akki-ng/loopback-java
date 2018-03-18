package com.flipkart.loopback.exception;

import com.flipkart.loopback.model.PersistedModel;
import java.text.MessageFormat;
import lombok.AllArgsConstructor;

/**
 * Created by akshaya.sharma on 17/03/18
 */

@AllArgsConstructor
public class RelationNotFound extends LoopbackException {
  protected final Class<? extends PersistedModel> modelClass;
  protected final String relationInfo;
  protected final String relationInfoType;

  @Override
  public String getMessage() {
    return MessageFormat.format("No relation found with {0} {1} for model {2}", relationInfo,
        relationInfoType, modelClass.getSimpleName());
  }
}
