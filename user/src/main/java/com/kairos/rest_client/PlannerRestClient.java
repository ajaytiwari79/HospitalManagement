package com.kairos.rest_client;

import com.kairos.commons.client.RestTemplateResponseEnvelope;
import com.kairos.enums.CitizenHealthStatus;
import com.kairos.service.exception.ExceptionService;
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

import javax.inject.Inject;

import static com.kairos.rest_client.RestClientURLUtil.getBaseUrl;

@Component
public class PlannerRestClient {
    private static final Logger logger = LoggerFactory.getLogger(PlannerRestClient.class);

    @Autowired
    RestTemplate restTemplate;
    @Inject
    private ExceptionService exceptionService;

    /**
     * @auther anil maurya
     * map in planner controller
     * @param citizenId
     * @return
     */
    public boolean deleteTaskForCitizen(Long citizenId, CitizenHealthStatus citizenHealthStatus, String date){

        final String baseUrl= RestClientURLUtil.getBaseUrl(true);

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
            exceptionService.runtimeException("message.exception.taskmicroservice",e.getMessage());
           // throw new RuntimeException("exception occurred in task micro service "+e.getMessage());
        }
return false;
    }



}
