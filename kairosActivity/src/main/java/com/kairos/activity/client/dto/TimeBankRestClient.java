package com.kairos.activity.client.dto;

import com.kairos.activity.response.dto.time_bank.TimebankWrapper;
import com.kairos.activity.util.userContext.UserContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

/*
* Created By Pradeep singh rajawat
*  Date-27/01/2018
*
* */

@Service
public class TimeBankRestClient {

    private static final Logger logger = LoggerFactory.getLogger(TimeBankRestClient.class);

    @Autowired
    private RestTemplate restTemplate;

    public TimebankWrapper getCTAbyUnitEmployementPosition(Long unitPositionId) {
        String baseUrl=new StringBuilder("http://zuulservice/kairos/user/api/v1/organization/").append(UserContext.getOrgId()).append("/unit/").append(UserContext.getUnitId()).toString();
        try {
            ParameterizedTypeReference<RestTemplateResponseEnvelope<TimebankWrapper>> typeReference = new ParameterizedTypeReference<RestTemplateResponseEnvelope<TimebankWrapper>>(){};
            ResponseEntity<RestTemplateResponseEnvelope<TimebankWrapper>> restExchange =
                    restTemplate.exchange(
                            baseUrl + "/getCTAbyUnitPosition/{unitPositionId}",
                            HttpMethod.GET,
                            null, typeReference,unitPositionId);
            RestTemplateResponseEnvelope<TimebankWrapper> response = restExchange.getBody();
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

    /*public List<TimebankWrapper> getCTAbyUnitEmployementPosition() {
        try {
            ParameterizedTypeReference<RestTemplateResponseEnvelope<List<TimebankWrapper>>> typeReference = new ParameterizedTypeReference<RestTemplateResponseEnvelope<List<TimebankWrapper>>>(){};
            ResponseEntity<RestTemplateResponseEnvelope<List<TimebankWrapper>>> restExchange =
                    restTemplate.exchange(
                            "" + "/getAllCTASWithUnitEmployementPositionIds",
                            HttpMethod.GET,
                            null, typeReference);
            RestTemplateResponseEnvelope<List<TimebankWrapper>> response = restExchange.getBody();
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
    }*/


}
