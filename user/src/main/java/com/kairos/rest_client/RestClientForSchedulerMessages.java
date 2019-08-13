package com.kairos.rest_client;

import com.kairos.commons.client.RestTemplateResponseEnvelope;
import com.kairos.commons.service.TokenAuthService;
import com.kairos.enums.IntegrationOperation;
import com.kairos.rest_client.priority_group.GenericRestClient;
import com.kairos.service.exception.ExceptionService;
import org.apache.http.NameValuePair;
import org.apache.http.client.utils.URIBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import javax.inject.Inject;
import java.io.UnsupportedEncodingException;
import java.net.URISyntaxException;
import java.net.URLEncoder;
import java.nio.charset.Charset;
import java.util.*;

import static com.kairos.constants.AppConstants.AUTHORIZATION;
import static com.kairos.rest_client.RestClientURLUtil.getBaseUrl;

@Service
public class RestClientForSchedulerMessages {
    private static Logger logger = LoggerFactory.getLogger(GenericRestClient.class);
    @Inject
    @Qualifier("restTemplateWithoutAuth")
    private RestTemplate restTemplateWithoutAuth;
    @Inject
    private ExceptionService exceptionService;
    @Inject
    private TokenAuthService tokenAuthService;

    public <T, V> V publish(T t, Long id, boolean isUnit, IntegrationOperation integrationOperation, String uri, Map<String,Object> queryParams, Object... pathParams) {
        final String baseUrl = getBaseUrl(isUnit, id)+getURI(t,uri,queryParams,pathParams);

        try {
            ParameterizedTypeReference<RestTemplateResponseEnvelope<V>> typeReference = new ParameterizedTypeReference<RestTemplateResponseEnvelope<V>>() {
            };
            HttpHeaders headers = new HttpHeaders();
            headers.add(AUTHORIZATION,"bearer "+ tokenAuthService.getAuthToken());
            HttpEntity<T> httpEntity= new HttpEntity<T>(t,headers);
            ResponseEntity<RestTemplateResponseEnvelope<V>> restExchange =
                    restTemplateWithoutAuth.exchange(
                            baseUrl,
                            getHttpMethod(integrationOperation),
                            httpEntity, typeReference);
            if(restExchange.getStatusCode().value()==401) {
                tokenAuthService.getNewAuthToken();
                headers.remove(AUTHORIZATION);
                headers.add(AUTHORIZATION, "bearer " + tokenAuthService.getNewAuthToken());
                httpEntity = new HttpEntity<T>(t, headers);
                restExchange = restTemplateWithoutAuth.exchange(
                        baseUrl,
                        getHttpMethod(integrationOperation),
                        httpEntity, typeReference);

            }
            RestTemplateResponseEnvelope<V> response = restExchange.getBody();
            if (!restExchange.getStatusCode().is2xxSuccessful()) {
                throw new RuntimeException(response.getMessage());
            }
            return response.getData();
        } catch (HttpClientErrorException e) {
            logger.info("status {}", e.getStatusCode());
            logger.info("response {}", e.getResponseBodyAsString());
            throw new RuntimeException("exception occurred in activity micro service " + e.getMessage());
        }

    }

    public <T extends Object, V> V publishRequest(T t, Long id, boolean isUnit, IntegrationOperation integrationOperation, String uri, List<NameValuePair> queryParam, ParameterizedTypeReference<RestTemplateResponseEnvelope<V>> typeReference, Object... pathParams) {
        final String baseUrl = getBaseUrl(isUnit,id)+uri;
        String url = baseUrl+getURIWithParam(queryParam).replace("%2C+",",");
        try {
            ResponseEntity<RestTemplateResponseEnvelope<V>> restExchange =
                    restTemplateWithoutAuth.exchange(
                            url,
                            getHttpMethod(integrationOperation),
                            new HttpEntity<>(t), typeReference,pathParams);
            RestTemplateResponseEnvelope<V> response = restExchange.getBody();
            if (!restExchange.getStatusCode().is2xxSuccessful()) {
                exceptionService.internalServerError(response.getMessage());
            }
            return response.getData();
        } catch (HttpClientErrorException e) {
            logger.info("status {}", e.getStatusCode());
            logger.info("response {}", e.getResponseBodyAsString());
            throw new RuntimeException("exception occurred in activity micro service " + e.getMessage());
        }

    }
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
    public static <T> String getURI(T t,String uri,Map<String,Object> queryParams,Object... pathParams){
        URIBuilder builder = new URIBuilder().setCharset(Charset.forName("UTF-8"));


        try {
            if(Optional.ofNullable(queryParams).isPresent()){
                queryParams.entrySet().forEach(e->{
                    try {
                        builder.addParameter(e.getKey(),URLEncoder.encode(e.getValue().toString(),"UTF-8"));
                    } catch (UnsupportedEncodingException e1) {
                        e1.printStackTrace();
                    }
                });
            }
            uri= uri+builder.build().toString();
        } catch (URISyntaxException ex) {
            ex.printStackTrace();
        }
        return uri;
    }

    public String getURIWithParam(List<NameValuePair> queryParam){
        try {
            URIBuilder builder = new URIBuilder();
            if(queryParam!=null && !queryParam.isEmpty()) {
                builder.setParameters(queryParam);
            }
            return builder.build().toString();
        } catch (URISyntaxException e) {
            exceptionService.internalServerError(e.getMessage());
        }
        return null;
    }
}
