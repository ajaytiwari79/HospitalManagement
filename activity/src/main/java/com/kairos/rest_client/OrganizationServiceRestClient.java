package com.kairos.rest_client;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

import static com.kairos.utils.RestClientUrlUtil.getBaseUrl;

@Component
@Deprecated
public class OrganizationServiceRestClient {
    private static final Logger logger = LoggerFactory.getLogger(OrganizationServiceRestClient.class);

    @Autowired
    RestTemplate restTemplate;


    public Map<String, Object> getOrganizationServices(Long unitId, String organizationType){

        final String baseUrl=getBaseUrl(true);

        try {
            ParameterizedTypeReference<RestTemplateResponseEnvelope<Map<String,Object>>> typeReference = new ParameterizedTypeReference<RestTemplateResponseEnvelope<Map<String,Object>>>() {};
            ResponseEntity<RestTemplateResponseEnvelope<Map<String,Object>>> restExchange =
                    restTemplate.exchange(
                            baseUrl + "/service/data?type="+organizationType,
                            HttpMethod.GET,
                            null, typeReference, organizationType);
            RestTemplateResponseEnvelope<Map<String,Object>> response = restExchange.getBody();
            if (restExchange.getStatusCode().is2xxSuccessful()) {
                Map<String, Object> organizationServices = response.getData();
                logger.info("organizationServices >>> "+organizationServices);
                return organizationServices;
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
