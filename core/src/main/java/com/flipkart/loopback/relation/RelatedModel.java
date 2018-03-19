package com.flipkart.loopback.relation;

import com.flipkart.loopback.annotation.Transaction;
import com.flipkart.loopback.configuration.ModelConfiguration;
import com.flipkart.loopback.configuration.manager.ModelConfigurationManager;
import com.flipkart.loopback.connector.Connector;
import com.flipkart.loopback.constants.RelationType;
import com.flipkart.loopback.exception.configuration.ModelNotConfiguredException;
import com.flipkart.loopback.exception.model.CouldNotPerformException;
import com.flipkart.loopback.exception.model.InternalError;
import com.flipkart.loopback.exception.model.OperationNotAllowedException;
import com.flipkart.loopback.exception.model.persistence.ModelNotFoundException;
import com.flipkart.loopback.exception.model.relation.InvalidScopeException;
import com.flipkart.loopback.exception.validation.filter.InvalidFilterException;
import com.flipkart.loopback.exception.validation.model.IdFieldNotFoundException;
import com.flipkart.loopback.exception.validation.model.InvalidPropertyValueException;
import com.flipkart.loopback.exception.validation.model.PropertyNotFoundException;
import com.flipkart.loopback.exception.validation.model.ReadOnlyPropertyException;
import com.flipkart.loopback.filter.Filter;
import com.flipkart.loopback.filter.WhereFilter;
import com.flipkart.loopback.model.Model;
import com.flipkart.loopback.model.PersistedModel;
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

  private WhereFilter _getHasOneScope() throws InvalidScopeException {
    try {
      Object value = fromModel.getPropertyValue(relation.getFromPropertyName());
      if (value != null) {
        Class<? extends PersistedModel> relatedModelClass = relation.getRelatedModelClass();
        String filterString = "{\"" + relation.getToPropertyName() + "\" : " + value.toString() +
            "}";
        return new WhereFilter(filterString);
      }
      throw new InvalidScopeException(this, "related property can not be null");
    } catch (InvalidFilterException | PropertyNotFoundException | ReadOnlyPropertyException e) {
      e.printStackTrace();
      throw new InvalidScopeException(this, e.getMessage());
    }
  }

  private WhereFilter _getHasManyScope() throws InvalidScopeException {
    try {
      Object value = fromModel.getPropertyValue(relation.getFromPropertyName());
      if (value != null) {
        Class<? extends PersistedModel> relatedModelClass = relation.getRelatedModelClass();
        String filterString = "{\"" + relation.getToPropertyName() + "\" : " + value.toString() +
            "}";
        return new WhereFilter(filterString);
      }
      throw new InvalidScopeException(this, "related property can not be null");
    } catch (InvalidFilterException | PropertyNotFoundException | ReadOnlyPropertyException e) {
      e.printStackTrace();
      throw new InvalidScopeException(this, e.getMessage());
    }
  }

  private WhereFilter _getHasManyThroughScope() throws InvalidScopeException {
    // TODO
    // Fetch through model
    // For each fetch related entity

    try {
      Object value = fromModel.getPropertyValue(relation.getFromPropertyName());
      if (value != null) {
        Class<? extends PersistedModel> relatedModelClass = relation.getRelatedModelClass();
        String filterString = "{\"" + relation.getToPropertyName() + "\" : " + value.toString() +
            "}";
        return new WhereFilter(filterString);
      }
      throw new InvalidScopeException(this, "related property can not be null");
    } catch (InvalidFilterException | PropertyNotFoundException | ReadOnlyPropertyException e) {
      e.printStackTrace();
      throw new InvalidScopeException(this, e.getMessage());
    }
  }

  private WhereFilter _getBelongsToScope() throws InvalidScopeException {
    try {
      Object value = fromModel.getPropertyValue(relation.getFromPropertyName());
      if (value != null) {
        Class<? extends PersistedModel> relatedModelClass = relation.getRelatedModelClass();
        String filterString = "{\"" + relation.getToPropertyName() + "\" : " + value.toString() +
            "}";
        return new WhereFilter(filterString);
      }
      throw new InvalidScopeException(this, "related property can not be null");
    } catch (InvalidFilterException | PropertyNotFoundException | ReadOnlyPropertyException e) {
      e.printStackTrace();
      throw new InvalidScopeException(this, e.getMessage());
    }
  }

  public WhereFilter getScope() throws InvalidScopeException {
    RelationType relationType = relation.getRelationType();
    switch (relationType) {
      case HAS_ONE:
        return _getHasOneScope();
      case HAS_MANY:
        return _getHasManyScope();
      case HAS_MANY_THROUGH:
        return _getHasManyThroughScope();
      case BELONGS_TO:
        return _getBelongsToScope();
    }
    throw new InvalidScopeException(this, "Relation is misconfigured");
  }

  public <T extends Object> T find(
      Filter relatedModelfilter) throws ModelNotFoundException,
      CouldNotPerformException {
    try {
      if (relatedModelfilter == null) {
        relatedModelfilter = new Filter();
      }
      if (relatedModelfilter.getWhere() == null) {
        relatedModelfilter.setWhere(new WhereFilter());
      }
      WhereFilter scope = getScope();
      relatedModelfilter.setWhere(relatedModelfilter.getWhere().merge(scope));

      Class<? extends PersistedModel> relatedModelClass = relation.getRelatedModelClass();

      if (relation.getRelationType() == RelationType.HAS_ONE || relation.getRelationType() ==
          RelationType.BELONGS_TO) {
        return (T) PersistedModel.findOne(relatedModelClass, relatedModelfilter);
      } else {
        // HAS_MANY, HAS_MANY_THROUGH
        return (T) PersistedModel.find(relatedModelClass, relatedModelfilter);
      }
    } catch (InvalidScopeException | InvalidFilterException e) {
      e.printStackTrace();
      throw new CouldNotPerformException(relation.getRelatedModelClass(), "find", e);
    }
  }

  public <T extends PersistedModel> T findById(
      Serializable relatedModelId) throws OperationNotAllowedException,
      ModelNotFoundException {
    try {
      if (relation.getRelationType() == RelationType.HAS_ONE || relation.getRelationType() ==
          RelationType.BELONGS_TO) {

        throw new OperationNotAllowedException(fromModel.getClass(), "findById",
            "Relation must be one of HAS_MANY, HAS_MANY_THROUGH type");
      }
      Class<? extends PersistedModel> relModelClass = relation.getRelatedModelClass();
      ModelConfiguration configuration = ModelConfigurationManager.getInstance()
          .getModelConfiguration(
          relModelClass);
      String relatedModelIdPropertyName = configuration.getIdPropertyName();
      String filterString = "{\"where\": {\"" + relatedModelIdPropertyName + "\" : " +
          relatedModelId.toString() + "}}";
      Filter relatedModelFilter = new Filter(filterString);
      WhereFilter scope = getScope();
      relatedModelFilter.setWhere(relatedModelFilter.getWhere().merge(scope));

      Class<? extends PersistedModel> relatedModelClass = relation.getRelatedModelClass();

      return (T) PersistedModel.findOne(relatedModelClass, relatedModelFilter);
    } catch (InvalidFilterException | InvalidScopeException | ModelNotConfiguredException e) {
      e.printStackTrace();
      throw new InternalError(relation.getRelatedModelClass(), e);
    }
  }

  @Transaction
  public <R extends PersistedModel> R create(
      R relatedModel) throws OperationNotAllowedException, CouldNotPerformException {
    try {
      Class<? extends PersistedModel> relModelClass = relation.getRelatedModelClass();
      if (!relModelClass.isInstance(relatedModel)) {
        throw new OperationNotAllowedException(relModelClass, "create",
            "Model is not matching the schema ");
      }
      if (relation.getRelationType() == RelationType.HAS_MANY) {
        Object fromValue = fromModel.getPropertyValue(relation.getFromPropertyName());
        relatedModel.setAttribute(relation.getToPropertyName(), (Serializable) fromValue);
        relatedModel = PersistedModel.create(relatedModel);
        return relatedModel;
      }
      return null;
      // TODO
    } catch (InvalidPropertyValueException | PropertyNotFoundException e) {
      e.printStackTrace();
      throw new InternalError(relation.getRelatedModelClass(), e);
    }
  }

  @Transaction
  public <R extends PersistedModel> R replaceById(R relatedModel,
      Serializable relatedModelId) throws OperationNotAllowedException, InternalError, ModelNotFoundException, CouldNotPerformException {
    try {
      if (relation.getRelationType() == RelationType.HAS_ONE || relation.getRelationType() ==
          RelationType.BELONGS_TO) {

        throw new OperationNotAllowedException(fromModel.getClass(), "replaceById",
            "Relation must be one of HAS_MANY, HAS_MANY_THROUGH type");
      }

      Class<? extends PersistedModel> relModelClass = relation.getRelatedModelClass();
      if (!relModelClass.isInstance(relatedModel)) {
        throw new OperationNotAllowedException(relModelClass, "create",
            "Model is not matching the schema ");
      }

      Object fromValue = fromModel.getPropertyValue(relation.getFromPropertyName());
      relatedModel.setAttribute(relation.getToPropertyName(), (Serializable) fromValue);
      relatedModel = PersistedModel.replaceById(relatedModel, relatedModelId);
      return relatedModel;
    } catch (InvalidPropertyValueException | PropertyNotFoundException e) {
      e.printStackTrace();
      throw new InternalError(relation.getRelatedModelClass(), e);
    }
  }

  @Transaction
  public <R extends PersistedModel> R updateOrCreate(
      Map<String, Object> patchData) throws ModelNotFoundException, InternalError,
      InvalidPropertyValueException, CouldNotPerformException, IdFieldNotFoundException,
      OperationNotAllowedException {
    ModelConfiguration configuration = getConfiguration();
    String idPropertyName = configuration.getIdPropertyName();

    if (this.relation.getRelationType() == RelationType.HAS_MANY) {
      patchData.remove(relation.getToPropertyName());
    } else if (this.relation.getRelationType() == RelationType.HAS_ONE) {
      patchData.remove(relation.getFromPropertyName());
    }

    R relatedInstance = null;
    if (patchData.containsKey(idPropertyName) && patchData.get(idPropertyName) != null) {
      // Id exists
      relatedInstance = (R) R.findById(relation.getRelatedModelClass(), null,
          (Serializable) patchData.get(idPropertyName));
      relatedInstance = (R) relatedInstance.updateAttributes(patchData);
    } else {
      // Try create
      R transientInstance = relation.getInstance(patchData);
      return this.create(transientInstance);
    }
    return relatedInstance;
  }

  @Transaction
  public <R extends PersistedModel> R updateAttributes(Serializable relatedModelId,
      Map<String, Object> data) throws ModelNotFoundException, OperationNotAllowedException,
      InternalError, InvalidPropertyValueException, IdFieldNotFoundException,
      CouldNotPerformException {
    if (this.relation.getRelationType() == RelationType.HAS_MANY) {
      data.remove(relation.getToPropertyName());
    } else if (this.relation.getRelationType() == RelationType.HAS_ONE) {
      data.remove(relation.getFromPropertyName());
    }
    R relatedInstance = findById(relatedModelId);
    return (R) relatedInstance.setAttributes(data).save();
  }

  @Transaction
  public long destroyAll(WhereFilter where) throws InternalError, CouldNotPerformException {
    try {
      if (where == null) {
        where = new WhereFilter();
      }
      WhereFilter scope = getScope();
      where = where.merge(scope);
      if (this.relation.getRelationType() == RelationType.HAS_ONE) {
        // Foreign key constraint / remove dependency
        fromModel.setAttribute(relation.getFromPropertyName(), null).save();
      }
      long count = PersistedModel.destroyAll(relation.getRelatedModelClass(), where);
      return count;
    } catch (InvalidFilterException | InvalidPropertyValueException | InvalidScopeException e) {
      e.printStackTrace();
      throw new CouldNotPerformException(relation.getRelatedModelClass(), "destroyAll", e);
    }

  }

  @Transaction
  public <T extends PersistedModel> T destroyById(
      Serializable relatedModelId) throws ModelNotFoundException, OperationNotAllowedException,
      InternalError {
    PersistedModel relatedInstance = findById(relatedModelId);
    return (T) relatedInstance.destroy();
  }

  public Connector getConnector() throws InternalError {
    return PersistedModel.getConnector(relation.getRelatedModelClass());
  }

  public ModelConfiguration getConfiguration() throws InternalError {
    return PersistedModel.getConfiguration(relation.getRelatedModelClass());
  }
}
