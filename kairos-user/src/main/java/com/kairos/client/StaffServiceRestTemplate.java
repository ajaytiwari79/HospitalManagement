package com.kairos.client;

import com.kairos.client.dto.RestTemplateResponseEnvelope;
import com.kairos.response.dto.web.ResponseEnvelope;
import com.kairos.response.dto.web.StaffAssignedTasksWrapper;
import com.kairos.util.userContext.UserContext;
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
import org.springframework.web.util.UriComponentsBuilder;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by anil on 8/8/17.
 */
@Component
public class StaffServiceRestTemplate {
    private static final Logger logger = LoggerFactory.getLogger(ClientServiceRestClient.class);
    @Autowired
    RestTemplate restTemplate;


    /**
     *  @auther anil maurya
     *  endpoint map in task controller
     * @param staffId
     * @param anonymousStaffId
     * @return
     */
    public boolean  updateTaskForStaff(Long staffId,Long anonymousStaffId){

        final String baseUrl=getBaseUrl();

        try {
            ParameterizedTypeReference<RestTemplateResponseEnvelope<Boolean>> typeReference = new ParameterizedTypeReference<RestTemplateResponseEnvelope<Boolean>>() {
            };
            ResponseEntity<RestTemplateResponseEnvelope<Boolean>> restExchange =
                    restTemplate.exchange(baseUrl+"/task/staff/{staffId}/{anonymousStaffId}",
                            HttpMethod.
                                    GET,null, typeReference,staffId,anonymousStaffId);

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

    /**
     * @auther anil maurya
     * map this endpoint on task controller
     * @param unitId
     * @param staffId
     * @param date
     * @return
     */
    public List<StaffAssignedTasksWrapper> getAssignedTasksOfStaff(long staffId,String date){


        final String baseUrl=getBaseUrl();

        try {
            ParameterizedTypeReference<RestTemplateResponseEnvelope<List<StaffAssignedTasksWrapper>>> typeReference = new ParameterizedTypeReference<RestTemplateResponseEnvelope<List<StaffAssignedTasksWrapper>>>() {
            };
            String url = baseUrl + "/task/staff/{staffId}/assigned_tasks?date=" + date;
            // URI (URL) parameters
            Map<String, Object> uriParams = new HashMap<>();
            uriParams.put("staffId",staffId);

            UriComponentsBuilder builder = UriComponentsBuilder.fromUriString(url)
                    // Add query parameter
                    .queryParam("date", date);
            ResponseEntity<RestTemplateResponseEnvelope<List<StaffAssignedTasksWrapper>>> restExchange =
                    restTemplate.exchange(url,
                            HttpMethod.
                                    GET,null, typeReference,staffId);

            RestTemplateResponseEnvelope<List<StaffAssignedTasksWrapper>> response = restExchange.getBody();
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


    private final String getBaseUrl(){
        String baseUrl=new StringBuilder("http://zuulservice/activity/api/v1/organization/").append(UserContext.getOrgId()).append("/unit/").append(UserContext.getUnitId()).toString();
        return baseUrl;
    }
}
