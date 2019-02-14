package com.kairos.controller.exception_handler;


import com.kairos.commons.custom_exception.DataNotFoundByIdException;
import com.kairos.commons.custom_exception.DuplicateDataException;
import com.kairos.commons.custom_exception.InvalidRequestException;
import com.kairos.commons.service.locale.LocaleService;
import com.kairos.custom_exception.*;
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

import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
/*
 *
 *  created by bobby 25/4/2018
 * */


@ControllerAdvice
@Order(1)
class GlobalExceptionHandler extends ResponseEntityExceptionHandler {



    @Inject
    private LocaleService localeService;

    private String convertMessage(String message, Object... params) {
        for (int i = 0; i < params.length; i++) {
            try {
                params[i] = localeService.getMessage(params[i].toString());
            } catch (Exception e) {
                // intentionally left empty
            }
        }
        return localeService.getMessage(message, params);
    }


    @Override
    public ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException ex, HttpHeaders headers, HttpStatus status, WebRequest request) {

        logger.error("error in gdpr service ", ex);
        List<FieldError> fieldErrors = ex.getBindingResult().getFieldErrors();
        List<ObjectError> globalErrors = ex.getBindingResult().getGlobalErrors();
        List<FieldErrorDTO> errors = new ArrayList<>(fieldErrors.size() + globalErrors.size());
        for (FieldError fieldError : fieldErrors) {
            FieldErrorDTO error = new FieldErrorDTO(fieldError.getField(),convertMessage( fieldError.getDefaultMessage()));
            errors.add(error);
        }
        for (ObjectError objectError : globalErrors) {
            FieldErrorDTO error = new FieldErrorDTO(objectError.getObjectName(), convertMessage(objectError.getDefaultMessage()));
            errors.add(error);
        }
        ResponseEnvelope errorMessage = new ResponseEnvelope();
        errorMessage.setErrors(errors);
        errorMessage.setSuccess(false);

        return new ResponseEntity<>(errorMessage, HttpStatus.UNPROCESSABLE_ENTITY);

    }

    @ExceptionHandler(value = {DuplicateDataException.class, DataNotExists.class, DataNotFoundByIdException.class,
            InvalidRequestException.class, MetaDataLinkedWithAssetException.class, MetaDataLinkedWithProcessingActivityException.class})
    @ResponseBody
    protected ResponseEntity<Object> exceptionHandler(RuntimeException ex, HttpServletRequest request) {

        Map<String, Object> result = new HashMap<>();
        result.put("message", ex.getMessage());
        result.put("cause", ex.getCause());
        result.put("request URl", request.getRequestURI());
        return new ResponseEntity<>(result, HttpStatus.BAD_REQUEST);

    }


}
