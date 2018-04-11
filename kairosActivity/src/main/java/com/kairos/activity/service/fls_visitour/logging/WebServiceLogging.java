package com.kairos.activity.service.fls_visitour.logging;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ws.client.WebServiceClientException;
import org.springframework.ws.client.support.interceptor.ClientInterceptor;
import org.springframework.ws.context.MessageContext;

/**
 * Created by Rama.Shankar on 27/9/16.
 */
public class WebServiceLogging implements ClientInterceptor {

    private static final Logger logger = LoggerFactory.getLogger(WebServiceLogging.class);

    @Override
    public boolean handleRequest(MessageContext messageContext) throws WebServiceClientException {
        logger.debug("**********haddle requeste [" + messageContext.getResponse() + "] for request [" +
                messageContext.getRequest() + "]");
        return true;
    }

    @Override
    public boolean handleResponse(MessageContext messageContext) throws WebServiceClientException {
        logger.debug("**********Received response by sample [" + messageContext.getResponse() + "] for request [" +
                messageContext.getRequest() + "]");
        return true;
    }

    @Override
    public boolean handleFault(MessageContext messageContext) throws WebServiceClientException {
        return true;
    }

    @Override
    public void afterCompletion(MessageContext messageContext, Exception e) throws WebServiceClientException {

    }
}
