package com.flipkart.loopback.exception.model.relation;

import com.flipkart.loopback.exception.LoopbackException;
import com.flipkart.loopback.model.PersistedModel;
import com.flipkart.loopback.relation.RelatedModel;
import com.flipkart.loopback.relation.Relation;
import java.text.MessageFormat;
import lombok.AllArgsConstructor;

/**
 * Created by akshaya.sharma on 18/03/18
 */

@AllArgsConstructor
public class InvalidScopeException extends LoopbackException {
  private final RelatedModel relatedModel;
  private final String reason;

  @Override
  public String getMessage() {
    return MessageFormat.format("Scope could not be calculated for relation {0} of model {1} "
        + "because {2}", relatedModel.getRelation().getName(), relatedModel.getFromModel()
        .getClass().getSimpleName(), reason);
  }
}
