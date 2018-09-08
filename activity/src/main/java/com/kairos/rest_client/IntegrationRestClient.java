package com.kairos.rest_client;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentMap;

import static com.kairos.utils.RestClientUrlUtil.getBaseUrl;

@Component
public class IntegrationRestClient {

    private static final Logger logger = LoggerFactory.getLogger(IntegrationRestClient.class);


    @Autowired
    RestTemplate restTemplate;

    /**
     * @auther anil maurya
     *
     * endpoint map on IntegrationController in user micro service
     *
     * @param citizenUnitId
     * @return
     */
    public Map<String, String> getFLS_Credentials(Long citizenUnitId){

        final String baseUrl=getBaseUrl(true);

        try {
            ParameterizedTypeReference<RestTemplateResponseEnvelope<Map<String,String>>> typeReference = new ParameterizedTypeReference<RestTemplateResponseEnvelope<Map<String,String>>>() {};
            ResponseEntity<RestTemplateResponseEnvelope<Map<String,String>>> restExchange =
                    restTemplate.exchange(
                            baseUrl + "/integration/unit/{citizenUnitId}/flsCred",
                            HttpMethod.GET,
                            null, typeReference, citizenUnitId);
            RestTemplateResponseEnvelope<Map<String,String>> response = restExchange.getBody();
            if (restExchange.getStatusCode().is2xxSuccessful()) {
                return response.getData();
            } else {
                throw new RuntimeException(response.getMessage());
            }
        }catch (HttpClientErrorException e) {
            logger.info("status {}",e.getStatusCode());
            logger.info("response {}",e.getResponseBodyAsString());
            throw new RuntimeException("exception occurred in user micro service "+e.getMessage());
        }


    }

    public ConcurrentMap<Long,ConcurrentMap<String,String>> getFLSCredentials(List<Long> credentialsForUnitIds){

        final String baseUrl=getBaseUrl(true);

        try {
            HttpEntity<Long> entity = new HttpEntity(credentialsForUnitIds);
            ParameterizedTypeReference<RestTemplateResponseEnvelope<ConcurrentMap<Long,ConcurrentMap<String,String>>>> typeReference = new ParameterizedTypeReference<RestTemplateResponseEnvelope<ConcurrentMap<Long,ConcurrentMap<String,String>>>>() {};
            ResponseEntity<RestTemplateResponseEnvelope<ConcurrentMap<Long,ConcurrentMap<String,String>>>> restExchange =
                    restTemplate.exchange(
                            baseUrl + "/integration/units/flsCred",
                            HttpMethod.POST,
                            entity, typeReference);
            RestTemplateResponseEnvelope<ConcurrentMap<Long,ConcurrentMap<String,String>>> response = restExchange.getBody();
            if (restExchange.getStatusCode().is2xxSuccessful()) {
                return response.getData();
            } else {
                throw new RuntimeException(response.getMessage());
            }
        }catch (HttpClientErrorException e) {
            logger.info("status {}",e.getStatusCode());
            logger.info("response {}",e.getResponseBodyAsString());
            throw new RuntimeException("exception occurred in user micro service "+e.getMessage());
        }


    }

}
