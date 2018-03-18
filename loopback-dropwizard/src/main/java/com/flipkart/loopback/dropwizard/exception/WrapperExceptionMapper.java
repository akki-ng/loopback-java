package com.flipkart.loopback.dropwizard.exception;

import com.flipkart.loopback.exception.ConfigurationException;
import com.flipkart.loopback.exception.ConnectorException;
import com.flipkart.loopback.exception.ConnectorNotFoundException;
import com.flipkart.loopback.exception.CouldNotPerformException;
import com.flipkart.loopback.exception.IdFieldNotFoundException;
import com.flipkart.loopback.exception.InternalError;
import com.flipkart.loopback.exception.InvalidFilterException;
import com.flipkart.loopback.exception.InvalidOperatorException;
import com.flipkart.loopback.exception.InvalidPropertyValueException;
import com.flipkart.loopback.exception.InvalidScopeException;
import com.flipkart.loopback.exception.LoopbackException;
import com.flipkart.loopback.exception.ModelNotConfiguredException;
import com.flipkart.loopback.exception.ModelNotFoundException;
import com.flipkart.loopback.exception.OperationNotAllowedException;
import com.flipkart.loopback.exception.PropertyNotFoundException;
import com.flipkart.loopback.exception.RelationNotFound;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import org.eclipse.jetty.http.HttpStatus.Code;

/**
 * Created by akshaya.sharma on 18/03/18
 */

public class WrapperExceptionMapper implements ExceptionMapper<WrapperException> {

  @AllArgsConstructor
  @Getter
  private static enum ExceptionCode {
    UNHANDLED_EXCEPTION(LoopbackException.class,
        Code.INTERNAL_SERVER_ERROR), InvalidFilterException(InvalidFilterException.class,
        Code.UNPROCESSABLE_ENTITY), InvalidOperatorException(InvalidOperatorException.class,
        Code.UNPROCESSABLE_ENTITY), ConfigurationException(ConfigurationException.class,
        Code.INTERNAL_SERVER_ERROR), ConnectorException(ConnectorException.class,
        Code.INTERNAL_SERVER_ERROR), ConnectorNotFoundException(ConnectorNotFoundException.class,
        Code.INTERNAL_SERVER_ERROR), CouldNotPerformException(CouldNotPerformException.class,
        Code.FORBIDDEN), IdFieldNotFoundException(IdFieldNotFoundException.class,
        Code.UNPROCESSABLE_ENTITY), InternalError(
        com.flipkart.loopback.exception.InternalError.class,
        Code.INTERNAL_SERVER_ERROR), InvalidPropertyValueException(
        InvalidPropertyValueException.class, Code.UNPROCESSABLE_ENTITY), InvalidScopeException(
        InvalidScopeException.class, Code.INTERNAL_SERVER_ERROR), ModelNotConfiguredException(
        ModelNotConfiguredException.class, Code.INTERNAL_SERVER_ERROR), ModelNotFoundException(
        ModelNotFoundException.class, Code.NOT_FOUND), OperationNotAllowedException(
        OperationNotAllowedException.class, Code.FORBIDDEN), PropertyNotFoundException(
        PropertyNotFoundException.class, Code.UNPROCESSABLE_ENTITY), RelationNotFound(
        RelationNotFound.class, Code.BAD_REQUEST);

    private final Class<? extends LoopbackException> exceptionClass;
    private final Code exceptionCode;

    public static ExceptionCode fromClass(Class<? extends LoopbackException> exceptionClass) {

      for (int i = 0; i < ExceptionCode.values().length; i++) {
        ExceptionCode code = ExceptionCode.values()[i];
        if (code.getExceptionClass().equals(exceptionClass)) {
          return code;
        }
      }
      return UNHANDLED_EXCEPTION;
    }
  }

  @Data
  @AllArgsConstructor
  class HttpErrorBody {
    private final String message;
    private final String errorCode;
  }

  @Override
  public Response toResponse(WrapperException e) {
    LoopbackException ex = (LoopbackException) e.getCause();
    if (ex instanceof InternalError) {
      return Response.status(500).entity("INTERNAL_SERVER_ERROR").type(
          MediaType.TEXT_PLAIN).build();
    } else if (ex instanceof ModelNotFoundException) {
      return Response.status(404).entity(ex.getMessage()).type(MediaType.TEXT_PLAIN).build();
    }
    return Response.status(500).entity("INTERNAL_SERVER_ERROR").type(MediaType.TEXT_PLAIN).build();
  }
}
