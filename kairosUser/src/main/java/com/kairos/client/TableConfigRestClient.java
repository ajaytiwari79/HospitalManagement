package com.kairos.client;

import com.kairos.client.dto.RestTemplateResponseEnvelope;
import com.kairos.client.dto.TableConfiguration;
import com.kairos.util.userContext.UserContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

@Component
public class TableConfigRestClient {
    @Autowired
    RestTemplate restTemplate;



    private static final Logger logger = LoggerFactory.getLogger(TableConfigRestClient.class);

    /**
     * @auther anil maurya
     *
     * @param staffId
     * @param organizationId
     * @param unitId
     * @return
     */
    public TableConfiguration getTableConfiguration(long organizationId, long unitId, Long staffId){

        final String baseUrl=getBaseUrl();

        try {
            ParameterizedTypeReference<RestTemplateResponseEnvelope<TableConfiguration>> typeReference = new ParameterizedTypeReference<RestTemplateResponseEnvelope<TableConfiguration>>(){};
            ResponseEntity<RestTemplateResponseEnvelope<TableConfiguration>> restExchange =
                    restTemplate.exchange(
                            baseUrl + "/table/{staffId}",
                            HttpMethod.
                                    GET,null, typeReference,staffId);

            RestTemplateResponseEnvelope<TableConfiguration> response = restExchange.getBody();
            if (restExchange.getStatusCode().is2xxSuccessful()) {
                return response.getData();
            } else {
                throw new RuntimeException(response.getMessage());
            }
        }catch (HttpClientErrorException e) {

            logger.info("status {}",e.getStatusCode());
            logger.info("response {}",e.getResponseBodyAsString());
            throw new RuntimeException("exception occurred in task micro service "+e.getMessage());
        }
    }

    private final String getBaseUrl(){
        String baseUrl=new StringBuilder("http://zuulservice/kairos/activity/api/v1/organization/").append(UserContext.getOrgId()).append("/unit/").append(UserContext.getUnitId()).toString();
        return baseUrl;
    }
}
