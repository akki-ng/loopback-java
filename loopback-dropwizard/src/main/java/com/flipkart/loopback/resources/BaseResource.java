package com.flipkart.loopback.resources;

import com.flipkart.loopback.dropwizard.exception.WrapperException;
import com.flipkart.loopback.filter.Filter;
import com.flipkart.loopback.filter.WhereFilter;
import com.flipkart.loopback.model.PersistedModel;
import com.flipkart.loopback.relation.RelatedModel;
import com.flipkart.loopback.relation.Relation;
import java.io.Serializable;
import java.util.List;
import java.util.Map;
import javax.ws.rs.container.ContainerRequestContext;

/**
 * Created by akshaya.sharma on 06/03/18
 */

public abstract class BaseResource<T extends PersistedModel> implements DWResource<T> {
  public abstract <C extends PersistedModel> Class<C> getModelClass();

  @Override
  public T patchOrInsert(Map<String, Serializable> patchData,
      ContainerRequestContext requestContext) throws WrapperException {
    try {
      return T.updateOrCreate(getModelClass(), patchData);
    } catch (Throwable e) {
      throw new WrapperException(e);
    }
  }

  @Override
  public List<T> getAll(Filter filter,
      ContainerRequestContext requestContext) throws WrapperException {
    try {
      return T.find(getModelClass(), filter);
    } catch (Throwable e) {
      throw new WrapperException(e);
    }
  }

  @Override
  public T replaceOrCreate(T model,
      ContainerRequestContext requestContext) throws WrapperException {
    try {
      return T.replaceOrCreate(model);
    } catch (Throwable e) {
      throw new WrapperException(e);
    }
  }

  @Override
  public T create(T model, ContainerRequestContext requestContext) throws WrapperException {
    try {
      return T.create(model);
    } catch (Throwable e) {
      throw new WrapperException(e);
    }
  }

  @Override
  public List<T> create(List<T> models,
      ContainerRequestContext requestContext) throws WrapperException {
    try {
      return T.create(models);
    } catch (Throwable e) {
      throw new WrapperException(e);
    }
  }

  @Override
  public T updateAttributes(String id, Map<String, Object> patchData,
      ContainerRequestContext requestContext) throws WrapperException {
    try {
      T model = T.findById(getModelClass(), null, id);
      return (T) model.updateAttributes(patchData);
    } catch (Throwable e) {
      throw new WrapperException(e);
    }
  }

  @Override
  public T findById(String id, Filter filter,
      ContainerRequestContext requestContext) throws WrapperException {
    try {
      T model = T.findById(getModelClass(), filter, id);
      return model;
    } catch (Throwable e) {
      throw new WrapperException(e);
    }
  }

  @Override
  public boolean existsByHead(String id,
      ContainerRequestContext requestContext) throws WrapperException {
    try {
      return exists(id, requestContext);
    } catch (Throwable e) {
      throw new WrapperException(e);
    }
  }

  @Override
  public boolean exists(String id, ContainerRequestContext requestContext) throws WrapperException {
    try {
      return T.exists(getModelClass(), id);
    } catch (Throwable e) {
      throw new WrapperException(e);
    }
  }

  @Override
  public T replaceByPut(String id, T model,
      ContainerRequestContext requestContext) throws WrapperException {
    return replaceByPost(id, model, requestContext);
  }

  @Override
  public T replaceByPost(String id, T model,
      ContainerRequestContext requestContext) throws WrapperException {
    try {
      return T.replaceById(model, id);
    } catch (Throwable e) {
      throw new WrapperException(e);
    }
  }

  @Override
  public T deleteById(String id, ContainerRequestContext requestContext) throws WrapperException {
    try {
      return T.destroyById(getModelClass(), id);
    } catch (Throwable e) {
      throw new WrapperException(e);
    }
  }

  @Override
  public long count(WhereFilter where,
      ContainerRequestContext requestContext) throws WrapperException {
    try {
      return T.count(getModelClass(), where);
    } catch (Throwable e) {
      throw new WrapperException(e);
    }
  }

  @Override
  public long update(WhereFilter where, T model,
      ContainerRequestContext requestContext) throws WrapperException {
    try {
      return T.updateAll(model.getClass(), where, model.getFieldMap());
    } catch (Throwable e) {
      throw new WrapperException(e);
    }
  }

  @Override
  public T upsertWithWhere(WhereFilter where, T model,
      ContainerRequestContext requestContext) throws WrapperException {
    try {
      return (T) T.upsertWithWhere(getModelClass(), where, model.getFieldMap());
    } catch (Throwable e) {
      throw new WrapperException(e);
    }
  }

//  @Override
//  public <R extends PersistedModel> R getHasOneRelatedModel(String id, String relationRestPath,
//                                                ContainerRequestContext requestContext) throws
// WrapperException {
//    try {
//      T model = findById(id, requestContext);
//      Relation r = model.getRelationByRestPath(relationRestPath);
//      return (R) model.getHasOneRelatedModel(r);
//    }catch (Throwable e) {
//      return  null;
//    }
//  }
//
//  @Override
//  public <R extends PersistedModel> List<R> getHasManyRelatedModel(String id, String
//      relationRestPath, Filter filter, ContainerRequestContext requestContext) throws
// WrapperException {
//    try {
//      T model = findById(id, requestContext);
//      Relation r = model.getRelationByRestPath(relationRestPath);
//      return (List) null;
//    }catch (Throwable e) {
//      return  null;
//    }
//  }

  @Override
  public Object getOnRelatedModel(String id, String relationRestPath, Filter relatedModelfilter,
      ContainerRequestContext requestContext) throws WrapperException {
    try {
      T model = findById(id, null, requestContext);
      Relation relation = model.getRelationByRestPath(relationRestPath);
      RelatedModel relatedModel = model.getRelatedModel(relation.getName());
      if (relatedModel.isManyRelation()) {
        List<PersistedModel> result = relatedModel.find(relatedModelfilter);
        return result;
      }
      PersistedModel result = relatedModel.find(relatedModelfilter);
      return result;
    } catch (Throwable e) {
      throw new WrapperException(e);
    }
  }

  @Override
  public PersistedModel fineOneRelatedModelEntity(String id, String relationRestPath, String fk,
      ContainerRequestContext requestContext) throws WrapperException {
    try {
      T model = findById(id, null, requestContext);
      Relation relation = model.getRelationByRestPath(relationRestPath);
      RelatedModel relatedModel = model.getRelatedModel(relation.getName());
      return relatedModel.findById(fk);
    } catch (Throwable e) {
      throw new WrapperException(e);
    }
  }

  @Override
  public PersistedModel createRelatedEntity(String id, String relationRestPath,
      Map<String, Object> data, ContainerRequestContext requestContext) throws WrapperException {
    try {
      T model = findById(id, null, requestContext);
      Relation relation = model.getRelationByRestPath(relationRestPath);
      RelatedModel relatedModel = model.getRelatedModel(relation.getName());

      PersistedModel transientInstance = relatedModel.getRelation().getInstance(data);
      transientInstance = relatedModel.create(transientInstance);
      return transientInstance;
    } catch (Throwable e) {
      throw new WrapperException(e);
    }
  }

  @Override
  public PersistedModel patchOrInsertRelatedEntity(String id, String relationRestPath,
      Map<String, Object> data, ContainerRequestContext requestContext) throws WrapperException {
    try {
      T model = findById(id, null, requestContext);
      Relation relation = model.getRelationByRestPath(relationRestPath);
      RelatedModel relatedModel = model.getRelatedModel(relation.getName());
      PersistedModel relatedInstance = relatedModel.updateOrCreate(data);
      return relatedInstance;
    } catch (Throwable e) {
      throw new WrapperException(e);
    }
  }

  @Override
  public long destroyAllRelatedEntities(String id, String relationRestPath, WhereFilter where,
      Map<String, Object> data, ContainerRequestContext requestContext) throws WrapperException {
    try {
      T model = findById(id, null, requestContext);
      Relation relation = model.getRelationByRestPath(relationRestPath);
      RelatedModel relatedModel = model.getRelatedModel(relation.getName());
      return relatedModel.destroyAll(where);
    } catch (Throwable e) {
      throw new WrapperException(e);
    }
  }

  @Override
  public PersistedModel deleteOneRelatedModelEntity(String id, String relationRestPath, String fk,
      ContainerRequestContext requestContext) throws WrapperException {
    try {
      T model = findById(id, null, requestContext);
      Relation relation = model.getRelationByRestPath(relationRestPath);
      RelatedModel relatedModel = model.getRelatedModel(relation.getName());
      return relatedModel.findById(fk).destroy();
    } catch (Throwable e) {
      throw new WrapperException(e);
    }
  }

  @Override
  public PersistedModel replaceRelatedEntity(String id, String relationRestPath, String fk,
      Map<String, Object> data, ContainerRequestContext requestContext) throws WrapperException {
    try {
      T model = findById(id, null, requestContext);
      Relation relation = model.getRelationByRestPath(relationRestPath);
      RelatedModel relatedModel = model.getRelatedModel(relation.getName());
      PersistedModel transientInstance = relatedModel.getRelation().getInstance(data);
      return relatedModel.replaceById(transientInstance, fk);
    } catch (Throwable e) {
      throw new WrapperException(e);
    }
  }

  @Override
  public PersistedModel patchRelatedEntity(String id, String relationRestPath, String fk,
      Map<String, Object> data, ContainerRequestContext requestContext) throws WrapperException {
    try {
      T model = findById(id, null, requestContext);
      Relation relation = model.getRelationByRestPath(relationRestPath);
      RelatedModel relatedModel = model.getRelatedModel(relation.getName());
      PersistedModel relatedInstance = relatedModel.updateAttributes(fk, data);
      return relatedInstance;
    } catch (Throwable e) {
      throw new WrapperException(e);
    }
  }
}
