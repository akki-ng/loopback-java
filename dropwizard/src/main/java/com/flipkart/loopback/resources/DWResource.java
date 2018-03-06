package com.flipkart.loopback.resources;

import com.flipkart.loopback.filter.Filter;
import com.flipkart.loopback.filter.WhereFilter;
import com.flipkart.loopback.model.PersistedModel;
import java.util.List;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.HEAD;
import javax.ws.rs.PATCH;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;

/**
 * Created by akshaya.sharma on 06/03/18
 */

public interface DWResource<T extends PersistedModel> {
  /*
    Patch an existing model instance or insert a new one into the data source
   */
  @PATCH
  @Path("/")
  public T patchOrInsert(T model);

  /*
    Find all instances of the model matched by filter from the data source.
   */
  @GET
  @Path("/")
  public List<T> getAll(@QueryParam("filter") Filter filter, @Context HttpServletRequest request);

  /*
    Replace an existing model instance or insert a new one into the data source.
   */
  @PUT
  @Path("/")
  public T replaceOrCreate(T model);

  /*
    Create a new instance of the model and persist it into the data source.
   */
  @POST
  @Path("/")
  public T create(T model);

  /*
    Patch attributes for a model instance and persist it into the data source.
   */
  @PATCH
  @Path("/{id}")
  public T updateAttributes(@PathParam("id") String id, T model);

  /*
    Find a model instance by {{id}} from the data source.
   */
  @GET
  @Path("/{id}")
  public T findById(@PathParam("id") String id);

  // TODO
  /*
    Check whether a model instance exists in the data source.
    {
      "exists": true
    }
   */
  @HEAD
  @Path("/{id}")
  public boolean existsByHead(@PathParam("id") String id);

  // TODO
  /*
    Check whether a model instance exists in the data source.
    {
      "exists": true
    }
   */
  @GET
  @Path("/{id}/exists")
  public boolean exists(@PathParam("id") String id);

  /*
    Replace attributes for a model instance and persist it into the data source.
   */
  @PUT
  @Path("/{id}")
  public T replaceByPut(@PathParam("id") String id, T model);

  /*
   Replace attributes for a model instance and persist it into the data source.
  */
  @POST
  @Path("/{id}/replace")
  public T replaceByPost(@PathParam("id") String id, T model);

  /*
   Delete a model instance by {{id}} from the data source.
  */
  @DELETE
  @Path("/{id}")
  public void deleteById(@PathParam("id") String id);

  /*
    Count instances of the model matched by where from the data source.
   */
  @GET
  @Path("/count")
  public int count(@QueryParam("filter") Filter filter, @Context HttpServletRequest request);

  /*
    Update instances of the model matched by {{where}} from the data source.
   */
  @POST
  @Path("/update")
  public int update(@QueryParam("where") WhereFilter where, T model, @Context HttpServletRequest
      request);

  /*
    Update an existing model instance or insert a new one into the data source based on the where criteria.
   */
  @POST
  @Path("/upsertWithWhere") T upsertWithWhere(@QueryParam("where") WhereFilter where, T model,
                                              @Context HttpServletRequest
  request);
}
