package com.kairos.rest_client;

import com.kairos.commons.utils.ObjectMapperUtils;
import com.kairos.controller.exception_handler.ResponseEnvelope;
import com.kairos.enums.IntegrationOperation;
import com.kairos.enums.rest_client.RestClientUrlType;
import com.kairos.service.exception.ExceptionService;
import org.apache.commons.collections.CollectionUtils;
import org.apache.http.NameValuePair;
import org.apache.http.client.utils.URIBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import javax.inject.Inject;
import java.net.URISyntaxException;
import java.util.*;

import static com.kairos.utils.RestClientUrlUtil.getUserServiceBaseUrl;


@Service
public class GenericRestClient {
    private static Logger logger = LoggerFactory.getLogger(GenericRestClient.class);

    @Inject
    RestTemplate restTemplate;
    @Inject
    private ExceptionService exceptionService;
    @Autowired
    @Qualifier("restTemplateWithoutAuth")
    private RestTemplate schedulerRestTemplate;

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
    public <T extends Object, V> V publishRequest(T t, Long id, RestClientUrlType restClientUrlType, HttpMethod httpMethod, String uri, List<NameValuePair> queryParam, ParameterizedTypeReference<RestTemplateResponseEnvelope<V>> typeReference, Object... pathParams) {
        final String baseUrl = getUserServiceBaseUrl(restClientUrlType, id) + uri;
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
                exceptionService.internalError(response.getMessage());
            }
            responseData = response.getData();
        } catch (HttpClientErrorException e) {
            logger.info("status {}", e.getStatusCode());
            logger.info("response {}", e.getResponseBodyAsString());
            exceptionService.exceptionWithoutConvertInRestClient(ObjectMapperUtils.jsonStringToObject(e.getResponseBodyAsString(),ResponseEnvelope.class).getMessage());
        }
        return responseData;
    }

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
    public <T extends Object, V> V publishRequestWithoutAuth(T t, Long id, RestClientUrlType restClientUrlType, HttpMethod httpMethod, String uri, List<NameValuePair> queryParam, ParameterizedTypeReference<RestTemplateResponseEnvelope<V>> typeReference, Object... pathParams) {
        final String baseUrl = getUserServiceBaseUrl(restClientUrlType, id) + uri;
        String url = baseUrl + getURIWithParam(queryParam);
        try {
            ResponseEntity<RestTemplateResponseEnvelope<V>> restExchange =
                    schedulerRestTemplate.exchange(
                            url,
                            httpMethod,
                            new HttpEntity<>(t), typeReference, pathParams);
            RestTemplateResponseEnvelope<V> response = restExchange.getBody();
            if (!restExchange.getStatusCode().is2xxSuccessful()) {
                exceptionService.internalError(response.getMessage());
            }
            return response.getData();
        } catch (HttpClientErrorException e) {
            logger.info("status {}", e.getStatusCode());
            logger.info("response {}", e.getResponseBodyAsString());
            throw new RuntimeException("exception occurred in User micro service " + e.getMessage());
        }

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


    public static <T> String getURI(T t, String uri, Map<String, Object> queryParams) {
        URIBuilder builder = new URIBuilder();

        if (Optional.ofNullable(queryParams).isPresent()) {
            queryParams.entrySet().forEach(e -> {
                builder.addParameter(e.getKey(), e.getValue().toString());
            });
        }
        try {
            uri = uri + builder.build().toString();
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
        return uri;
    }

    //TODO Remove
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
            default:
                return null;
        }
    }
}