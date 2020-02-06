package com.planner.controller.custom_responseEntityExceptionHandler;

import com.mindscapehq.raygun4java.core.RaygunClient;
import com.planner.common.custum_exceptions.DataNotFoundByIdException;
import com.planner.common.custum_exceptions.FieldAlreadyExistsException;
import com.planner.component.exception.ExceptionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import javax.mail.MessagingException;
import java.util.ArrayList;
import java.util.List;

import static com.planner.constants.PlannerMessagesConstants.INTERNAL_SERVER_ERROR;

@ControllerAdvice
@Order(1)
public class CustomResponseEntityExceptionHandler extends ResponseEntityExceptionHandler{


    public CustomResponseEntityExceptionHandler() {
        super();
    }


    @Autowired
    private ExceptionService exceptionService;
    @Autowired
    private RaygunClient raygunClient;


    @Override
    public ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException ex, HttpHeaders headers, HttpStatus status, WebRequest request) {
        logger.error("exception in planner service", ex);
        List<FieldError> fieldErrors = ex.getBindingResult().getFieldErrors();
        List<ObjectError> globalErrors = ex.getBindingResult().getGlobalErrors();
        List<FieldErrorDTO> errors = new ArrayList<FieldErrorDTO>(fieldErrors.size() + globalErrors.size());
        //  String error;
        for (FieldError fieldError : fieldErrors) {
            FieldErrorDTO error = new FieldErrorDTO(fieldError.getField(), exceptionService.convertMessage(fieldError.getDefaultMessage()));
            errors.add(error);
        }
        for (ObjectError objectError : globalErrors) {
            FieldErrorDTO error = new FieldErrorDTO(objectError.getObjectName(), exceptionService.convertMessage(objectError.getDefaultMessage()));
            errors.add(error);
        }

        ResponseEnvelope errorMessage = new ResponseEnvelope();
        errorMessage.setErrors(errors);
        errorMessage.setSuccess(false);

        return new ResponseEntity<Object>(errorMessage, headers, HttpStatus.UNPROCESSABLE_ENTITY);

    }

    @ExceptionHandler({NullPointerException.class, IllegalArgumentException.class, IllegalStateException.class, Exception.class, MessagingException.class})
    public ResponseEntity<Object> handleInternal(final Exception ex, final WebRequest request) {
        logger.error("error in user service ", ex);
        ResponseEnvelope errorMessage = new ResponseEnvelope();
        errorMessage.setSuccess(false);
        errorMessage.setMessage(exceptionService.convertMessage(INTERNAL_SERVER_ERROR));
        raygunClient.send(ex);
        return handleExceptionInternal(ex, errorMessage, new HttpHeaders(), HttpStatus.INTERNAL_SERVER_ERROR, request);
    }

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
