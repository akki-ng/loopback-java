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
import java.util.List;
import java.util.Map;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import org.jetbrains.annotations.NotNull;
import org.json.JSONObject;

/**
 * Created by akshaya.sharma on 16/03/18
 */

@AllArgsConstructor
@Getter
public class RelatedModel extends Model {
  private PersistedModel fromModel;
  private Relation relation;
  private WhereFilter relationScope;

  public RelatedModel(PersistedModel fromModel, Relation relation) {
    this.fromModel = fromModel;
    this.relation = relation;
    this.relationScope = _buildRelationScope();
  }

  private WhereFilter _buildRelationScope() {
    if(relation.getRelationType() == RelationType.HAS_ONE || relation.getRelationType() ==
        RelationType.BELONGS_TO || relation.getRelationType() == RelationType.HAS_MANY) {
      Object fromPropertyValue = this.fromModel.getPropertyValue(relation.getFromPropertyName());
      if(fromPropertyValue == null && relation.getRelationType() == RelationType.HAS_ONE) {
        return null;
      }
      JSONObject obj = new JSONObject();
      obj.put(relation.getToPropertyName(), fromPropertyValue);
      return new WhereFilter(obj);
    }else if(relation.getRelationType() == RelationType.HAS_MANY_THROUGH) {
      // Scope for internal through model
      JSONObject obj = new JSONObject();
      obj.put(relation.getToThroughPropertyName(), this.fromModel.getPropertyValue(relation.getFromPropertyName()));
      return new WhereFilter(obj);
    }
    return null;
  }

  public boolean isManyRelation() {
    RelationType relationType = relation.getRelationType();
    return relationType == RelationType.HAS_MANY || relationType == RelationType.HAS_MANY_THROUGH;
  }

  public Connector getConnector() throws InternalError {
    return PersistedModel.getConnector(relation.getRelatedModelClass());
  }

  public ModelConfiguration getConfiguration() throws InternalError {
    return PersistedModel.getConfiguration(relation.getRelatedModelClass());
  }

  /*
      T is <? extends PersistedModel> (HasOne, belongsTo)
      or
      T is List<<? extends PersistedModel>> (HasMany, HasManyThrough)
   */
  public <T extends Object> T find(Filter filter, Filter throughFilter) throws
      ModelNotFoundException {
    if(relation.getRelationType() == RelationType.HAS_ONE && relationScope == null) {
      throw new ModelNotFoundException(relation.getRelatedModelClass(), filter);
    }
    if(relation.getRelationType() == RelationType.HAS_ONE || relation.getRelationType() ==
        RelationType.BELONGS_TO || relation.getRelationType() == RelationType.HAS_MANY) {
      if(filter == null) {
        filter = new Filter();
      }
      if(filter.getWhere() == null) {
        filter.setWhere(relationScope);
      }else {
        filter.setWhere(filter.getWhere().merge(relationScope));
      }

      if(relation.getRelationType() == RelationType.HAS_ONE || relation.getRelationType() ==
          RelationType.BELONGS_TO) {
        return (T) PersistedModel.findOne(relation.getRelatedModelClass(), filter);
      }else {
        return (T) PersistedModel.find(relation.getRelatedModelClass(), filter);
      }
    }else if(relation.getRelationType() == RelationType.HAS_MANY_THROUGH){
//      return Model.getProvider().find(this);
      List<RelatedThroughEntity> result = Model
          .getProvider().findThroughRelatedEntities(relationScope, relation, throughFilter, filter);
      return (T) result;
    }
    throw new ModelNotConfiguredException(fromModel.getClass());
  }


  public <M extends PersistedModel> M create(M relatedModel) throws OperationNotAllowedException, CouldNotPerformException {
    // TODO
    if(relation.getRelationType() == RelationType.BELONGS_TO) {
      Class<? extends PersistedModel> relModelClass = relation.getRelatedModelClass();
      throw new OperationNotAllowedException(relModelClass, "create", "can not create a belongs "
          + "to entity");
    }
    if(relation.getRelationType() == RelationType.HAS_ONE || relation.getRelationType() == RelationType.HAS_MANY) {
      Class<? extends PersistedModel> relModelClass = relation.getRelatedModelClass();
      if(relatedModel == null || !relModelClass.isInstance(relatedModel)) {
        throw new OperationNotAllowedException(relModelClass, "create",
            "Model is not matching the schema");
      }
      if (relation.getRelationType() == RelationType.HAS_MANY) {
        Object fromValue = fromModel.getPropertyValue(relation.getFromPropertyName());
        relatedModel = (M) relatedModel.setAttribute(relation.getToPropertyName(), (Serializable)
            fromValue);
        relatedModel = PersistedModel.create(relatedModel);
        return relatedModel;
      }else if (relation.getRelationType() == RelationType.HAS_ONE) {
        try {
          PersistedModel persisted = find(null, null);
          throw new OperationNotAllowedException(relModelClass, "create",
              "Model already exists. Please update using update api");
        } catch (ModelNotFoundException e) {
          // Continue create
          M persisted = PersistedModel.create(relatedModel);
          fromModel = fromModel.setAttribute(relation.getFromPropertyName(), (Serializable)
              persisted.getPropertyValue(relation.getToPropertyName()));
          fromModel = fromModel.save();
          return persisted;
        }
      }
    }
    // TODO has many through
    return null;
  }

  public <M extends PersistedModel> long destroyAll(WhereFilter where) throws OperationNotAllowedException {
    if(relation.getRelationType() == RelationType.BELONGS_TO) {
      Class<? extends PersistedModel> relModelClass = relation.getRelatedModelClass();
      throw new OperationNotAllowedException(relModelClass, "destroyAll", "can not destroyAll a belongs "
          + "to entity");
    }
    Class<? extends PersistedModel> destroyModelClass = null;
    if(relation.getRelationType() == RelationType.HAS_ONE || relation.getRelationType() == RelationType.HAS_MANY) {
      destroyModelClass = relation.getRelatedModelClass();
    }else if(relation.getRelationType() == RelationType.HAS_MANY_THROUGH) {
      destroyModelClass = relation.getThroughModelClass();
    }
    if(where == null) {
      where = new WhereFilter();
    }
    where = where.merge(relationScope);
    return PersistedModel.destroyAll(destroyModelClass, where);
  }

  public <M extends PersistedModel> M findByFk(@NotNull Serializable fk) throws ModelNotFoundException,
      OperationNotAllowedException {
    if(relation.getRelationType() == RelationType.BELONGS_TO || relation.getRelationType() ==
        RelationType.HAS_ONE ) {
      Class<? extends PersistedModel> relModelClass = relation.getRelatedModelClass();
      throw new OperationNotAllowedException(relModelClass, "findByFk", "can not findByFk for "
          + "hasOne or belongs "
          + "to entity");
    }

    if(relation.getRelationType() == RelationType.HAS_MANY) {
      WhereFilter where = PersistedModel.createIDFilter(relation.getRelatedModelClass(), fk);
      where = where.merge(relationScope);
      Filter filter = new Filter();
      filter.setWhere(where);
      return (M) PersistedModel.findOne(relation.getRelatedModelClass(), filter);
    }
    return null;
    // TODO hasManyThrough
  }

  public <M extends PersistedModel> M replaceByFk(M model, Serializable fk) throws
      ModelNotFoundException {
    return null;
  }

  public <M extends PersistedModel> M updateAttributes(Serializable id, Map<String, Object>
      data) throws ModelNotFoundException, CouldNotPerformException{
    return null;
  }

  public <M extends PersistedModel> M deleteByFk(Serializable fk) throws OperationNotAllowedException, ModelNotFoundException {
    if(relation.getRelationType() == RelationType.BELONGS_TO || relation.getRelationType() ==
        RelationType.HAS_ONE ) {
      Class<? extends PersistedModel> relModelClass = relation.getRelatedModelClass();
      throw new OperationNotAllowedException(relModelClass, "deleteByFk", "can not deleteByFk for "
          + "hasOne or belongs "
          + "to entity");
    }

    if(relation.getRelationType() == RelationType.HAS_MANY) {
      WhereFilter where = PersistedModel.createIDFilter(relation.getRelatedModelClass(), fk);
      where = where.merge(relationScope);
      Filter filter = new Filter();
      filter.setWhere(where);
      return (M) PersistedModel.findOne(relation.getRelatedModelClass(), filter).destroy();
    }
    return null;
    // TODO hasManyThrough
  }

  public long count(WhereFilter where, WhereFilter throughWhere) {
    if(relation.getRelationType() == RelationType.HAS_ONE || relation.getRelationType() ==
        RelationType.BELONGS_TO || relation.getRelationType() == RelationType.HAS_MANY) {
      if(where == null) {
        where = new WhereFilter();
      }
      where = where.merge(relationScope);
      return PersistedModel.count(relation.getRelatedModelClass(), where);
    }else if(relation.getRelationType() == RelationType.HAS_MANY_THROUGH){
//      return Model.getProvider().find(this);
      return 0;
    }
    throw new ModelNotConfiguredException(fromModel.getClass());
  }

  public boolean exists(Serializable fk) throws OperationNotAllowedException {
    if(relation.getRelationType() == RelationType.BELONGS_TO || relation.getRelationType() ==
        RelationType.HAS_ONE ) {
      Class<? extends PersistedModel> relModelClass = relation.getRelatedModelClass();
      throw new OperationNotAllowedException(relModelClass, "exists", "can not exists for "
          + "hasOne or belongs "
          + "to entity");
    }

    if(relation.getRelationType() == RelationType.HAS_MANY) {
      WhereFilter where = PersistedModel.createIDFilter(relation.getRelatedModelClass(), fk);
      where = where.merge(relationScope);

      return PersistedModel.count(relation.getRelatedModelClass(), where) > 0;
    }else if(relation.getRelationType() == RelationType.HAS_MANY_THROUGH) {
      JSONObject obj = new JSONObject();
      obj.put(relation.getToThroughPropertyName(), fromModel.getPropertyValue(relation.getFromPropertyName()));
      obj.put(relation.getFromThroughPropertyName(), fk);
      WhereFilter where = new WhereFilter(obj);
      return PersistedModel.count(relation.getThroughModelClass(), where) > 0;
    }

    return false;
  }
}
