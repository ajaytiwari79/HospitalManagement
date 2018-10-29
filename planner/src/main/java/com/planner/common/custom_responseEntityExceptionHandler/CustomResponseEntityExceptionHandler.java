package com.planner.common.custom_responseEntityExceptionHandler;

import com.planner.common.custum_exceptions.DataNotFoundByIdException;
import com.planner.common.custum_exceptions.FieldAlreadyExistsException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

public class CustomResponseEntityExceptionHandler extends ResponseEntityExceptionHandler{

    @ExceptionHandler({DataNotFoundByIdException.class})
    public ResponseEntity<Object> handleDataNotFoundException(final Exception ex, final WebRequest request) {
        logger.error("exception in Planner service",ex);
        ResponseEnvelope errorMessage=new ResponseEnvelope();
        errorMessage.setSuccess(false);
        errorMessage.setMessage(ex.getMessage());
        return handleExceptionInternal(ex, errorMessage, new HttpHeaders(), HttpStatus.BAD_REQUEST, request);
    }

    @ExceptionHandler({FieldAlreadyExistsException.class})
    public ResponseEntity<Object> handleFieldAlreadyExistsException(final Exception ex, final WebRequest request) {
        logger.error("exception in Planner service",ex);
        ResponseEnvelope errorMessage=new ResponseEnvelope();
        errorMessage.setSuccess(false);
        errorMessage.setMessage(ex.getMessage());
        return handleExceptionInternal(ex, errorMessage, new HttpHeaders(), HttpStatus.CONFLICT, request);
    }

}
