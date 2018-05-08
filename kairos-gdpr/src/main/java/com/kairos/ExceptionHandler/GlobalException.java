package com.kairos.ExceptionHandler;


import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;

@ControllerAdvice
public class GlobalException  extends ResponseEntityExceptionHandler {



@ExceptionHandler(value = {WorkSpaceExistException.class})
    protected ResponseEntity<Object> workSpaceExists(RuntimeException ex , HttpServletRequest request)
{

    Map<String,Object> result=new HashMap<>();
    result.put("Exception",ex.getMessage());
    result.put("cause",ex.getCause());
    result.put("request URl",request.getRequestURL());
    return new ResponseEntity<>(result,HttpStatus.BAD_REQUEST);

}

    @ExceptionHandler(value = {DuplicateDataException.class,NotExists.class,DataNotFoundByIdException.class})
    protected ResponseEntity<Object> duplicateDataException(RuntimeException ex , HttpServletRequest request)
    {

        Map<String,Object> result=new HashMap<>();
        result.put("Exception",ex.getMessage());
        result.put("cause",ex.getCause());
        result.put("request URl",request.getRequestURI());
        return new ResponseEntity<>(result,HttpStatus.BAD_REQUEST);

    }








}
