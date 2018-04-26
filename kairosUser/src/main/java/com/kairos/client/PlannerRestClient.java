package com.kairos.client;

import com.kairos.client.dto.RestTemplateResponseEnvelope;
import com.kairos.persistence.model.enums.CitizenHealthStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import static com.kairos.client.RestClientURLUtil.getBaseUrl;

@Component
public class PlannerRestClient {
    private static final Logger logger = LoggerFactory.getLogger(PlannerRestClient.class);

    @Autowired
    RestTemplate restTemplate;

    /**
     * @auther anil maurya
     * map in planner controller
     * @param citizenId
     * @return
     */
    public boolean deleteTaskForCitizen(Long citizenId, CitizenHealthStatus citizenHealthStatus,String date){

        final String baseUrl=getBaseUrl(true);

        try {
            ParameterizedTypeReference<RestTemplateResponseEnvelope<Boolean>> typeReference = new ParameterizedTypeReference<RestTemplateResponseEnvelope<Boolean>>() {};
            HttpEntity<CitizenHealthStatus> entity = new HttpEntity<>(citizenHealthStatus);
            ResponseEntity<RestTemplateResponseEnvelope<Boolean>> restExchange =
                    restTemplate.exchange(
                            baseUrl + "/planner/citizen/{citizenId}/tasks?date=" + date,
                            HttpMethod.DELETE,
                            entity, typeReference, citizenId);

            RestTemplateResponseEnvelope<Boolean> response = restExchange.getBody();
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



}
