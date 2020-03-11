package com.planner.service.rest_client;

import com.kairos.commons.custom_exception.InvalidRequestException;
import com.kairos.enums.IntegrationOperation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;


@Service
public class PlannerRestClient {
    private Logger logger = LoggerFactory.getLogger(PlannerRestClient.class);
    @Autowired
    private RestTemplate restTemplate;
    private static String activityServiceUrl;

    @Value("${gateway.activityservice.url}")
    public  void setActivityServiceUrl(String activityServiceUrl) {
        PlannerRestClient.activityServiceUrl = activityServiceUrl;
    }

    public <T, V> RestTemplateResponseEnvelope<V> publish(T t, Long unitId, IntegrationOperation integrationOperation,Object... pathParams) {
        final String baseUrl = getActivityBaseUrl();

        try {
            String url=baseUrl + unitId +  getURI(pathParams);
            logger.info("calling url:{} with http method:{}",url,integrationOperation);
            ParameterizedTypeReference<RestTemplateResponseEnvelope<V>> typeReference = new ParameterizedTypeReference<RestTemplateResponseEnvelope<V>>() {
            };
            ResponseEntity<RestTemplateResponseEnvelope<V>> restExchange =
                    restTemplate.exchange(
                            url,
                            getHttpMethod(integrationOperation),
                            new HttpEntity<>(t), typeReference);
            RestTemplateResponseEnvelope<V> response = restExchange.getBody();
            if (!restExchange.getStatusCode().is2xxSuccessful()) {
                throw new InvalidRequestException(response.getMessage());
            }
            return response;
        } catch (HttpClientErrorException e) {
            logger.error("status {}", e.getStatusCode());
            logger.error("response {}", e.getResponseBodyAsString());
            throw new InvalidRequestException("exception occurred in activity micro service " + e.getMessage());
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
            default:return null;

        }
    }
    public static String getURI(Object... pathParams){
        return String.format("/planner/vrp_completed/%s",pathParams);
    }

    public static final String getActivityBaseUrl(){
        String baseUrl=new StringBuilder(activityServiceUrl+"organization/24/unit/").toString();
        return baseUrl;

    }

}