package com.kairos.rest_client;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.kairos.dto.user.staff.ClientStaffInfoDTO;
import com.kairos.dto.user.organization.skill.OrganizationClientWrapper;
import com.kairos.controller.exception_handler.ResponseEnvelope;
import com.kairos.persistence.model.client_exception.ClientExceptionDTO;
import com.kairos.dto.user.client.Client;
import com.kairos.dto.user.client.ClientOrganizationIds;
import com.kairos.dto.user.client.ClientTemporaryAddress;
import com.kairos.wrapper.task_demand.TaskDemandRequestWrapper;
import com.kairos.wrapper.task_demand.TaskDemandVisitWrapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import static com.kairos.utils.RestClientUrlUtil.getBaseUrl;

@Component
public class ClientRestClient {

    private static final Logger logger = LoggerFactory.getLogger(ClientRestClient.class);
    @Autowired
    RestTemplate restTemplate;
    @Autowired
    @Qualifier("schedulerRestTemplate")
    private RestTemplate schedulerRestTemplate;


    public Client getClient(Long clientId){
        final String baseUrl=getBaseUrl(true);

        try {
            ParameterizedTypeReference<RestTemplateResponseEnvelope<Client>> typeReference = new ParameterizedTypeReference<RestTemplateResponseEnvelope<Client>>(){};
            ResponseEntity<RestTemplateResponseEnvelope<Client>> restExchange =
                    restTemplate.exchange(
                            baseUrl + "/client/{clientId}",
                            HttpMethod.GET,
                            null, typeReference,clientId);
            RestTemplateResponseEnvelope<Client> response = restExchange.getBody();
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

    /**
     * @auther anil maurya
     *
     * @param clientExceptionDto
     * @param unitId
     * @param clientId
     * @return
     */
    public ClientTemporaryAddress updateClientTemporaryAddress(ClientExceptionDTO clientExceptionDto, long unitId, Long clientId){

        final String baseUrl=getBaseUrl(true);

        try {
            HttpEntity<ClientExceptionDTO> request = new HttpEntity<>(clientExceptionDto);
            ParameterizedTypeReference<RestTemplateResponseEnvelope<ClientTemporaryAddress>> typeReference = new ParameterizedTypeReference<RestTemplateResponseEnvelope<ClientTemporaryAddress>>(){};
            ResponseEntity<RestTemplateResponseEnvelope<ClientTemporaryAddress>> restExchange =
                    restTemplate.exchange(
                            baseUrl + "/client/{clientId}/updateClientTempAddress",
                            HttpMethod.POST,
                            request, typeReference,clientId);
            RestTemplateResponseEnvelope<ClientTemporaryAddress> response = restExchange.getBody();
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




    /**
     *  @auther anil maurya
     *  map this end point on client controller in user micro service
     * @param citizenId
     * @return
     */
    public Map<String,Object> getClientDetails(Long citizenId){

        final String baseUrl=getBaseUrl(true);

        try {
            ParameterizedTypeReference<RestTemplateResponseEnvelope<Map<String,Object>>> typeReference = new ParameterizedTypeReference<RestTemplateResponseEnvelope<Map<String,Object>>>() {};
            ResponseEntity<RestTemplateResponseEnvelope<Map<String,Object>>> restExchange =
                    restTemplate.exchange(
                            baseUrl + "/client/{citizenId}/info",
                            HttpMethod.GET,null,
                            typeReference, citizenId);
            RestTemplateResponseEnvelope<Map<String,Object>> response = restExchange.getBody();
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


    /**
     *  @auther anil maurya
     *  map this end point on client controller in user micro service
     * @param citizenId
     * @return
     */
    public Map<String,Object> getClientAddressInfo(Long citizenId){

        final String baseUrl=getBaseUrl(true);

        try {
            ParameterizedTypeReference<RestTemplateResponseEnvelope<Map<String,Object>>> typeReference = new ParameterizedTypeReference<RestTemplateResponseEnvelope<Map<String,Object>>>() {};
            ResponseEntity<RestTemplateResponseEnvelope<Map<String,Object>>> restExchange =
                    restTemplate.exchange(
                            baseUrl + "/client/{citizenId}/addressInfo",
                            HttpMethod.GET,null,
                            typeReference, citizenId);
            RestTemplateResponseEnvelope<Map<String,Object>> response = restExchange.getBody();
            if (restExchange.getStatusCode().is2xxSuccessful()) {
                return response.getData();
            } else {
                throw new RuntimeException(response.getMessage());
            }
        }catch (HttpClientErrorException e) {
            logger.info("status {}",e.getStatusCode());
            logger.info("response {}",e.getResponseBodyAsString());

            ResponseEnvelope responseEnvelope = convertRestClientExceptionJsonToDto(e);
            throw new RuntimeException(responseEnvelope.getMessage());
            //throw new DataNotFoundByIdException(restTemplateExceptionDTO.getMessage());
        }
    }

    /**
     * @auther anil maurya
     *
     * map this endpoint on client controller
     * @param taskDemandRequestWrapper
     * @return
     */
    public TaskDemandVisitWrapper getClientDetailsForTaskDemandVisit(TaskDemandRequestWrapper taskDemandRequestWrapper){

        final String baseUrl=getBaseUrl(true);
        try {
            ParameterizedTypeReference<RestTemplateResponseEnvelope<TaskDemandVisitWrapper>> typeReference = new ParameterizedTypeReference<RestTemplateResponseEnvelope<TaskDemandVisitWrapper>>() {};
            HttpEntity<TaskDemandRequestWrapper> request = new HttpEntity<>(taskDemandRequestWrapper);
            ResponseEntity<RestTemplateResponseEnvelope<TaskDemandVisitWrapper>> restExchange =
                    restTemplate.exchange(
                            baseUrl + "/client/getClientInfo",
                            HttpMethod.POST,
                            request, typeReference);
            RestTemplateResponseEnvelope<TaskDemandVisitWrapper> response = restExchange.getBody();
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

    /**
     * map this endpoint on client controller
     * @param citizenId
     * @param unitId
     * @return
     */
    public TaskDemandVisitWrapper getPrerequisitesForTaskCreation(Long citizenId, Long unitId){

        final String baseUrl=getBaseUrl(true);
        try {
            ParameterizedTypeReference<RestTemplateResponseEnvelope<TaskDemandVisitWrapper>> typeReference = new ParameterizedTypeReference<RestTemplateResponseEnvelope<TaskDemandVisitWrapper>>() {};
            ResponseEntity<RestTemplateResponseEnvelope<TaskDemandVisitWrapper>> restExchange =
                    restTemplate.exchange(
                            baseUrl + "/client/{citizenId}/{unitId}/task_prerequisites",
                            HttpMethod.GET,
                            null, typeReference,citizenId,unitId);
            RestTemplateResponseEnvelope<TaskDemandVisitWrapper> response = restExchange.getBody();
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

    /**
     * @auther anil maurya
     * map this endpoint on client controller
     * @param organizationId
     *
     * @return
     */
    public OrganizationClientWrapper getOrganizationClients(Long organizationId){
        final String baseUrl=getBaseUrl(true);
        try {
            ParameterizedTypeReference<RestTemplateResponseEnvelope<OrganizationClientWrapper>> typeReference = new ParameterizedTypeReference<RestTemplateResponseEnvelope<OrganizationClientWrapper>>() {};
            ResponseEntity<RestTemplateResponseEnvelope<OrganizationClientWrapper>> restExchange =
                    restTemplate.exchange(
                            baseUrl + "/client/organization_clients",
                            HttpMethod.GET,
                            null, typeReference,organizationId);
            RestTemplateResponseEnvelope<OrganizationClientWrapper> response = restExchange.getBody();
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

    /**
     *map this endpoint on client controller
     * @param unitId
     * @param clientIds
     * @return
     */
    public OrganizationClientWrapper getClientsByIds(Long unitId, List<Long> clientIds){

        final String baseUrl=getBaseUrl(true);
        try {
            ParameterizedTypeReference<RestTemplateResponseEnvelope<OrganizationClientWrapper>> typeReference = new ParameterizedTypeReference<RestTemplateResponseEnvelope<OrganizationClientWrapper>>() {};
            HttpEntity<List> request = new HttpEntity<>(clientIds);
            ResponseEntity<RestTemplateResponseEnvelope<OrganizationClientWrapper>> restExchange =
                    restTemplate.exchange(
                            baseUrl + "/client/organization_clients/ids",
                            HttpMethod.POST,
                            request, typeReference,unitId);
            RestTemplateResponseEnvelope<OrganizationClientWrapper> response = restExchange.getBody();
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


    /**
     * this method will be get citizens of unit,
     * unit id will be extracted from url
     * @return
     */
    public List<Long> getCitizenIds(){

        final String baseUrl=getBaseUrl(true);
        try {
            ParameterizedTypeReference<RestTemplateResponseEnvelope<List<Long>>> typeReference = new ParameterizedTypeReference<RestTemplateResponseEnvelope<List<Long>>>() {};
            ResponseEntity<RestTemplateResponseEnvelope<List<Long>>> restExchange =
                    restTemplate.exchange(
                            baseUrl + "/client/client_ids",
                            HttpMethod.GET,
                            null, typeReference);
            RestTemplateResponseEnvelope<List<Long>> response = restExchange.getBody();
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

    /**
     * @auther anil maurya
     * map this endpoint on client controller
     * @param clientId
     * @return
     */
    public ClientStaffInfoDTO getClientStaffInfo(Long clientId){
        final String baseUrl=getBaseUrl(true);

        try {
            ParameterizedTypeReference<RestTemplateResponseEnvelope<ClientStaffInfoDTO>> typeReference = new ParameterizedTypeReference<RestTemplateResponseEnvelope<ClientStaffInfoDTO>>() {};
            ResponseEntity<RestTemplateResponseEnvelope<ClientStaffInfoDTO>> restExchange =
                    restTemplate.exchange(
                            baseUrl + "/client/{clientId}/getClientStaffInfo",
                            HttpMethod.GET,
                            null, typeReference, clientId);
            RestTemplateResponseEnvelope<ClientStaffInfoDTO> response = restExchange.getBody();
            if (restExchange.getStatusCode().is2xxSuccessful()) {

                ClientStaffInfoDTO clientStaffInfoDTO =  response.getData();
                return clientStaffInfoDTO;
            } else {
                throw new RuntimeException(response.getMessage());
            }
        }catch (HttpClientErrorException e) {
            logger.info("status {}",e.getStatusCode());
            logger.info("response {}",e.getResponseBodyAsString());
            throw new RuntimeException("exception occurred in user micro service "+e.getMessage());
        }


    }



    public Map<String, Object> getStaffAndCitizenHouseholds(Long citizenId,Long staffId){

        final String baseUrl=getBaseUrl(true);

        try {
            ParameterizedTypeReference<RestTemplateResponseEnvelope<Map<String, Object>>> typeReference = new ParameterizedTypeReference<RestTemplateResponseEnvelope<Map<String, Object>>>() {};
            ResponseEntity<RestTemplateResponseEnvelope<Map<String, Object>>> restExchange =
                    restTemplate.exchange(
                            baseUrl + "/client/{citizenId}/staff/{staffId}/getStaffCitizenHouseholds",
                            HttpMethod.GET,
                            null, typeReference,citizenId,staffId);
            RestTemplateResponseEnvelope<Map<String, Object>> response = restExchange.getBody();
            if (restExchange.getStatusCode().is2xxSuccessful()) {

                Map<String, Object> getStaffAndCitizenHouseholds =  response.getData();
                return getStaffAndCitizenHouseholds;
            } else {
                throw new RuntimeException(response.getMessage());
            }
        }catch (HttpClientErrorException e) {
            logger.info("status {}",e.getStatusCode());
            logger.info("response {}",e.getResponseBodyAsString());
            throw new RuntimeException("exception occurred in user micro service "+e.getMessage());
        }
    }

    public List<Client> getCitizensByIdsInList(List<Long> citizenIds){
        final String baseUrl=getBaseUrl(true);

        try {
            ParameterizedTypeReference<RestTemplateResponseEnvelope<List<Client>>> typeReference = new ParameterizedTypeReference<RestTemplateResponseEnvelope<List<Client>>>(){};
            HttpEntity<List> request = new HttpEntity<>(citizenIds);
            ResponseEntity<RestTemplateResponseEnvelope<List<Client>>> restExchange =
                    restTemplate.exchange(
                            baseUrl + "/client/clientsByIds",
                            HttpMethod.POST,
                            request, typeReference);
            RestTemplateResponseEnvelope<List<Client>> response = restExchange.getBody();
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

    private ResponseEnvelope convertRestClientExceptionJsonToDto(HttpClientErrorException e){
        ObjectMapper objectMapper = new ObjectMapper();
        ResponseEnvelope responseEnvelope = null;
        try {
            responseEnvelope = objectMapper.readValue(e.getResponseBodyAsString(),ResponseEnvelope.class);
        } catch (IOException e1) {
            e1.printStackTrace();
        }
        return responseEnvelope;
    }

    /**
     * this method will be get citizens of unit,
     * unit id will be extracted from url
     * @return
     */
    public List<ClientOrganizationIds> getCitizenIdsByUnitIds(List<Long> unitIds){

        final String baseUrl=getBaseUrl();
        Long unitId = unitIds.get(0);
        Long organizationId = unitIds.get(0);
        try {
            ParameterizedTypeReference<RestTemplateResponseEnvelope<List<ClientOrganizationIds>>> typeReference = new ParameterizedTypeReference<RestTemplateResponseEnvelope<List<ClientOrganizationIds>>>() {};
            HttpEntity<List<Long>> request = new HttpEntity<>(unitIds);
            ResponseEntity<RestTemplateResponseEnvelope<List<ClientOrganizationIds>>> restExchange =
                    schedulerRestTemplate.exchange(
                            baseUrl+"organization/{organizationId}/unit/{unitId}/client/client_ids_by_unitIds",
                            HttpMethod.POST,
                            request, typeReference, organizationId, unitId);
            RestTemplateResponseEnvelope<List<ClientOrganizationIds>> response = restExchange.getBody();
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
