package com.kairos.activity.client;

import com.kairos.activity.client.dto.RestTemplateResponseEnvelope;
import com.kairos.activity.response.dto.TimeCareOrganizationDTO;
import com.kairos.activity.util.timeCareShift.GetWorkShiftsFromWorkPlaceByIdResult;
import com.kairos.activity.util.userContext.UserContext;
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
@Component
public class TimeCareRestClient {
    private static final Logger logger = LoggerFactory.getLogger(CountryRestClient.class);

    @Autowired
    RestTemplate restTemplate;


    public TimeCareOrganizationDTO getPrerequisitesForTimeCareTask(GetWorkShiftsFromWorkPlaceByIdResult workShift){
        final String baseUrl=getBaseUrl(false);

        try {
            HttpEntity<GetWorkShiftsFromWorkPlaceByIdResult> entity = new HttpEntity<>(workShift);
            ParameterizedTypeReference<RestTemplateResponseEnvelope<TimeCareOrganizationDTO>> typeReference = new ParameterizedTypeReference<RestTemplateResponseEnvelope<TimeCareOrganizationDTO>>() {};
            ResponseEntity<RestTemplateResponseEnvelope<TimeCareOrganizationDTO>> restExchange =
                    restTemplate.exchange(
                            baseUrl + "/timecare_task/prerequisites",
                            HttpMethod.GET,
                            entity, typeReference);
            RestTemplateResponseEnvelope<TimeCareOrganizationDTO> response = restExchange.getBody();
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

    private final String getBaseUrl(boolean hasUnitInUrl) {
        if (hasUnitInUrl) {
            String baseUrl = new StringBuilder("http://zuulservice/kairos/activity/api/v1/organization/").append(UserContext.getOrgId()).append("/unit/").append(UserContext.getUnitId()).toString();
            return baseUrl;
        } else {
            String baseUrl = new StringBuilder("http://zuulservice/kairos/activity/api/v1/organization/").append(UserContext.getOrgId()).toString();
            return baseUrl;
        }

    }

}
