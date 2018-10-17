package com.kairos.rest_client;

import com.kairos.dto.user.country.time_slot.TimeSlotWrapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Map;

import static com.kairos.utils.RestClientUrlUtil.getBaseUrl;

@Component
@Deprecated
public class TimeSlotRestClient {
    private static final Logger logger = LoggerFactory.getLogger(TimeSlotRestClient.class);

    @Autowired
    RestTemplate restTemplate;

    public Map<String, Object> getTimeSlotByUnitIdAndTimeSlotId(Long unitId, Long timeSlotId) {
        //Map<String, Object> timeSlotMap = timeSlotGraphRepository.getTimeSlotByUnitIdAndTimeSlotId(unitId,timeSlotId);
        final String baseUrl=getBaseUrl(true);
        try {
            ParameterizedTypeReference<RestTemplateResponseEnvelope<Map<String,Object>>> typeReference = new ParameterizedTypeReference<RestTemplateResponseEnvelope< Map<String,Object>>>() {
            };
            ResponseEntity<RestTemplateResponseEnvelope<Map<String,Object>>> restExchange =
                    restTemplate.exchange(
                            baseUrl + "/time_slot/{timeSlotId}",
                            HttpMethod.GET,
                            null, typeReference,timeSlotId);

            RestTemplateResponseEnvelope<Map<String,Object>> response  = restExchange.getBody();
            if (restExchange.getStatusCode().is2xxSuccessful()) {
                return response.getData();
            } else {
                throw new RuntimeException(response.getMessage());
            }
        }catch (HttpClientErrorException e){
            logger.info("status {}", e.getStatusCode());
            logger.info("response {}", e.getResponseBodyAsString());
            throw new RuntimeException("exception occurred in user micro service " + e.getMessage());
        }
    }


    public List<TimeSlotWrapper> getCurrentTimeSlot(long unitId) {
        final String baseUrl = getBaseUrl(true);
        try {
            ParameterizedTypeReference<RestTemplateResponseEnvelope<List<TimeSlotWrapper>>> typeReference = new ParameterizedTypeReference<RestTemplateResponseEnvelope< List<TimeSlotWrapper>>>() {
            };
            ResponseEntity<RestTemplateResponseEnvelope<List<TimeSlotWrapper>>> restExchange =
                    restTemplate.exchange(
                            baseUrl + "/current/time_slots",
                            HttpMethod.GET,
                            null, typeReference,unitId);

            RestTemplateResponseEnvelope<List<TimeSlotWrapper>> response  = restExchange.getBody();
            if (restExchange.getStatusCode().is2xxSuccessful()) {
                return response.getData();
            } else {
                throw new RuntimeException(response.getMessage());
            }
        }catch (HttpClientErrorException e){
            logger.info("status {}", e.getStatusCode());
            logger.info("response {}", e.getResponseBodyAsString());
            throw new RuntimeException("exception occurred in user micro service " + e.getMessage());
        }
    }
}
