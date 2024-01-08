package com.salesmanager.shop.store.api.exception;

import java.util.Objects;
import java.util.Optional;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

@Order(Ordered.HIGHEST_PRECEDENCE)
@ControllerAdvice({"com.salesmanager.shop.store.api"})
public class RestErrorHandler {
  
    private static final Logger log = LoggerFactory.getLogger(RestErrorHandler.class);

    @RequestMapping(produces = "application/json")
    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public @ResponseBody ErrorEntity handleServiceException(Exception exception) {
        log.error(exception.getMessage(), exception);
        Objects.requireNonNull(exception.getCause());
        Throwable rootCause = exception.getCause();
        /* QECI-fix (2024-01-08 21:10:09.611735):
        Storing the result of rootCause.getCause() in a variable to avoid calling the method multiple times within the loop condition.
        Updating the loop condition to use this variable for checking the loop termination condition.
        */
        Throwable cause = rootCause.getCause();
        while (cause != null && cause != rootCause) {
            rootCause = cause;
            cause = rootCause.getCause();
        }
        ErrorEntity errorEntity = createErrorEntity("500", exception.getMessage(),
                rootCause.getMessage());
        return errorEntity;
    }


    /**
     * Generic exception serviceException handler
     */
}

    @RequestMapping(produces = "application/json")
@ExceptionHandler(ServiceRuntimeException.class)
@ResponseStatus(HttpStatus.BAD_REQUEST)
public @ResponseBody ErrorEntity handleServiceException(ServiceRuntimeException exception) {
    log.error(exception.getErrorMessage(), exception);
    Throwable rootCause = exception.getCause();
    /* QECI-fix (2024-01-08 21:10:09.611735):
    Refactored the loop condition to avoid calling the getCause method multiple times.
    Stored the result of rootCause.getCause() in a variable to use in the loop condition.
    */
    Throwable cause = rootCause != null ? rootCause.getCause() : null;
    while (cause != null && cause != rootCause) {
        rootCause = cause;
        cause = rootCause.getCause();
    }
    ErrorEntity errorEntity = createErrorEntity(exception.getErrorCode()!=null?exception.getErrorCode():"500", exception.getErrorMessage(),
            rootCause.getMessage());
    return errorEntity;
}

@RequestMapping(produces = "application/json")
@ExceptionHandler(ConversionRuntimeException.class)
@ResponseStatus(HttpStatus.BAD_REQUEST)
public @ResponseBody ErrorEntity handleServiceException(ConversionRuntimeException exception) {
    log.error(exception.getErrorMessage(), exception);
    ErrorEntity errorEntity = createErrorEntity(exception.getErrorCode(), exception.getErrorMessage(),
        exception.getLocalizedMessage());
    return errorEntity;
}


    @RequestMapping(produces = "application/json")
    @ExceptionHandler(ResourceNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public @ResponseBody ErrorEntity handleServiceException(ResourceNotFoundException exception) {
        log.error(exception.getErrorMessage(), exception);

        ErrorEntity errorEntity = createErrorEntity(exception.getErrorCode(), exception.getErrorMessage(),
                exception.getLocalizedMessage());
        return errorEntity;
    }
    
    @RequestMapping(produces = "application/json")
    @ExceptionHandler(UnauthorizedException.class)
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    public @ResponseBody ErrorEntity handleServiceException(UnauthorizedException exception) {
        log.error(exception.getErrorMessage(), exception);

        ErrorEntity errorEntity = createErrorEntity(exception.getErrorCode(), exception.getErrorMessage(),
                exception.getLocalizedMessage());
        return errorEntity;
    }

    @RequestMapping(produces = "application/json")
    @ExceptionHandler(RestApiException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public @ResponseBody ErrorEntity handleRestApiException(RestApiException exception) {
        log.error(exception.getErrorMessage(), exception);

        ErrorEntity errorEntity = createErrorEntity(exception.getErrorCode(), exception.getErrorMessage(),
                exception.getLocalizedMessage());
        return errorEntity;
    }

    private ErrorEntity createErrorEntity(String errorCode, String message, String detailMessage) {
        ErrorEntity errorEntity = new ErrorEntity();
        Optional.ofNullable(errorCode)
                .ifPresent(errorEntity::setErrorCode);

        String resultMessage = (message != null && detailMessage !=null)  ? new StringBuilder().append(message).append(", ").append(detailMessage).toString() : detailMessage;
        if(StringUtils.isBlank(resultMessage)) {
        	resultMessage = message;
        }
        Optional.ofNullable(resultMessage)
                .ifPresent(errorEntity::setMessage);
        return errorEntity;
    }
}
