package com.flipkart.sample.resources;

import com.flipkart.loopback.resources.BaseResource;
import com.flipkart.sample.entity.TempModel;
import io.swagger.annotations.Api;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

/**
 * Created by akshaya.sharma on 06/03/18
 */

@Api(tags = {"Temp"})
@Path("/temp")
@Produces(MediaType.APPLICATION_JSON)
public class TempResource extends BaseResource<TempModel> {

  @Override
  public Class getModelClass() {
    return TempModel.class;
  }

}