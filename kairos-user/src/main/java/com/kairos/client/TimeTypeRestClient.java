package com.kairos.client;

import com.kairos.client.dto.RestTemplateResponseEnvelope;
import com.kairos.client.dto.TimeTypeDTO;
import com.kairos.util.userContext.UserContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.util.List;

@Service
public class TimeTypeRestClient {

    @Autowired private RestTemplate restTemplate;

    private final Logger logger = LoggerFactory.getLogger(TimeTypeRestClient.class);

    public List<TimeTypeDTO> getTaskTypes(Long countryId) {
        try {
            ParameterizedTypeReference<RestTemplateResponseEnvelope<List<TimeTypeDTO>>> typeReference = new ParameterizedTypeReference<RestTemplateResponseEnvelope<List<TimeTypeDTO>>>() {};
            ResponseEntity<RestTemplateResponseEnvelope<List<TimeTypeDTO>>> restExchange =
                    restTemplate.exchange(
                            "http://zuulservice/kairos/activity/api/v1//organization/"+ UserContext.getOrgId()+"/country/{countryId}",
                            HttpMethod.
                                    GET,null, typeReference,countryId);

            RestTemplateResponseEnvelope<List<TimeTypeDTO>> response = restExchange.getBody();
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
