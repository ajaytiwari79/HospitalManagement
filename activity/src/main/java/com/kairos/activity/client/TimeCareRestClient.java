package com.kairos.activity.client;

import com.kairos.activity.client.dto.RestTemplateResponseEnvelope;
import com.kairos.user.organization.address.TimeCareOrganizationDTO;
import com.kairos.util.timeCareShift.GetWorkShiftsFromWorkPlaceByIdResult;
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

import static com.kairos.util.RestClientUrlUtil.getBaseUrl;

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

}
