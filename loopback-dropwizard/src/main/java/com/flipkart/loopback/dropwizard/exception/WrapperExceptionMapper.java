package com.flipkart.loopback.dropwizard.exception;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.flipkart.loopback.exception.LoopbackException;
import com.flipkart.loopback.exception.configuration.ConfigurationException;
import com.flipkart.loopback.exception.configuration.ConnectorNotFoundException;
import com.flipkart.loopback.exception.configuration.ModelNotConfiguredException;
import com.flipkart.loopback.exception.external.ConnectorException;
import com.flipkart.loopback.exception.model.CouldNotPerformException;
import com.flipkart.loopback.exception.model.OperationNotAllowedException;
import com.flipkart.loopback.exception.model.persistence.ModelNotFoundException;
import com.flipkart.loopback.exception.model.relation.InvalidScopeException;
import com.flipkart.loopback.exception.model.relation.RelationNotFound;
import com.flipkart.loopback.exception.validation.filter.InvalidFilterException;
import com.flipkart.loopback.exception.validation.filter.InvalidOperatorException;
import com.flipkart.loopback.exception.validation.model.IdFieldNotFoundException;
import com.flipkart.loopback.exception.validation.model.InvalidPropertyValueException;
import com.flipkart.loopback.exception.validation.model.PropertyNotFoundException;
import com.flipkart.loopback.exception.validation.model.ReadOnlyPropertyException;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.eclipse.jetty.http.HttpStatus.Code;

/**
 * Created by akshaya.sharma on 18/03/18
 */

public class WrapperExceptionMapper implements ExceptionMapper<WrapperException> {

  @AllArgsConstructor
  @Getter
  private static enum ExceptionDetails {
    UNHANDLED_EXCEPTION(LoopbackException.class, Code.INTERNAL_SERVER_ERROR, "INTERNAL_ERROR"),
    InvalidFilterException(InvalidFilterException.class, Code.UNPROCESSABLE_ENTITY,
        "INVALID_FILTER", true),
    InvalidOperatorException(InvalidOperatorException.class, Code.UNPROCESSABLE_ENTITY,
        "INVALID_FILTER", true),
    ConfigurationException(ConfigurationException.class, Code.INTERNAL_SERVER_ERROR,
        "INTERNAL_ERROR"),
    ConnectorException(ConnectorException.class, Code.INTERNAL_SERVER_ERROR, "INTERNAL_ERROR"),
    ConnectorNotFoundException(ConnectorNotFoundException.class, Code.INTERNAL_SERVER_ERROR,
        "INTERNAL_ERROR"), CouldNotPerformException(CouldNotPerformException.class, Code.FORBIDDEN,
        "OPERATION_NOT_PERFORMED", true),
    IdFieldNotFoundException(IdFieldNotFoundException.class, Code.UNPROCESSABLE_ENTITY,
        "INVALID_DATA", true),
    InternalError(com.flipkart.loopback.exception.model.InternalError.class,
        Code.INTERNAL_SERVER_ERROR, "INTERNAL_ERROR"),
    InvalidPropertyValueException(InvalidPropertyValueException.class, Code.UNPROCESSABLE_ENTITY,
        "INVALID_DATA", true),
    InvalidScopeException(InvalidScopeException.class, Code.INTERNAL_SERVER_ERROR,
        "INTERNAL_ERROR"),
    ModelNotConfiguredException(ModelNotConfiguredException.class, Code.INTERNAL_SERVER_ERROR,
        "INTERNAL_ERROR"),
    ModelNotFoundException(ModelNotFoundException.class, Code.NOT_FOUND, "MODEL_NOT_FOUND", true),
    OperationNotAllowedException(OperationNotAllowedException.class, Code.FORBIDDEN,
        "OPERATION_NOT_PERMITTED", true),
    ReadOnlyPropertyException(ReadOnlyPropertyException.class, Code.UNPROCESSABLE_ENTITY,
        "OPERATION_NOT_PERMITTED", true),
    PropertyNotFoundException(PropertyNotFoundException.class, Code.UNPROCESSABLE_ENTITY,
        "INVALID_DATA", true),
    RelationNotFound(RelationNotFound.class, Code.BAD_REQUEST, "RELATION_NOT_FOUND", true);

    private final Class<? extends Throwable> exceptionClass;
    private final Code exceptionCode;
    private final String errorCode;
    private final boolean allowMessageForward;

    ExceptionDetails(Class<? extends Throwable> exceptionClass, Code exceptionCode,
        String errorCode) {
      this.exceptionClass = exceptionClass;
      this.exceptionCode = exceptionCode;
      this.errorCode = errorCode;
      this.allowMessageForward = false;
    }

    public static ExceptionDetails fromClass(Class<? extends Throwable> exceptionClass) {

      for (int i = 0; i < ExceptionDetails.values().length; i++) {
        ExceptionDetails code = ExceptionDetails.values()[i];
        if (code.getExceptionClass().isAssignableFrom(exceptionClass)) {
          return code;
        }
      }
      return UNHANDLED_EXCEPTION;
    }
  }

  @AllArgsConstructor
  class HttpErrorBody {
    private final ExceptionDetails detail;
    private final Throwable exception;

    @JsonProperty("STATUS_CODE")
    public int getStatusCode() {
      return detail.getExceptionCode().getCode();
    }

    @JsonProperty("ERROR_CODE")
    public String getErrorCode() {
      return detail.getErrorCode();
    }

    @JsonProperty("DESCRIPTION")
    public String getDescription() {
      if (detail.allowMessageForward && exception != null) {
        return exception.getMessage();
      }
      return getErrorCode();
    }
  }

  @Override
  public Response toResponse(WrapperException e) {
    Throwable ex = e.getCause();
    ExceptionDetails detail = ExceptionDetails.fromClass(ex.getClass());
    return Response.status(detail.getExceptionCode().getCode()).entity(
        new HttpErrorBody(detail, ex)).build();
  }
}
