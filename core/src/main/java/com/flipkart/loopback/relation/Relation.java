package com.flipkart.loopback.relation;

import com.flipkart.loopback.constants.RelationType;
import com.flipkart.loopback.exception.LoopbackException;
import com.flipkart.loopback.filter.Filter;
import com.flipkart.loopback.model.PersistedModel;
import java.util.Map;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import org.apache.commons.lang3.StringUtils;
import org.jibx.schema.codegen.extend.DefaultNameConverter;
import org.jibx.schema.codegen.extend.NameConverter;

/**
 * Created by akshaya.sharma on 08/03/18
 */

@Data
@AllArgsConstructor
@Builder
public class Relation {
//  <R extends PersistedModel>
  private final RelationType relationType;
  private final Filter scope;
  private final String fromPropertyName;
  private final String toPropertyName;
  private final String name;
  private final String restPath;
  private Class<? extends PersistedModel> relatedModelClass;

  public String getRestPath() {
    NameConverter nameTools = new DefaultNameConverter();
    if(StringUtils.isBlank(restPath)) {
      return nameTools.pluralize(this.getName()).toLowerCase();
    }
    return restPath.toLowerCase();
  }

  public <R extends PersistedModel> R getInstance(Map<String, Object> data) throws
      IllegalAccessException, InstantiationException, LoopbackException {
    R transientInstance = (R) getRelatedModelClass().newInstance();
    transientInstance.setAttributes(data);
    return transientInstance;
  }
}
