package com.flipkart.loopback.resources;

import com.flipkart.loopback.filter.Filter;
import com.flipkart.loopback.filter.WhereFilter;
import com.flipkart.loopback.model.PersistedModel;
import java.util.List;
import java.util.Map;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.HEAD;
import javax.ws.rs.PATCH;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.QueryParam;
import javax.ws.rs.container.ContainerRequestContext;
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
  public T patchOrInsert(Map<String, Object> patchData, @Context ContainerRequestContext
      requestContext);

  /*
    Find all instances of the model matched by filter from the data source.
   */
  @GET
  @Path("/")
  public List<T> getAll(@QueryParam("filter") Filter filter, @Context ContainerRequestContext requestContext);

  /*
    Replace an existing model instance or insert a new one into the data source.
   */
  @PUT
  @Path("/")
  //TODO
  public T replaceOrCreate(T model, @Context ContainerRequestContext requestContext);

  /*
    Create a new instance of the model and persist it into the data source.
   */
  @POST
  @Path("/")
  public T create(T model, @Context ContainerRequestContext requestContext);

  /*
    Create bulk instances of the model and persist them into the data source.
   */
  @POST
  @Path("/bulk")
  public List<T> create(List<T> models, @Context ContainerRequestContext requestContext);

  /*
    Patch attributes for a model instance and persist it into the data source.
   */
  @PATCH
  @Path("/{id}")
  public T updateAttributes(@PathParam("id") String id, Map<String, Object> patchData, @Context ContainerRequestContext requestContext);

  /*
    Find a model instance by {{id}} from the data source.
   */
  @GET
  @Path("/{id}")
  public T findById(@PathParam("id") String id, @QueryParam("filter") Filter filter, @Context
      ContainerRequestContext requestContext);

  // TODO
  /*
    Check whether a model instance exists in the data source.
    {
      "exists": true
    }
   */
  @HEAD
  @Path("/{id}")
  public boolean existsByHead(@PathParam("id") String id, @Context ContainerRequestContext requestContext);

  // TODO
  /*
    Check whether a model instance exists in the data source.
    {
      "exists": true
    }
   */
  @GET
  @Path("/{id}/exists")
  public boolean exists(@PathParam("id") String id, @Context ContainerRequestContext requestContext);

  /*
    Replace attributes for a model instance and persist it into the data source.
   */
  @PUT
  @Path("/{id}")
  public T replaceByPut(@PathParam("id") String id, T model, @Context ContainerRequestContext requestContext);

  /*
   Replace attributes for a model instance and persist it into the data source.
  */
  @POST
  @Path("/{id}/replace")
  public T replaceByPost(@PathParam("id") String id, T model, @Context ContainerRequestContext requestContext);

  /*
   Delete a model instance by {{id}} from the data source.
  */
  @DELETE
  @Path("/{id}")
  public T deleteById(@PathParam("id") String id, @Context ContainerRequestContext requestContext);

  /*
    Count instances of the model matched by where from the data source.
   */
  @GET
  @Path("/count")
  public long count(@QueryParam("filter") Filter filter, @Context ContainerRequestContext
      requestContext);

  /*
    Update instances of the model matched by {{where}} from the data source.
   */
  @POST
  @Path("/update")
  public long update(@QueryParam("where") WhereFilter where, T model, @Context ContainerRequestContext requestContext);

  /*
    Update an existing model instance or insert a new one into the data source based on the where criteria.
   */
  @POST
  @Path("/upsertWithWhere") T upsertWithWhere(@QueryParam("where") WhereFilter where, T model
      , @Context ContainerRequestContext requestContext);

//  /*
//    Fetches hasOne relation
//   */
//  @GET
//  @Path("/{id}/{relation}")
//  public <R extends PersistedModel> R getHasOneRelatedModel(@PathParam("id") String id, @PathParam
//      ("relation") String relationRestPath, @Context ContainerRequestContext requestContext);
//
//  /*
//   Fetches hasMany relation
//  */
//  @GET
//  @Path("/{id}/{relation}")
//  public <R extends PersistedModel> List<R> getHasManyRelatedModel(@PathParam("id") String id,
//                                                               @PathParam
//      ("relation") String relationRestPath, @QueryParam("filter") Filter filter, @Context
//                                                                         ContainerRequestContext requestContext);

  @GET
  @Path("/{id}/{relation}")
  public Object getOnRelatedModel(@PathParam("id") String id,
                                                               @PathParam
      ("relation") String relationRestPath, @QueryParam("filter") Filter filter, @Context
                                        ContainerRequestContext requestContext);

  @GET
  @Path("/{id}/{relation}/{fk}")
  public PersistedModel getOnRelatedModelEntity(@PathParam("id") String id,
                                  @PathParam
                                      ("relation") String relationRestPath, @PathParam("fk")
                                              String fk, @QueryParam("filter") Filter filter,
                                        @Context
                                      ContainerRequestContext requestContext);
}
