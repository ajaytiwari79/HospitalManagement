package com.planner.component.rest_client;

import com.kairos.commons.client.RestTemplateResponseEnvelope;
import com.kairos.commons.utils.ObjectMapperUtils;
import com.kairos.dto.user_context.UserContext;
import com.kairos.enums.rest_client.RestClientUrlType;
import com.planner.component.exception.ExceptionService;
import com.planner.controller.custom_responseEntityExceptionHandler.ResponseEnvelope;
import org.apache.commons.collections.CollectionUtils;
import org.apache.http.NameValuePair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import javax.inject.Inject;
import java.util.List;
import java.util.Optional;



//@Service
public class GenericRestClient {
    private static Logger logger = LoggerFactory.getLogger(GenericRestClient.class);

    public static final String ORGANIZATION = "organization/";
    public static final String UNIT = "/unit/";
    public static final String COUNTRY = "/country/";

    //@Inject
    RestTemplate restTemplate;
    //@Inject
    private ExceptionService exceptionService;

    //@Value("${gateway.userservice.url}")
    private String userServiceUrl;

    //@Value("${gateway.activityservice.url}")
    private String activityServiceUrl;


    /**
     * @param t
     * @param id
     * @param restClientUrlType
     * @param httpMethod
     * @param uri
     * @param queryParam
     * @param typeReference
     * @param pathParams
     * @param <T>
     * @param <V>
     * @return
     * @author mohit
     * @date 12-10-2018
     */
    public <T extends Object, V> V publishRequest(T t, Long id, RestClientUrlType restClientUrlType, HttpMethod httpMethod, String uri, List<NameValuePair> queryParam,boolean isActivityServiceUrl, ParameterizedTypeReference<RestTemplateResponseEnvelope<V>> typeReference, Object... pathParams) {
        final String baseUrl = getServiceBaseUrl(restClientUrlType, id,isActivityServiceUrl) + uri;
        String url = baseUrl +getURIWithParam(queryParam);
        V responseData = null;
        try {
            ResponseEntity<RestTemplateResponseEnvelope<V>> restExchange =
                    restTemplate.exchange(
                            url,
                            httpMethod,
                            new HttpEntity<>(t), typeReference, pathParams);
            RestTemplateResponseEnvelope<V> response = restExchange.getBody();
            if (!restExchange.getStatusCode().is2xxSuccessful()) {
                exceptionService.dataNotFoundByIdException(response.getMessage());
            }
            responseData = response.getData();
        } catch (HttpClientErrorException e) {
            logger.info("status {}", e.getStatusCode());
            logger.info("response {}", e.getResponseBodyAsString());
            throw new RuntimeException(ObjectMapperUtils.jsonStringToObject(e.getResponseBodyAsString(), ResponseEnvelope.class).getMessage());
        }
        return responseData;
    }

    public String getURIWithParam(List<NameValuePair> queryParam) {
        String path="";
            if (CollectionUtils.isNotEmpty(queryParam)) {
                StringBuilder stringBuilder = new StringBuilder("?");
                for (NameValuePair nameValuePair : queryParam) {
                    stringBuilder.append("&").append(nameValuePair.getName()).append("=").append(nameValuePair.getValue().replace("[", "").replace("]", ""));
                }
                path= stringBuilder.toString();//.replace("%2C+","");
            }
        return path;
    }

    public String getServiceBaseUrl(RestClientUrlType restClientUrlType, Long id,boolean isActivityService){
        String baseUrl = null;
        StringBuilder serviceUrl = isActivityService ? new StringBuilder(activityServiceUrl) : new StringBuilder(userServiceUrl);
        switch (restClientUrlType){
            case UNIT:baseUrl = serviceUrl.append(UNIT).append((Optional.ofNullable(id).isPresent() ? id : UserContext.getUnitId())).toString();
                break;
            case COUNTRY:baseUrl = serviceUrl.append(COUNTRY).append(id).toString();
                break;
            case ORGANIZATION:baseUrl = serviceUrl.toString();
                break;
            case COUNTRY_WITHOUT_PARENT_ORG:
                baseUrl = serviceUrl.append(COUNTRY).append(id).toString();
                break;
            case UNIT_WITHOUT_PARENT_ORG:
                baseUrl = serviceUrl.append(UNIT).append((Optional.ofNullable(id).isPresent() ? id : UserContext.getUnitId())).toString();
                break;

        }
        return baseUrl;
    }

}