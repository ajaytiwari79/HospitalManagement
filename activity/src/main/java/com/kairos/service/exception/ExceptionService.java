package com.kairos.service.exception;

import com.kairos.commons.utils.CommonsExceptionUtil;
import com.kairos.custom_exception.*;
import com.kairos.service.fls_visitour.exceptions.scheduler.FlsCallException;
import com.kairos.service.fls_visitour.exceptions.scheduler.SchedulerException;
import org.apache.poi.openxml4j.exceptions.InvalidOperationException;
import org.springframework.stereotype.Service;

import javax.inject.Inject;

/**
 * Created by vipul on 11/5/18.
 */
@Service
public class ExceptionService extends CommonsExceptionUtil {





    public void timeTypeLinkedException(String message, Object... params) {
        throw new TimeTypeLinkedException(convertMessage(message, params));
    }
    public void invalidClientException(String message, Object... params) {
        throw new InvalidClientException(convertMessage(message, params));
    }
    public void schedulerException(String message, Object... params) {
        throw new SchedulerException(convertMessage(message, params));
    }

    public void runtimeException(String message, Object... params) {
        throw new RuntimeException(convertMessage(message, params));
    }
    public void flsCredentialException(String message, Object... params) {
        throw new FlsCredentialException(convertMessage(message, params));
    }
    public void flsCallException(String message, Object... params) {
        throw new FlsCallException(convertMessage(message, params));
    }
    public void dataNotModifiedException(String message, Object... params) {
        throw new DataNotModifiedException(convertMessage(message, params));
    }
    public void unsupportedOperationException(String message, Object... params) {
        throw new UnsupportedOperationException(convertMessage(message, params));
    }
    public void taskDemandException(String message, Object... params) {
        throw new TaskDemandException(convertMessage(message, params));
    }
    public void invalidOperationException(String message, Object... params) {
        throw new InvalidOperationException(convertMessage(message, params));
    }
    public void dataNotFoundException(String message, Object... params) {
        throw new DataNotFoundException(convertMessage(message, params));
    }

    public String getLanguageSpecificText(String message,Object... params){
        return convertMessage(message,params);
    }
}
