package com.kairos.client;

import com.kairos.client.dto.OrgTaskTypeAggregateResult;
import com.kairos.client.dto.RestTemplateResponseEnvelope;
import com.kairos.client.dto.TableConfiguration;
import com.kairos.client.dto.TaskTypeAggregateResult;
import com.kairos.response.dto.web.EscalatedTasksWrapper;
import com.kairos.response.dto.web.ResponseEnvelope;
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

import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 *
 * @author Anil maurya
 *
 */
@Component
public class ClientServiceRestClient {
    @Autowired
    RestTemplate restTemplate;



    private static final Logger logger = LoggerFactory.getLogger(ClientServiceRestClient.class);


    /**
     * @auther anil maurya
     * map in task controller
     * @param clientId
     * @param organizationId1
     * @return List
     * @see List<Long>
     *
     */
    public List<Long> getClientTaskServices(Long clientId, Long organizationId1) {
        final String baseUrl=getBaseUrl();

        try {
            ParameterizedTypeReference<RestTemplateResponseEnvelope<List<Long>>> typeReference = new ParameterizedTypeReference<RestTemplateResponseEnvelope<List<Long>>>() {};
            ResponseEntity<RestTemplateResponseEnvelope<List<Long>>> restExchange =
                    restTemplate.exchange(
                            baseUrl + "/task/{clientId}/organization/{organizationId1}/service",
                            HttpMethod.GET,
                            null,typeReference,clientId,organizationId1);

            RestTemplateResponseEnvelope<List<Long>> response = restExchange.getBody();
            if (restExchange.getStatusCode().is2xxSuccessful()) {
                return response.getData();
            } else {
                throw new RuntimeException(response.getMessage());
            }
        } catch (HttpClientErrorException e) {
            logger.info("status {}",e.getStatusCode());
            logger.info("response {}",e.getResponseBodyAsString());
            throw new RuntimeException("exception occurred in task micro service "+e.getMessage());
        }


    }


    /**
     * @auther anil maurya
     * map in planner controller
     * @param citizenId
     * @return
     */
    public boolean deleteTaskForCitizen(Long citizenId){

        final String baseUrl=getBaseUrl();

        try {
            ParameterizedTypeReference<RestTemplateResponseEnvelope<Boolean>> typeReference = new ParameterizedTypeReference<RestTemplateResponseEnvelope<Boolean>>() {};
            ResponseEntity<RestTemplateResponseEnvelope<Boolean>> restExchange =
                    restTemplate.exchange(
                            baseUrl + "/planner/citizen/{citizenId}",
                            HttpMethod.DELETE,
                            null, typeReference, citizenId);

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
                            "http://zuulservice/activity/api/v1/task_demand/getTaskTypes",
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

        final String baseUrl=getBaseUrl();

        try {
            ParameterizedTypeReference<RestTemplateResponseEnvelope<List<OrgTaskTypeAggregateResult>>> typeReference = new ParameterizedTypeReference<RestTemplateResponseEnvelope<List<OrgTaskTypeAggregateResult>>>() {};
            ResponseEntity<RestTemplateResponseEnvelope<List<OrgTaskTypeAggregateResult>>> restExchange =
                    restTemplate.exchange(
                            "http://zuulservice/activity/api/v1/task_demand/unit/{unitId}",
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

    public Map<String,Object>  getOrganizationClientsWithPlanning(Long staffId,Long organizationId,List<Map<String, Object>> mapList){

        try {
            HttpEntity<List> request = new HttpEntity<>(mapList);
            ParameterizedTypeReference<RestTemplateResponseEnvelope<Map<String,Object>>> typeReference = new ParameterizedTypeReference<RestTemplateResponseEnvelope<Map<String,Object>>>() {};
            ResponseEntity<RestTemplateResponseEnvelope<Map<String,Object>>> restExchange =
                    restTemplate.exchange("http://zuulservice/activity/api/v1/task_demand/organization/{organizationId}/{staffId}",
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
                    restTemplate.exchange("http://zuulservice/activity/api/v1/task_demand/organization/{organizationId}",
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







    /**
     *  @auther anil maurya
     *
     * @param clientId
     * @param serviceId
     * @param unitId
     * @return
     */
    public List<Object> getClientTaskByServiceIdAndUnitId(Long clientId,Long serviceId,Long unitId){

        ResponseEntity<List> restExchange =
                restTemplate.exchange(
                        "http://zuulservice/activity/api/v1/task/{clientId}/{serviceId}/{unitId}",
                        HttpMethod.GET,
                        null,List.class);

        List<Object> clientTaks = restExchange.getBody();
        return clientTaks;
    }


    /**
     * @auther anil maurya
     * @param orgainationId
     * @return
     */
    public  List<Object> getTaskTypesByOrganizarion(Long orgainationId){


        final String baseUrl=getBaseUrl();

        try {
            ResponseEntity<ResponseEnvelope> restExchange =
                    restTemplate.exchange(
                            "http://zuulservice/activity/api/v1/organization/{organizationId}/task_types",
                            HttpMethod.
                                    GET,null, ResponseEnvelope.class);

            ResponseEnvelope response = restExchange.getBody();
            if (restExchange.getStatusCode().is2xxSuccessful()) {

                List<OrgTaskTypeAggregateResult> OrgTaskTypeList= (List<OrgTaskTypeAggregateResult>) response.getData();
                List<Object> taskTypesList= (List<Object>) response.getData();
                return taskTypesList;
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
     *
     * @param staffId
     * @param organizationId
     * @param unitId
     * @return
     */
    public TableConfiguration getTableConfiguration(long organizationId,long unitId,Long staffId){

        final String baseUrl=getBaseUrl();

        try {
            ParameterizedTypeReference<RestTemplateResponseEnvelope<TableConfiguration>> typeReference = new ParameterizedTypeReference<RestTemplateResponseEnvelope<TableConfiguration>>(){};
            ResponseEntity<RestTemplateResponseEnvelope<TableConfiguration>> restExchange =
                    restTemplate.exchange(
                            baseUrl + "/table/{staffId}",
                            HttpMethod.
                                    GET,null, typeReference,staffId);

            RestTemplateResponseEnvelope<TableConfiguration> response = restExchange.getBody();
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


    public List<EscalatedTasksWrapper> getStaffNotAssignedTasks(Long unitId){
        return Collections.emptyList();
    }
    private final String getBaseUrl(){
        String baseUrl=new StringBuilder("http://zuulservice/activity/api/v1/organization/").append(UserContext.getOrgId()).append("/unit/").append(UserContext.getUnitId()).toString();
        return baseUrl;
    }

}
