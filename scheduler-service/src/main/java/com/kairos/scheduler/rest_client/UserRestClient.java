package com.kairos.scheduler.rest_client;

import com.kairos.commons.client.RestTemplateResponseEnvelope;
import com.kairos.commons.service.TokenAuthService;
import com.kairos.enums.IntegrationOperation;
import com.kairos.scheduler.config.EnvConfig;
import com.kairos.scheduler.service.exception.ExceptionService;
import org.apache.http.NameValuePair;
import org.apache.http.client.utils.URIBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import javax.inject.Inject;
import java.net.URISyntaxException;
import java.util.List;

import static com.kairos.scheduler.rest_client.RestClientUrlUtil.getBaseUrl;


@Service
public class UserRestClient {
    private static Logger logger = LoggerFactory.getLogger(UserRestClient.class);

    @Inject
    @Qualifier("restTemplate")
    private RestTemplate restTemplate;
    @Inject
    @Qualifier("restTemplateWithoutAuth")
    private RestTemplate schedulerServiceRestTemplate;

    @Inject
    private TokenAuthService tokenAuthService;
    @Inject
    private ExceptionService exceptionService;
    @Inject
    private EnvConfig env ;


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


    public <T extends Object, V> V publishRequest(T t, Long id, boolean isUnit, IntegrationOperation integrationOperation, String uri, List<NameValuePair> queryParam, ParameterizedTypeReference<RestTemplateResponseEnvelope<V>> typeReference, boolean withoutAuth, Object... pathParams) {
        final String baseUrl = getBaseUrl(isUnit,id,env.getUserServiceUrl())+uri;
        String url = baseUrl+getURIWithParam(queryParam).replace("%2C+",",");
        try {
            ResponseEntity<RestTemplateResponseEnvelope<V>> restExchange;
            if(withoutAuth) {
                HttpHeaders headers = new HttpHeaders();
                headers.add("Authorization","bearer "+ tokenAuthService.getAuthToken());
                HttpEntity<T> httpEntity= new HttpEntity<T>(t,headers);
                restExchange = schedulerServiceRestTemplate.exchange(
                        url,
                        getHttpMethod(integrationOperation),
                        httpEntity, typeReference,pathParams);
                if(restExchange.getStatusCode().value()==401) {
                    tokenAuthService.getNewAuthToken();
                headers.remove("Authorization");
                headers.add("Authorization","bearer "+ tokenAuthService.getNewAuthToken());
                    httpEntity= new HttpEntity<T>(t,headers);
                    restExchange = schedulerServiceRestTemplate.exchange(
                            url,
                            getHttpMethod(integrationOperation),
                            httpEntity, typeReference,pathParams);
                }

            }
            else {
                 restExchange = restTemplate.exchange(
                        url,
                        getHttpMethod(integrationOperation),
                        new HttpEntity<>(t), typeReference, pathParams);
            }
            RestTemplateResponseEnvelope<V> response = restExchange.getBody();
            if (!restExchange.getStatusCode().is2xxSuccessful()) {
                logger.error("not valid code"+restExchange.getStatusCode());
                exceptionService.internalError(response.getMessage());
            }
            return response.getData();
        } catch (HttpClientErrorException e) {
            logger.info("status {}", e.getStatusCode());
            logger.info("response {}", e.getResponseBodyAsString());
            throw new RuntimeException("exception occurred in activity micro service " + e.getMessage());
       }

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
}
