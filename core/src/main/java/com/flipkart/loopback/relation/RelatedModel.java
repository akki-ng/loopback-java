package com.flipkart.loopback.relation;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.flipkart.loopback.annotation.Transaction;
import com.flipkart.loopback.configuration.ModelConfiguration;
import com.flipkart.loopback.configuration.manager.ModelConfigurationManager;
import com.flipkart.loopback.connector.Connector;
import com.flipkart.loopback.constants.RelationType;
import com.flipkart.loopback.exception.LoopbackException;
import com.flipkart.loopback.filter.Filter;
import com.flipkart.loopback.filter.WhereFilter;
import com.flipkart.loopback.model.Model;
import com.flipkart.loopback.model.PersistedModel;
import java.io.IOException;
import java.io.Serializable;
import java.util.Map;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

/**
 * Created by akshaya.sharma on 16/03/18
 */

@AllArgsConstructor
@Builder
@Getter
public class RelatedModel extends Model {
  private PersistedModel fromModel;
  private Relation relation;

  public boolean isManyRelation() {
    RelationType relationType = relation.getRelationType();
    return relationType == RelationType.HAS_MANY || relationType == RelationType.HAS_MANY_THROUGH;
  }

  private WhereFilter _getHasOneScope() throws IllegalAccessException, LoopbackException {
    Object value = fromModel.getPropertyValue(relation.getFromPropertyName());
    if(value != null) {
      Class<? extends PersistedModel> relatedModelClass = relation.getRelatedModelClass();
      String filterString = "{\"" + relation.getToPropertyName() + "\" : " +
          value.toString() + "}";
      return new WhereFilter(filterString);
    }

    // TODO no related entity associated - exception
    throw new LoopbackException("Not found");
  }

  private WhereFilter _getHasManyScope() throws IllegalAccessException, LoopbackException {
    Object value = fromModel.getPropertyValue(relation.getFromPropertyName());
    if(value != null) {
      Class<? extends PersistedModel> relatedModelClass = relation.getRelatedModelClass();
      String filterString = "{\"" + relation.getToPropertyName() + "\" : " +
          value.toString() + "}";
      return new WhereFilter(filterString);
    }
    throw new LoopbackException("Not found");
  }

  private WhereFilter _getHasManyThroughScope() throws IllegalAccessException, LoopbackException {
    // TODO
    // Fetch through model
    // For each fetch related entity

    Object value = fromModel.getPropertyValue(relation.getFromPropertyName());
    if(value != null) {
      Class<? extends PersistedModel> relatedModelClass = relation.getRelatedModelClass();
      String filterString = "{\"" + relation.getToPropertyName() + "\" : " +
          value.toString() + "}";
      return new WhereFilter(filterString);
    }
    throw new LoopbackException("Not found");
  }

  private  WhereFilter _getBelongsToScope() throws IllegalAccessException, LoopbackException {
    Object value = fromModel.getPropertyValue(relation.getFromPropertyName());
    if(value != null) {
      Class<? extends PersistedModel> relatedModelClass = relation.getRelatedModelClass();
      String filterString = "{\"" + relation.getToPropertyName() + "\" : " +
          value.toString() + "}";
      return new WhereFilter(filterString);
    }
    throw new LoopbackException("Not found");
  }

  public WhereFilter getScope() throws IllegalAccessException, IOException, LoopbackException {
    RelationType relationType = relation.getRelationType();
    switch (relationType) {
      case HAS_ONE: return _getHasOneScope();
      case HAS_MANY: return _getHasManyScope();
      case HAS_MANY_THROUGH: return _getHasManyThroughScope();
      case BELONGS_TO: return _getBelongsToScope();
    }
    throw new LoopbackException("Invalid relation");
  }

  public <T extends Object> T find(Filter relatedModelfilter) throws IllegalAccessException,
      IOException, LoopbackException {
    if (relatedModelfilter == null) {
      relatedModelfilter = new Filter("{}");
    }
    if (relatedModelfilter.getWhere() == null) {
      relatedModelfilter.setWhere(new WhereFilter("{}"));
    }
    WhereFilter scope = getScope();
    relatedModelfilter.setWhere(relatedModelfilter.getWhere().merge(scope));

    Class<? extends PersistedModel> relatedModelClass = relation.getRelatedModelClass();

    if (relation.getRelationType() == RelationType.HAS_ONE || relation.getRelationType() ==
        RelationType.BELONGS_TO) {
      return (T) PersistedModel.findOne(relatedModelClass, relatedModelfilter);
    }else {
      // HAS_MANY, HAS_MANY_THROUGH
      return (T) PersistedModel.find(relatedModelClass, relatedModelfilter);
    }
  }

  public <T extends PersistedModel> T findById(Serializable relatedModelId) throws
      IllegalAccessException,
      IOException, LoopbackException {
    if (relation.getRelationType() == RelationType.HAS_ONE || relation.getRelationType() ==
        RelationType.BELONGS_TO) {
      throw new LoopbackException("Relation must be HAS_MANY or HAS_MANY_THROUGH relation ");
    }
    Class<? extends PersistedModel> relModelClass = relation.getRelatedModelClass();
    ModelConfiguration configuration = ModelConfigurationManager.getInstance()
        .getModelConfiguration(relModelClass);
    String relatedModelIdPropertyName = configuration.getIdPropertyName();
    String filterString = "{\"where\": {\"" + relatedModelIdPropertyName + "\" : " +
        relatedModelId.toString() + "}}";
    Filter relatedModelFilter = new Filter(filterString);
    WhereFilter scope = getScope();
    relatedModelFilter.setWhere(relatedModelFilter.getWhere().merge(scope));

    Class<? extends PersistedModel> relatedModelClass = relation.getRelatedModelClass();

    return (T) PersistedModel.findOne(relatedModelClass, relatedModelFilter);
  }

  @Transaction
  public <R extends PersistedModel> R create(
      R relatedModel) throws IllegalAccessException, LoopbackException {
    Class<? extends PersistedModel> relModelClass = relation.getRelatedModelClass();
    if(!relModelClass.isInstance(relatedModel)) {
      throw new LoopbackException("Model is not matching the class type");
    }
    if (relation.getRelationType() == RelationType.HAS_MANY) {
      Object fromValue = fromModel.getPropertyValue(relation.getFromPropertyName());
      relatedModel.setAttribute(relation.getToPropertyName(), fromValue);
      relatedModel = PersistedModel.create(relatedModel);
      return relatedModel;
    }
    return null;
  }

  @Transaction
  public <R extends PersistedModel> R replaceById(R relatedModel, Serializable relatedModelId) throws LoopbackException, IllegalAccessException {
    Class<? extends PersistedModel> relModelClass = relation.getRelatedModelClass();
    if(!relModelClass.isInstance(relatedModel)) {
      throw new LoopbackException("Model is not matching the class type");
    }
    if (relation.getRelationType() == RelationType.HAS_MANY) {
      Object fromValue = fromModel.getPropertyValue(relation.getFromPropertyName());
      relatedModel.setAttribute(relation.getToPropertyName(), fromValue);
      relatedModel = PersistedModel.replaceById(relatedModel, relatedModelId);
      return relatedModel;
    }
    return null;
  }

  @Transaction
  public <R extends PersistedModel> R updateOrCreate(Map<String,
      Object> patchData) throws LoopbackException, InstantiationException, IllegalAccessException {
    ModelConfiguration configuration = getConfiguration();
    String idPropertyName = configuration.getIdPropertyName();

    if(this.relation.getRelationType() == RelationType.HAS_MANY) {
      patchData.remove(relation.getToPropertyName());
    }else if(this.relation.getRelationType() == RelationType.HAS_ONE) {
      patchData.remove(relation.getFromPropertyName());
    }

    R relatedInstance = null;
    if(patchData.containsKey(idPropertyName) && patchData.get(idPropertyName) != null) {
      // Id exists
      relatedInstance = (R) R.findById(relation.getRelatedModelClass(),null, (Serializable)
          patchData.get(idPropertyName));
      relatedInstance = (R) relatedInstance.updateAttributes(patchData);
    }else {
      // Try create
      R transientInstance = relation.getInstance(patchData);
      return this.create(transientInstance);
    }
    return relatedInstance;
  }

  @Transaction
  public <R extends PersistedModel> R updateAttributes(Serializable relatedModelId, Map<String,
      Object> data)
      throws LoopbackException, IOException, IllegalAccessException {
    if(this.relation.getRelationType() == RelationType.HAS_MANY) {
      data.remove(relation.getToPropertyName());
    }else if(this.relation.getRelationType() == RelationType.HAS_ONE) {
      data.remove(relation.getFromPropertyName());
    }
    R relatedInstance = findById(relatedModelId);
    return (R) relatedInstance.setAttributes(data).save();
  }

  @Transaction
  public long destroyAll(WhereFilter where) throws IllegalAccessException, IOException,
      LoopbackException {
    if(where == null) {
      where = new WhereFilter();
    }
    WhereFilter scope = getScope();
    where = where.merge(scope);
    if(this.relation.getRelationType() == RelationType.HAS_ONE) {
      // Foreign key constraint / remove dependency
      fromModel.setAttribute(relation.getFromPropertyName(), null).save();
    }
    long count = PersistedModel.destroyAll(relation.getRelatedModelClass(), where);
    return count;
  }

  @Transaction
  public <T extends PersistedModel> T destroyById(Serializable relatedModelId) throws IllegalAccessException, IOException,
      LoopbackException {
    PersistedModel relatedInstance = findById(relatedModelId);
    return (T) relatedInstance.destroy();
  }

  @Override
  public Connector getConnector() {
    return PersistedModel.getConnector(relation.getRelatedModelClass());
  }

  @Override
  public ModelConfiguration getConfiguration() {
    return PersistedModel.getConfiguration(relation.getRelatedModelClass());
  }
}
