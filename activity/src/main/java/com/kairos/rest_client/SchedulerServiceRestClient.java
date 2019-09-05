package com.kairos.rest_client;

import com.kairos.commons.utils.ObjectMapperUtils;
import com.kairos.controller.exception_handler.ResponseEnvelope;
import com.kairos.enums.IntegrationOperation;
import com.kairos.service.exception.ExceptionService;
import org.apache.http.NameValuePair;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.conn.HttpHostConnectException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import javax.inject.Inject;
import java.net.ConnectException;
import java.net.URISyntaxException;
import java.util.*;

import static com.kairos.utils.RestClientUrlUtil.getSchedulerBaseUrl;

@Service
public class SchedulerServiceRestClient {
    private static Logger logger = LoggerFactory.getLogger(GenericRestClient.class);

    @Autowired
    RestTemplate restTemplate;
    @Inject private ExceptionService exceptionService;

    public static HttpMethod getHttpMethod(IntegrationOperation integrationOperation) {
        switch (integrationOperation) {
            case CREATE:
                return HttpMethod.POST;
            case DELETE:
                return HttpMethod.DELETE;
            case UPDATE:
                return HttpMethod.PUT;
            case GET:
                return HttpMethod.GET;
            default:return null;

        }
    }


    public <T extends Object, V> V publishRequest(T t, Long id, boolean isUnit, IntegrationOperation integrationOperation, String uri, List<NameValuePair> queryParam, ParameterizedTypeReference<RestTemplateResponseEnvelope<V>> typeReference, Object... pathParams) {
        final String baseUrl = getSchedulerBaseUrl(isUnit,id)+uri;
        String url = baseUrl+getURIWithParam(queryParam).replace("%2C+",",");
        V responseData = null;
        try {
            ResponseEntity<RestTemplateResponseEnvelope<V>> restExchange =
                    restTemplate.exchange(
                            url,
                            getHttpMethod(integrationOperation),
                            new HttpEntity<>(t), typeReference,pathParams);
            RestTemplateResponseEnvelope<V> response = restExchange.getBody();
            if (!restExchange.getStatusCode().is2xxSuccessful()) {
                exceptionService.internalError(response.getMessage());
            }
            responseData =  response.getData();
        } catch (Exception exception) {
            if(exception instanceof HttpClientErrorException){
                HttpClientErrorException httpClientErrorException = (HttpClientErrorException)exception;
                logger.info("status {}", httpClientErrorException.getStatusCode());
                logger.info("response {}", httpClientErrorException.getResponseBodyAsString());
                exceptionService.exceptionWithoutConvertInRestClient(ObjectMapperUtils.jsonStringToObject(httpClientErrorException.getResponseBodyAsString(),ResponseEnvelope.class).getMessage());
            }
        }
        return responseData;
    }


    public String getURIWithParam(List<NameValuePair> queryParam){
        try {
            URIBuilder builder = new URIBuilder();
            if(queryParam!=null && !queryParam.isEmpty()) {
                builder.setParameters(queryParam);
            }
            return builder.build().toString();
        } catch (URISyntaxException e) {
            exceptionService.internalError(e.getMessage());
        }
        return null;
    }




    public static <T> String getURI(T t,String uri,Map<String,Object> queryParams){
        URIBuilder builder = new URIBuilder();

        if(Optional.ofNullable(queryParams).isPresent()){
            queryParams.entrySet().forEach(e->{
                builder.addParameter(e.getKey(),e.getValue().toString());
            });
        }
        try {
            uri= uri+builder.build().toString();
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
        return uri;
    }
}
