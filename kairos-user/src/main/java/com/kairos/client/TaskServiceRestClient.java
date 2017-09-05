package com.kairos.client;

import com.kairos.client.dto.OrgTaskTypeAggregateResult;
import com.kairos.client.dto.RestTemplateResponseEnvelope;
import com.kairos.response.dto.web.EscalatedTasksWrapper;
import com.kairos.response.dto.web.KMDShift;
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

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class TaskServiceRestClient {

    private static final Logger logger = LoggerFactory.getLogger(SkillServiceTemplateClient.class);
    @Autowired
    RestTemplate restTemplate;
    /**
     * @auther anil maurya
     * map in task  controller
     * @param unitId
     * @return
     */
    public Boolean createTaskFromKMD(Long staffId, KMDShift shift, Long unitId){
        String baseUrl = getBaseUrl(false);
        try {
            HttpEntity<KMDShift> request = new HttpEntity<>(shift);
            ParameterizedTypeReference<RestTemplateResponseEnvelope<Boolean>> typeReference = new ParameterizedTypeReference<RestTemplateResponseEnvelope<Boolean>>() {};
            ResponseEntity<RestTemplateResponseEnvelope<Boolean>> restExchange =
                    restTemplate.exchange(
                            baseUrl + "/unit/{unitId}/createTask/{staffId}",
                            HttpMethod.POST, request, typeReference,unitId,staffId);

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
     * map in task controller
     * @param clientId
     * @param organizationId1
     * @return List
     * @see List <Long>
     *
     */
    public List<Long> getClientTaskServices(Long clientId, Long organizationId1) {
        final String baseUrl=getBaseUrl(true);

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


        final String baseUrl=getBaseUrl(true);

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


    public List<EscalatedTasksWrapper> getStaffNotAssignedTasks(Long unitId){

        final String baseUrl=getBaseUrl(true);

        try {
            ParameterizedTypeReference<RestTemplateResponseEnvelope<List<EscalatedTasksWrapper>>> typeReference = new ParameterizedTypeReference<RestTemplateResponseEnvelope<List<EscalatedTasksWrapper>>>(){};
            ResponseEntity<RestTemplateResponseEnvelope<List<EscalatedTasksWrapper>>> restExchange =
                    restTemplate.exchange(
                            baseUrl + "/getStaffNotAssignedTasks",
                            HttpMethod.GET,null, typeReference);

            RestTemplateResponseEnvelope<List<EscalatedTasksWrapper>> response = restExchange.getBody();
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
     *  endpoint map in task controller
     * @param staffId
     * @param anonymousStaffId
     * @return
     */
    public boolean  updateTaskForStaff(Long staffId,Long anonymousStaffId){

        final String baseUrl=getBaseUrl(true);

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
     * @param staffId
     * @param date
     * @return
     */
    public List<StaffAssignedTasksWrapper> getAssignedTasksOfStaff(long staffId,String date) {


        final String baseUrl = getBaseUrl(true);

        try {
            ParameterizedTypeReference<RestTemplateResponseEnvelope<List<StaffAssignedTasksWrapper>>> typeReference = new ParameterizedTypeReference<RestTemplateResponseEnvelope<List<StaffAssignedTasksWrapper>>>() {
            };
            String url = baseUrl + "/task/staff/{staffId}/assigned_tasks?date=" + date;
            // URI (URL) parameters
            Map<String, Object> uriParams = new HashMap<>();
            uriParams.put("staffId", staffId);

            UriComponentsBuilder builder = UriComponentsBuilder.fromUriString(url)
                    // Add query parameter
                    .queryParam("date", date);
            ResponseEntity<RestTemplateResponseEnvelope<List<StaffAssignedTasksWrapper>>> restExchange =
                    restTemplate.exchange(url,
                            HttpMethod.
                                    GET, null, typeReference, staffId);

            RestTemplateResponseEnvelope<List<StaffAssignedTasksWrapper>> response = restExchange.getBody();
            if (restExchange.getStatusCode().is2xxSuccessful()) {
                return response.getData();

            } else {
                throw new RuntimeException(response.getMessage());
            }
        } catch (HttpClientErrorException e) {

            logger.info("status {}", e.getStatusCode());
            logger.info("response {}", e.getResponseBodyAsString());
            throw new RuntimeException("exception occurred in task micro service " + e.getMessage());
        }

    }

    private final String getBaseUrl(boolean hasUnitInUrl){
        if(hasUnitInUrl){
            String baseUrl=new StringBuilder("http://zuulservice/activity/api/v1/organization/").append(UserContext.getOrgId()).append("/unit/").append(UserContext.getUnitId()).toString();
            return baseUrl;
        }else{
            String baseUrl=new StringBuilder("http://zuulservice/activity/api/v1/organization/").append(UserContext.getOrgId()).toString();
            return baseUrl;
        }

    }

}
