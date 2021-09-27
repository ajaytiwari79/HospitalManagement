package com.kairos.persistence.model;

import com.kairos.commons.custom_exception.DataNotFoundException;
import com.kairos.commons.custom_exception.DataNotMatchedException;
import com.kairos.commons.custom_exception.DuplicateDataException;
import com.kairos.commons.custom_exception.TimeTypeLinkedException;
import com.kairos.commons.utils.CommonsExceptionUtil;
import org.springframework.stereotype.Service;

/**
 * Created by vipul on 11/5/18.
 */
@Service
public class ExceptionService extends CommonsExceptionUtil {


    public void dataNotFoundByIdException(String message, Object... params) {
        throw new com.kairos.commons.custom_exception.DataNotFoundByIdException(message, params);
    }

    public void actionNotPermittedException(String message, Object... params) {
        throw new com.kairos.commons.custom_exception.ActionNotPermittedException(message, params);
    }

    //This method is for throwing exception without converting message in RestClient
    public void exceptionWithoutConvertInRestClient(String message) {
        throw new com.kairos.commons.custom_exception.ActionNotPermittedException(message);
    }

    public void invalidRequestException(String message, Object... params) {
        throw new com.kairos.commons.custom_exception.InvalidRequestException(message, params);
    }

    public void duplicateDataException(String message, Object... params) {
        throw new DuplicateDataException(message, params);
    }
    public void internalError(String message,Object... params) {
        throw new InternalError(convertMessage(message,params));
    }

    public void illegalArgumentException(String message,Object... params) {
        throw new IllegalArgumentException(convertMessage(message,params));
    }

    public void dataNotMatchedException(String message,Object... params) {
        throw new DataNotMatchedException(message, params);
    }

    public void internalServerError(String message,Object... params) {
        throw new InternalError(convertMessage(message,params));
    }


    public void timeTypeLinkedException(String message, Object... params) {
        throw new TimeTypeLinkedException(message, params);
    }
    public void unsupportedOperationException(String message,Object... params) {
        throw new UnsupportedOperationException(convertMessage(message,params));
    }
    public void dataNotFoundException(String message, Object... params) {
        throw new DataNotFoundException(message, params);
    }

    public String getLanguageSpecificText(String message,Object... params){
        return convertMessage(message,params);
    }
}
