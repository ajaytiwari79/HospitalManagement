package com.kairos.client.activity_types;

import com.kairos.client.dto.RestTemplateResponseEnvelope;
import com.kairos.response.dto.web.cta.ActivityTypeDTO;
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

import java.util.List;
@Component
public class ActivityTypesRestClient {
    private static final Logger logger = LoggerFactory.getLogger(ActivityTypesRestClient.class);

    @Autowired
    RestTemplate restTemplate;

    /**
     * @Auther anil maurya
     * @returnActivityTypeDTOActivityTypeDTO
     */
    public List<ActivityTypeDTO> getActivityType (Long countryId){

        final String baseUrl=getBaseUrl(false);
        try {
            ParameterizedTypeReference<RestTemplateResponseEnvelope<List<ActivityTypeDTO>>> typeReference =
                    new ParameterizedTypeReference<RestTemplateResponseEnvelope<List<ActivityTypeDTO>>>() {};
            ResponseEntity<RestTemplateResponseEnvelope<List<ActivityTypeDTO>>> restExchange =
                    restTemplate.exchange(
                            baseUrl + "/country/{countryId}/activity",
                            HttpMethod.GET,
                            null, typeReference,countryId);

            RestTemplateResponseEnvelope<List<ActivityTypeDTO>> response = restExchange.getBody();
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

    private final String getBaseUrl(boolean hasUnitInUrl){
        if(hasUnitInUrl){
            String baseUrl=new StringBuilder("http://zuulservice/kairos/activity/api/v1/organization/").append(UserContext.getOrgId()).append("/unit/").append(UserContext.getUnitId()).toString();
            return baseUrl;
        }else{
            String baseUrl=new StringBuilder("http://zuulservice/kairos/activity/api/v1/organization/").append(UserContext.getOrgId()).toString();
            return baseUrl;
        }

    }
}
