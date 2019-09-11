package com.kairos.rest_client;

import com.kairos.commons.client.RestTemplateResponseEnvelope;
import com.kairos.dto.activity.task.EscalatedTasksWrapper;
import com.kairos.dto.activity.task.StaffAssignedTasksWrapper;
import com.kairos.dto.planner.vrp.task.VRPTaskDTO;
import com.kairos.dto.user.staff.ImportShiftDTO;
import com.kairos.service.exception.ExceptionService;
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

import javax.inject.Inject;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.kairos.constants.UserMessagesConstants.MESSAGE_EXCEPTION_TASKMICROSERVICE;
import static com.kairos.rest_client.RestClientURLUtil.getBaseUrl;

@Component
public class TaskServiceRestClient {

    private static final Logger logger = LoggerFactory.getLogger(SkillServiceTemplateClient.class);
    @Autowired
    RestTemplate restTemplate;
    @Inject
    private ExceptionService exceptionService;
    /**
     * @auther anil maurya
     * map in task  controller
     * @param unitId
     * @return
     */
    public void createTaskFromKMD(Long staffId, ImportShiftDTO shift, Long unitId){
        String baseUrl = getBaseUrl(false);
        try {
            HttpEntity<ImportShiftDTO> request = new HttpEntity<>(shift);
            ParameterizedTypeReference<RestTemplateResponseEnvelope<Boolean>> typeReference = new ParameterizedTypeReference<RestTemplateResponseEnvelope<Boolean>>() {};
            ResponseEntity<RestTemplateResponseEnvelope<Boolean>> restExchange =
                    restTemplate.exchange(
                            baseUrl + "/unit/{unitId}/createTask/{staffId}",
                            HttpMethod.POST, request, typeReference,unitId,staffId);

            RestTemplateResponseEnvelope<Boolean> response = restExchange.getBody();
            if (!restExchange.getStatusCode().is2xxSuccessful()) {
                throw new RuntimeException(response.getMessage());
            }
        }catch (HttpClientErrorException e) {

            logger.info("status {}",e.getStatusCode());
            logger.info("response {}",e.getResponseBodyAsString());
            exceptionService.runtimeException(MESSAGE_EXCEPTION_TASKMICROSERVICE,e.getMessage());

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
            exceptionService.runtimeException(MESSAGE_EXCEPTION_TASKMICROSERVICE,e.getMessage());

        }
        return null;
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
            exceptionService.runtimeException(MESSAGE_EXCEPTION_TASKMICROSERVICE,e.getMessage());

        }
        return null;
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
            exceptionService.runtimeException(MESSAGE_EXCEPTION_TASKMICROSERVICE,e.getMessage());

        }
        return false;


    }



    /**
     * @auther anil maurya
     * map this endpoint on task controller
     * @param staffId
     * @param date
     * @return
     */
    public List<StaffAssignedTasksWrapper> getAssignedTasksOfStaff(long staffId, String date) {


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
            exceptionService.runtimeException(MESSAGE_EXCEPTION_TASKMICROSERVICE,e.getMessage());

        }
        return null;

    }

    public void createTaskBYExcel(List<VRPTaskDTO> vrpTaskDTOS){
        String baseUrl = getBaseUrl(true);
        try {
            HttpEntity<List<VRPTaskDTO>> request = new HttpEntity<>(vrpTaskDTOS);
            ParameterizedTypeReference<RestTemplateResponseEnvelope<Boolean>> typeReference = new ParameterizedTypeReference<RestTemplateResponseEnvelope<Boolean>>() {};
            ResponseEntity<RestTemplateResponseEnvelope<Boolean>> restExchange =
                    restTemplate.exchange(
                            baseUrl + "/importTask",
                            HttpMethod.POST, request, typeReference);

            RestTemplateResponseEnvelope<Boolean> response = restExchange.getBody();
            if (!restExchange.getStatusCode().is2xxSuccessful()) {
                throw new RuntimeException(response.getMessage());
            }
        }catch (HttpClientErrorException e) {

            logger.info("status {}",e.getStatusCode());
            logger.info("response {}",e.getResponseBodyAsString());
            exceptionService.runtimeException(MESSAGE_EXCEPTION_TASKMICROSERVICE,e.getMessage());

        }
    }


}
