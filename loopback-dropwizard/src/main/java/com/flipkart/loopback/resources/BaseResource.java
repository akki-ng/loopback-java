package com.flipkart.loopback.resources;

import com.flipkart.loopback.constants.RelationType;
import com.flipkart.loopback.exception.LoopbackException;
import com.flipkart.loopback.filter.Filter;
import com.flipkart.loopback.filter.WhereFilter;
import com.flipkart.loopback.model.PersistedModel;
import com.flipkart.loopback.relation.Relation;
import java.util.List;
import java.util.Map;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.core.Context;

/**
 * Created by akshaya.sharma on 06/03/18
 */

public abstract class BaseResource<T extends PersistedModel> implements DWResource<T> {
  public abstract <C extends PersistedModel> Class<C> getModelClass();

  @Override
  public T patchOrInsert(Map<String, Object> patchData, ContainerRequestContext requestContext) {

    try {
      return T.updateOrCreate(getModelClass(), patchData);
    } catch (LoopbackException e) {
      e.printStackTrace();
    }
    return null;
  }

  @Override
  public List<T> getAll(Filter filter, ContainerRequestContext requestContext) {
    return T.find(getModelClass(), filter);
  }

  @Override
  public T replaceOrCreate(T model, ContainerRequestContext requestContext) {
    return T.replaceOrCreate(model);
  }

  @Override
  public T create(T model, ContainerRequestContext requestContext) {
    return T.create(model);
  }

  @Override
  public List<T> create(List<T> models, ContainerRequestContext requestContext) {
    return T.create(models);
  }

  @Override
  public T updateAttributes(String id, Map<String, Object> patchData,
                            ContainerRequestContext requestContext) {
    try {
      T model = T.findById(getModelClass(), null, id);
      return (T) model.updateAttributes(patchData);
    } catch (LoopbackException e) {
      e.printStackTrace();
    }
    return null;
  }

  @Override
  public T findById(String id, Filter filter, ContainerRequestContext requestContext) {
    T model = T.findById(getModelClass(), filter, id);
    if (model == null) {
      // 404
    }
    return model;
  }

  @Override
  public boolean existsByHead(String id, ContainerRequestContext requestContext) {
    return exists(id, requestContext);
  }

  @Override
  public boolean exists(String id, ContainerRequestContext requestContext) {
    return T.exists(getModelClass(), id);
  }

  @Override
  public T replaceByPut(String id, T model, ContainerRequestContext requestContext) {
    return replaceByPost(id, model, requestContext);
  }

  @Override
  public T replaceByPost(String id, T model, ContainerRequestContext requestContext) {
    return T.replaceById(model, id);
  }

  @Override
  public T deleteById(String id, ContainerRequestContext requestContext) {
    return T.destroyById(getModelClass(), id);
  }

  @Override
  public long count(Filter filter, ContainerRequestContext requestContext) {
    return T.count(getModelClass(), filter);
  }

  @Override
  public int update(WhereFilter where, T model, ContainerRequestContext requestContext) {
    return T.updateAll(model.getClass(), where, model.getFieldMap());
  }

  @Override
  public T upsertWithWhere(WhereFilter where, T model, ContainerRequestContext requestContext) {
    return (T) T.upsertWithWhere(getModelClass(), where, model.getFieldMap());
  }

//  @Override
//  public <R extends PersistedModel> R getHasOneRelatedModel(String id, String relationRestPath,
//                                                ContainerRequestContext requestContext) {
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
//      relationRestPath, Filter filter, ContainerRequestContext requestContext) {
//    try {
//      T model = findById(id, requestContext);
//      Relation r = model.getRelationByRestPath(relationRestPath);
//      return (List) null;
//    }catch (Throwable e) {
//      return  null;
//    }
//  }

  @Override
  public Object getOnRelatedModel(String id, String relationRestPath, Filter filter,
                                  ContainerRequestContext
                                      requestContext) {
    try {
      T model = findById(id, null, requestContext);
      Relation r = model.getRelationByRestPath(relationRestPath);
      if (r.getRelationType() == RelationType.HAS_ONE) {
        return model.getHasOneRelatedModel(r);
      } else if (r.getRelationType() == RelationType.HAS_MANY) {
        List res = model.getHasManyRelatedModel(r, filter);
        return res;
      }
      return null;
    } catch (Throwable e) {
      return null;
    }
  }

  public PersistedModel getOnRelatedModelEntity(String id, String relationRestPath,
                                                String fk, Filter filter,
                                                ContainerRequestContext
                                                    requestContext) {
    try {
      T model = findById(id, null, requestContext);
      Relation r = model.getRelationByRestPath(relationRestPath);
      if (r.getRelationType() == RelationType.HAS_MANY) {
        Class<? extends PersistedModel> relModelClass = r.getRelatedModelClass();
        return T.getProvider().findById(relModelClass, null, fk);
      }
      return null;
    } catch (Throwable e) {
      return null;
    }
  }
}
