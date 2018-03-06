package com.flipkart.loopback.resources;

import com.flipkart.loopback.exception.LoopbackException;
import com.flipkart.loopback.filter.Filter;
import com.flipkart.loopback.filter.WhereFilter;
import com.flipkart.loopback.model.PersistedModel;
import java.util.List;
import javax.servlet.http.HttpServletRequest;

/**
 * Created by akshaya.sharma on 06/03/18
 */

public abstract class BaseResource<T extends PersistedModel> implements DWResource<T> {
  public abstract <C extends PersistedModel> Class<C> getModelClass();

  @Override
  public T patchOrInsert(T model) {
    return T.updateOrCreate(model);
  }

  @Override
  public List<T> getAll(Filter filter, HttpServletRequest request) {
    return T.find(getModelClass(), filter);
  }

  @Override
  public T replaceOrCreate(T model) {
    return T.replaceOrCreate(model);
  }

  @Override
  public T create(T model) {
    return T.create(model);
  }

  @Override
  public T updateAttributes(String id, T model) {
    try {
      if(!model.getStringifiedId().equals(id)) {
        throw new LoopbackException("id mismatch");
      }
      return (T)model.updateAttributes();
    } catch (LoopbackException e) {
      e.printStackTrace();
    }
    return null;
  }

  @Override
  public T findById(String id) {
    return (T) T.findById(getModelClass(), id);
  }

  @Override
  public boolean existsByHead(String id) {
    return exists(id);
  }

  @Override
  public boolean exists(String id) {
    return T.exists(getModelClass(), id);
  }

  @Override
  public T replaceByPut(String id, T model) {
    return replaceByPost(id, model);
  }

  @Override
  public T replaceByPost(String id, T model) {
    return T.replaceById(model,id);
  }

  @Override
  public void deleteById(String id) {
    T.destroyById(getModelClass(), id);
  }

  @Override
  public int count(Filter filter, HttpServletRequest request) {
    return T.count(getModelClass(), filter);
  }

  @Override
  public int update(WhereFilter where, T model, HttpServletRequest request) {
    return T.updateAll(model, where);
  }

  @Override
  public T upsertWithWhere(WhereFilter where, T model, HttpServletRequest request) {
    return (T) T.upsertWithWhere(getModelClass(), where, model.getFieldMap());
  }
}it
