package com.kairos.controller.exception_handler;


import com.kairos.custome_exception.*;
import com.kairos.controller.exception_handler.dto.FieldErrorDTO;
import com.kairos.controller.exception_handler.dto.ResponseEnvelope;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@ControllerAdvice
@Order(1)
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {



    @Override
    public ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException ex, HttpHeaders headers, HttpStatus status, WebRequest request) {
<<<<<<< HEAD
        logger.error("error in filter service ",ex);
=======
        logger.error("error in user service ",ex);
>>>>>>> d97f2521641985b2ef7974bbd2871f4291a34c91
        List<FieldError> fieldErrors = ex.getBindingResult().getFieldErrors();
        List<ObjectError> globalErrors = ex.getBindingResult().getGlobalErrors();
        List<FieldErrorDTO> errors = new ArrayList<FieldErrorDTO>(fieldErrors.size() + globalErrors.size());
        //  String error;
        for (FieldError fieldError : fieldErrors) {
            FieldErrorDTO error=new FieldErrorDTO(fieldError.getField(),fieldError.getDefaultMessage());
            errors.add(error);
        }
        for (ObjectError objectError : globalErrors) {
            FieldErrorDTO error=new FieldErrorDTO(objectError.getObjectName(),objectError.getDefaultMessage());
            errors.add(error);
        }
        ResponseEnvelope errorMessage = new ResponseEnvelope();
        errorMessage.setErrors(errors);
        errorMessage.setSuccess(false);

        return new ResponseEntity<Object>(errorMessage, HttpStatus.UNPROCESSABLE_ENTITY);

    }

@ExceptionHandler(value = {WorkSpaceExistException.class})
@ResponseBody
    protected ResponseEntity<Object> workSpaceExists(Exception ex , HttpServletRequest request)
{

    Map<String,Object> result=new HashMap<>();
    result.put("message",ex.getMessage());
    result.put("cause",ex.getCause());
    result.put("request URl",request.getRequestURL());
    return new ResponseEntity<>(result,HttpStatus.BAD_REQUEST);

}

    @ExceptionHandler(value = {DuplicateDataException.class,DataNotExists.class,DataNotFoundByIdException.class,OrganizationTypeException.class})
    @ResponseBody
    protected ResponseEntity<Object> duplicateDataException(RuntimeException ex , HttpServletRequest request)
    {

        Map<String,Object> result=new HashMap<>();
        result.put("messgage",ex.getMessage());
        result.put("cause",ex.getCause());
        result.put("request URl",request.getRequestURI());
        return new ResponseEntity<>(result,HttpStatus.BAD_REQUEST);

    }








}
