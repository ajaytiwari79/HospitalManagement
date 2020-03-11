package com.kairos.rest_client;

import com.kairos.commons.client.RestTemplateResponseEnvelope;
import com.kairos.commons.service.TokenAuthService;
import com.kairos.config.env.EnvConfig;
import com.kairos.enums.rest_client.MicroService;
import com.kairos.enums.rest_client.RestClientUrlType;
import com.kairos.service.exception.ExceptionService;
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

import static com.kairos.service.shift.ShiftValidatorService.throwException;
import static com.kairos.utils.RestClientUrlUtil.getSchedulerBaseUrl;
import static com.kairos.utils.RestClientUrlUtil.getUserServiceBaseUrl;


@Service
public class UserRestClientForScheduler {
    public static final String AUTHORIZATION = "Authorization";
    private static Logger logger = LoggerFactory.getLogger(UserRestClientForScheduler.class);


    @Inject
    @Qualifier("restTemplateWithoutAuth")
    private RestTemplate schedulerServiceRestTemplate;

    @Inject
    private TokenAuthService tokenAuthService;
    @Inject
    private ExceptionService exceptionService;
    @Inject
    private EnvConfig env ;



    public <T extends Object, V> V publishRequest(T t, Long id, RestClientUrlType restClientUrlType, HttpMethod httpMethod,MicroService microService, String uri, List<NameValuePair> queryParam, ParameterizedTypeReference<RestTemplateResponseEnvelope<V>> typeReference, Object... pathParams) {
        String baseUrl = getBaseUrl(id, restClientUrlType, microService, uri);
        // organizationId
        String url = baseUrl+getURIWithParam(queryParam).replace("%2C+",",");
        try {
            ResponseEntity<RestTemplateResponseEnvelope<V>> restExchange;
                HttpHeaders headers = new HttpHeaders();
                headers.add(AUTHORIZATION,"bearer "+ tokenAuthService.getAuthToken());
                HttpEntity<T> httpEntity= new HttpEntity<>(t,headers);
                restExchange = schedulerServiceRestTemplate.exchange(
                        url,
                        httpMethod,
                        httpEntity, typeReference,pathParams);
            restExchange = getRestTemplateResponseEnvelopeResponseEntity(t, httpMethod, typeReference, url, restExchange, headers, pathParams);
            RestTemplateResponseEnvelope<V> response = restExchange.getBody();
            if (!restExchange.getStatusCode().is2xxSuccessful()) {
                logger.error("not valid code {}",restExchange.getStatusCode());
                exceptionService.internalError(response.getMessage());
            }
            return response.getData();
        } catch (HttpClientErrorException e) {
            logger.info("status {}", e.getStatusCode());
            logger.info("response {}", e.getResponseBodyAsString());
            throwException("exception occurred in activity micro service " + e.getMessage());
        }
        return null;
    }

    private <T extends Object, V> ResponseEntity<RestTemplateResponseEnvelope<V>> getRestTemplateResponseEnvelopeResponseEntity(T t, HttpMethod httpMethod, ParameterizedTypeReference<RestTemplateResponseEnvelope<V>> typeReference, String url, ResponseEntity<RestTemplateResponseEnvelope<V>> restExchange, HttpHeaders headers, Object[] pathParams) {
        HttpEntity<T> httpEntity;
        if(restExchange.getStatusCode().value()==401) {
            tokenAuthService.getNewAuthToken();
            headers.remove(AUTHORIZATION);
            headers.add(AUTHORIZATION,"bearer "+ tokenAuthService.getNewAuthToken());
            httpEntity= new HttpEntity<>(t,headers);
            restExchange = schedulerServiceRestTemplate.exchange(
                    url,
                    httpMethod,
                    httpEntity, typeReference,pathParams);
        }
        return restExchange;
    }

    private String getBaseUrl(Long id, RestClientUrlType restClientUrlType, MicroService microService, String uri) {
        String baseUrl;
        switch (microService) {
            case USER:
                 baseUrl = getUserServiceBaseUrl(restClientUrlType, id,id) + uri;// make same as its necessary for URL We have already a task to remove the organization
                break;
            case SCHEDULER:
                baseUrl=getSchedulerBaseUrl(true,id)+uri;
                break;
            default: throw new UnsupportedOperationException("Invalid method specified");
        }
        return baseUrl;
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
