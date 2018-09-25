package com.kairos.rest_client;

import com.kairos.commons.client.RestTemplateResponseEnvelope;
import com.kairos.dto.activity.planned_time_type.PresenceTypeDTO;
import com.kairos.service.exception.ExceptionService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import javax.inject.Inject;
import java.util.List;

import static com.kairos.rest_client.RestClientURLUtil.getBaseUrl;
import static com.kairos.constants.ApiConstants.COUNTRY_URL;

@Component
public class PlannedTimeTypeRestClient {
    private Logger logger = LoggerFactory.getLogger(PlannedTimeTypeRestClient.class);
    @Inject
    private RestTemplate restTemplate;
    @Inject
    private ExceptionService exceptionService;

    public List<PresenceTypeDTO> getAllPlannedTimeTypes(Long countryId){
        String baseUrl = getBaseUrl(false)+COUNTRY_URL+"/plannedTimeType";
        try {
            ParameterizedTypeReference<RestTemplateResponseEnvelope<List<PresenceTypeDTO>>> typeReference =
                    new ParameterizedTypeReference<RestTemplateResponseEnvelope<List<PresenceTypeDTO>>>() {
                    };
            ResponseEntity<RestTemplateResponseEnvelope<List<PresenceTypeDTO>>> restExchange =
                    restTemplate.exchange(
                            baseUrl,
                            HttpMethod.GET,
                            null, typeReference,countryId);

            RestTemplateResponseEnvelope<List<PresenceTypeDTO>> response = restExchange.getBody();
            if (restExchange.getStatusCode().is2xxSuccessful()) {
                logger.info("RestExchange", restExchange);
                return response.getData();
            } else {
                throw new RuntimeException(response.getMessage());
            }
        } catch (HttpClientErrorException e) {

            logger.info("status {}", e.getStatusCode());
            logger.info("response {}", e.getResponseBodyAsString());
            exceptionService.runtimeException("message.timeType.exceptionOccurred",e.getMessage());

        }

        return null;
    }
}
