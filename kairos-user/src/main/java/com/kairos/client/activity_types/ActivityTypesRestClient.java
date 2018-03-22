package com.kairos.client.activity_types;

import com.kairos.client.dto.RestTemplateResponseEnvelope;
import com.kairos.response.dto.web.cta.ActivityCategory;
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

import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class ActivityTypesRestClient {
    private static final Logger logger = LoggerFactory.getLogger(ActivityTypesRestClient.class);

    @Autowired
    RestTemplate restTemplate;

    /**
     * @Auther anil maurya
     * @returnActivityTypeDTOActivityTypeDTO
     */
    public List<ActivityTypeDTO> getActivitiesForCountry (Long countryId){

        final String baseUrl=getBaseUrl(false);
        try {
            ParameterizedTypeReference<RestTemplateResponseEnvelope<List<ActivityTypeDTO>>> typeReference =
                    new ParameterizedTypeReference<RestTemplateResponseEnvelope<List<ActivityTypeDTO>>>() {};
            ResponseEntity<RestTemplateResponseEnvelope<List<ActivityTypeDTO>>> restExchange =
                    restTemplate.exchange(
                            baseUrl + "/country/{countryId}/activity/cta_wta_setting",
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

    public List<ActivityCategory> getActivityCategoriesForCountry (Long countryId){

        final String baseUrl=getBaseUrl(false);
        try {
            ParameterizedTypeReference<RestTemplateResponseEnvelope<List<ActivityCategory>>> typeReference =
                    new ParameterizedTypeReference<RestTemplateResponseEnvelope<List<ActivityCategory>>>() {};
            ResponseEntity<RestTemplateResponseEnvelope<List<ActivityCategory>>> restExchange =
                    restTemplate.exchange(
                            baseUrl + "/country/{countryId}/activity/activity_categories",
                            HttpMethod.GET,
                            null, typeReference,countryId);

            RestTemplateResponseEnvelope<List<ActivityCategory>> response = restExchange.getBody();
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


    public List<ActivityTypeDTO> getActivitiesForUnit (Long unitId){

        final String baseUrl=getBaseUrl(true);
        try {
            ParameterizedTypeReference<RestTemplateResponseEnvelope<List<ActivityTypeDTO>>> typeReference =
                    new ParameterizedTypeReference<RestTemplateResponseEnvelope<List<ActivityTypeDTO>>>() {};
            ResponseEntity<RestTemplateResponseEnvelope<List<ActivityTypeDTO>>> restExchange =
                    restTemplate.exchange(
                            baseUrl + "/activity/cta_wta_setting",
                            HttpMethod.GET,
                            null, typeReference);

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

    public HashMap<Long,HashMap<Long,Long>> getActivityIdsForUnitsByParentActivityId (Long countryId, List<Long> unitIds, List<Long> parentActivityIds){

        final String baseUrl=getBaseUrl(false);
        final String unitIdsQueryString = unitIds.stream().map(Object::toString)
                .collect(Collectors.joining(", "));
        final String activityIdsQueryString = parentActivityIds.stream().map(Object::toString)
                .collect(Collectors.joining(", "));
        try {
            ParameterizedTypeReference<RestTemplateResponseEnvelope<HashMap<Long,HashMap<Long,Long>>>> typeReference =
                    new ParameterizedTypeReference<RestTemplateResponseEnvelope<HashMap<Long,HashMap<Long,Long>>>>() {};
            ResponseEntity<RestTemplateResponseEnvelope<HashMap<Long,HashMap<Long,Long>>>> restExchange =
                    restTemplate.exchange(
                            baseUrl + "/country/{countryId}/unit_activity?parentActivityIds="+activityIdsQueryString+"&unitIds="+unitIdsQueryString,
                            HttpMethod.GET,
                            null, typeReference,countryId);

            RestTemplateResponseEnvelope<HashMap<Long,HashMap<Long,Long>>> response = restExchange.getBody();
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
