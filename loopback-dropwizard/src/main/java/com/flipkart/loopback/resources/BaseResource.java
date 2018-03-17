package com.flipkart.loopback.resources;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.flipkart.loopback.configuration.ModelConfiguration;
import com.flipkart.loopback.configuration.manager.ModelConfigurationManager;
import com.flipkart.loopback.constants.RelationType;
import com.flipkart.loopback.exception.LoopbackException;
import com.flipkart.loopback.filter.Filter;
import com.flipkart.loopback.filter.WhereFilter;
import com.flipkart.loopback.model.PersistedModel;
import com.flipkart.loopback.relation.RelatedModel;
import com.flipkart.loopback.relation.Relation;
import java.io.IOException;
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
  public long count(WhereFilter where, ContainerRequestContext requestContext) {
    return T.count(getModelClass(), where);
  }

  @Override
  public long update(WhereFilter where, T model, ContainerRequestContext requestContext) {
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
  public Object getOnRelatedModel(String id, String relationRestPath, Filter relatedModelfilter,
                                  ContainerRequestContext
                                      requestContext) {
    try {
      T model = findById(id, null, requestContext);
      Relation relation = model.getRelationByRestPath(relationRestPath);
      RelatedModel relatedModel = model.getRelatedModel(relation);
      if(relatedModel.isManyRelation()) {
        List<PersistedModel> result = relatedModel.find(relatedModelfilter);
        return result;
      }
      PersistedModel result = relatedModel.find(relatedModelfilter);
      return result;
    } catch (Throwable e) {
      return null;
    }
  }

  @Override
  public PersistedModel fineOneRelatedModelEntity(String id, String relationRestPath,
                                                String fk, ContainerRequestContext requestContext) {
    try {
      T model = findById(id, null, requestContext);
      Relation relation = model.getRelationByRestPath(relationRestPath);
      RelatedModel relatedModel = model.getRelatedModel(relation);
      if(relatedModel.isManyRelation()) {
        return relatedModel.findById(fk);
      }
      throw new LoopbackException("Its not a many relation");
    } catch (Throwable e) {
      return null;
    }
  }

  @Override
  public PersistedModel createRelatedEntity(String id, String relationRestPath,
                                                          Map<String, Object> data,
                                                          ContainerRequestContext requestContext) {
    try {
      T model = findById(id, null, requestContext);
      Relation relation = model.getRelationByRestPath(relationRestPath);
      RelatedModel relatedModel = model.getRelatedModel(relation);

      PersistedModel transientInstance = relatedModel.getRelation().getInstance(data);
      transientInstance = relatedModel.create(transientInstance);
      return transientInstance;
    } catch (Throwable e) {
      return null;
    }
  }

  @Override
  public PersistedModel patchOrInsertRelatedEntity(String id, String relationRestPath, Map<String, Object> data,
                           ContainerRequestContext requestContext) {
    try {
      T model = findById(id, null, requestContext);
      Relation relation = model.getRelationByRestPath(relationRestPath);
      RelatedModel relatedModel = model.getRelatedModel(relation);
      PersistedModel relatedInstance = relatedModel.updateOrCreate(data);
      return relatedInstance;
    } catch (Throwable e) {
      return null;
    }
  }

  @Override
  public long destroyAllRelatedEntities(String id, String relationRestPath, WhereFilter where,
                                       Map<String, Object> data,
                                       ContainerRequestContext requestContext) throws
      LoopbackException, IOException, IllegalAccessException {
    T model = findById(id, null, requestContext);
    Relation relation = model.getRelationByRestPath(relationRestPath);
    RelatedModel relatedModel = model.getRelatedModel(relation);
    return relatedModel.destroyAll(where);
  }

  @Override
  public PersistedModel deleteOneRelatedModelEntity(String id, String relationRestPath, String fk,
                                                    ContainerRequestContext requestContext) {
    try {
      T model = findById(id, null, requestContext);
      Relation relation = model.getRelationByRestPath(relationRestPath);
      RelatedModel relatedModel = model.getRelatedModel(relation);
      if(relatedModel.isManyRelation()) {
        return relatedModel.findById(fk).destroy();
      }
      throw new LoopbackException("Its not a many relation");
    } catch (Throwable e) {
      return null;
    }
  }

  @Override
  public PersistedModel replaceRelatedEntity(String id, String relationRestPath, String fk,
                                             Map<String, Object> data,
                                             ContainerRequestContext requestContext) {
    try {
      T model = findById(id, null, requestContext);
      Relation relation = model.getRelationByRestPath(relationRestPath);
      RelatedModel relatedModel = model.getRelatedModel(relation);
      if(relatedModel.isManyRelation()) {
        PersistedModel transientInstance = relatedModel.getRelation().getInstance(data);
        return relatedModel.replaceById(transientInstance, fk);
      }
      throw new LoopbackException("Its not a many relation");
    } catch (Throwable e) {
      return null;
    }
  }

  @Override
  public PersistedModel patchRelatedEntity(String id, String relationRestPath, String fk,
                                           Map<String, Object> data,
                                           ContainerRequestContext requestContext) {
    try {
      T model = findById(id, null, requestContext);
      Relation relation = model.getRelationByRestPath(relationRestPath);
      RelatedModel relatedModel = model.getRelatedModel(relation);
      PersistedModel relatedInstance = relatedModel.updateAttributes(fk, data);
      return relatedInstance;
    } catch (Throwable e) {
      return null;
    }
  }
}
