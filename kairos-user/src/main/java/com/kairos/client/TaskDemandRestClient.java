package com.kairos.client;

import com.kairos.client.dto.OrgTaskTypeAggregateResult;
import com.kairos.client.dto.RestTemplateResponseEnvelope;
import com.kairos.client.dto.TaskTypeAggregateResult;
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

import java.util.List;
import java.util.Map;

@Component
public class TaskDemandRestClient {
    private static final Logger logger = LoggerFactory.getLogger(TaskDemandRestClient.class);
    @Autowired
    RestTemplate restTemplate;

    /**
     * @auther anil maurya
     * map in task demand controller
     * @param citizenIds
     * @return
     */
    public List<TaskTypeAggregateResult> getTaskTypesOfCitizens(List<Long>citizenIds) {

        try {
            HttpEntity<List> request = new HttpEntity<>(citizenIds);
            ParameterizedTypeReference<RestTemplateResponseEnvelope<List<TaskTypeAggregateResult>>> typeReference = new ParameterizedTypeReference<RestTemplateResponseEnvelope<List<TaskTypeAggregateResult>>>() {};
            ResponseEntity<RestTemplateResponseEnvelope<List<TaskTypeAggregateResult>>> restExchange =
                    restTemplate.exchange(
                            "http://zuulservice/kairos/activity/api/v1/task_demand/citizen/task_types",
                            HttpMethod.POST, request, typeReference);

            RestTemplateResponseEnvelope<List<TaskTypeAggregateResult>> response = restExchange.getBody();
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
     * map in task demand controller
     * @param unitId
     * @return
     */
    public List<OrgTaskTypeAggregateResult> getTaskTypesOfUnit(Long unitId) {

        final String baseUrl=getBaseUrl(true);

        try {
            ParameterizedTypeReference<RestTemplateResponseEnvelope<List<OrgTaskTypeAggregateResult>>> typeReference = new ParameterizedTypeReference<RestTemplateResponseEnvelope<List<OrgTaskTypeAggregateResult>>>() {};
            ResponseEntity<RestTemplateResponseEnvelope<List<OrgTaskTypeAggregateResult>>> restExchange =
                    restTemplate.exchange(
                            "http://zuulservice/kairos/activity/api/v1/task_demand/unit/{unitId}",
                            HttpMethod.
                                    GET,null, typeReference,unitId);

            RestTemplateResponseEnvelope<List<OrgTaskTypeAggregateResult>> response = restExchange.getBody();
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
     *  @auther anil maurya
     *
     *  map in task demand controller
     * @param staffId
     * @param organizationId
     * @param mapList
     * @return
     */

    public Map<String,Object> getOrganizationClientsWithPlanning(Long staffId, Long organizationId, List<Map<String, Object>> mapList){

        try {
            HttpEntity<List> request = new HttpEntity<>(mapList);
            ParameterizedTypeReference<RestTemplateResponseEnvelope<Map<String,Object>>> typeReference = new ParameterizedTypeReference<RestTemplateResponseEnvelope<Map<String,Object>>>() {};
            ResponseEntity<RestTemplateResponseEnvelope<Map<String,Object>>> restExchange =
                    restTemplate.exchange("http://zuulservice/kairos/activity/api/v1/task_demand/organization/{organizationId}/{staffId}",
                            HttpMethod.
                                    POST,request, typeReference,organizationId,staffId);

            RestTemplateResponseEnvelope<Map<String,Object>> response = restExchange.getBody();
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
     * map endpoind on task demand controller in task micro service
     * @param organizationId
     * @param mapList
     * @return
     */
    public Map<String, Object> getOrganizationClientsInfo(Long organizationId, List<Map<String, Object>> mapList) {

        try {
            HttpEntity<List> request = new HttpEntity<>(mapList);
            ParameterizedTypeReference<RestTemplateResponseEnvelope<Map<String,Object>>> typeReference = new ParameterizedTypeReference<RestTemplateResponseEnvelope<Map<String,Object>>>(){};
            ResponseEntity<RestTemplateResponseEnvelope<Map<String,Object>>> restExchange =
                    restTemplate.exchange("http://zuulservice/kairos/activity/api/v1/task_demand/organization/{organizationId}",
                            HttpMethod.
                                    POST,request, typeReference,organizationId);

            RestTemplateResponseEnvelope<Map<String,Object>> response = restExchange.getBody();
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
