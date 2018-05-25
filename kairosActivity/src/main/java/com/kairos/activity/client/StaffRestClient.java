package com.kairos.activity.client;

import com.kairos.activity.client.dto.ClientStaffInfoDTO;
import com.kairos.activity.client.dto.RestTemplateResponseEnvelope;
import com.kairos.activity.client.dto.staff.StaffAdditionalInfoDTO;
import com.kairos.activity.client.dto.staff.StaffDTO;
import com.kairos.activity.util.userContext.UserContext;
import com.kairos.response.dto.web.access_group.UserAccessRoleDTO;
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

import static com.kairos.activity.util.RestClientUrlUtil.getBaseUrl;

@Component
public class StaffRestClient {
    private static final Logger logger = LoggerFactory.getLogger(StaffRestClient.class);


    @Autowired
    RestTemplate restTemplate;

    /**
     * @param staffIds
     * @return
     * @auther anil maurya
     * map endpoint in staff controller in user micro service
     */
    public Map<String, Object> getTeamStaffAndStaffSkill(List<Long> staffIds) {
        final String baseUrl = getBaseUrl(true);

        try {
            ParameterizedTypeReference<RestTemplateResponseEnvelope<Map<String, Object>>> typeReference = new ParameterizedTypeReference<RestTemplateResponseEnvelope<Map<String, Object>>>() {
            };
            HttpEntity<List> request = new HttpEntity<>(staffIds);
            ResponseEntity<RestTemplateResponseEnvelope<Map<String, Object>>> restExchange =
                    restTemplate.exchange(
                            baseUrl + "/staff/getsfAndsfSkill",
                            HttpMethod.POST,
                            request, typeReference);
            RestTemplateResponseEnvelope<Map<String, Object>> response = restExchange.getBody();
            if (restExchange.getStatusCode().is2xxSuccessful()) {
                return response.getData();
            } else {
                throw new RuntimeException(response.getMessage());
            }
        } catch (HttpClientErrorException e) {
            logger.info("status {}", e.getStatusCode());
            logger.info("response {}", e.getResponseBodyAsString());
            throw new RuntimeException("exception occurred in user micro service " + e.getMessage());
        }

    }


    /**
     * @param staffId
     * @return
     * @auther anil maurya
     * <p>
     * enpoint map in staff controller
     */
    public StaffDTO getStaff(Long staffId) {

        final String baseUrl = getBaseUrl(true);

        try {
            ParameterizedTypeReference<RestTemplateResponseEnvelope<StaffDTO>> typeReference = new ParameterizedTypeReference<RestTemplateResponseEnvelope<StaffDTO>>() {
            };
            ResponseEntity<RestTemplateResponseEnvelope<StaffDTO>> restExchange =
                    restTemplate.exchange(
                            baseUrl + "/staff/{staffId}",
                            HttpMethod.GET, null, typeReference, staffId);
            RestTemplateResponseEnvelope<StaffDTO> response = restExchange.getBody();
            if (restExchange.getStatusCode().is2xxSuccessful()) {
                return response.getData();
            } else {
                throw new RuntimeException(response.getMessage());
            }
        } catch (HttpClientErrorException e) {
            logger.info("status {}", e.getStatusCode());
            logger.info("response {}", e.getResponseBodyAsString());
            throw new RuntimeException("exception occurred in user micro service " + e.getMessage());
        }
    }


    /**
     * @return
     * @auther anil maurya
     * map this endpoint on staff controller
     */
    public ClientStaffInfoDTO getStaffInfo() {
        final String baseUrl = getBaseUrl(true);

        try {
            ParameterizedTypeReference<RestTemplateResponseEnvelope<ClientStaffInfoDTO>> typeReference = new ParameterizedTypeReference<RestTemplateResponseEnvelope<ClientStaffInfoDTO>>() {
            };
            ResponseEntity<RestTemplateResponseEnvelope<ClientStaffInfoDTO>> restExchange =
                    restTemplate.exchange(
                            baseUrl + "/staff/getStaffInfo",
                            HttpMethod.GET,
                            null, typeReference);
            RestTemplateResponseEnvelope<ClientStaffInfoDTO> response = restExchange.getBody();
            if (restExchange.getStatusCode().is2xxSuccessful()) {

                ClientStaffInfoDTO clientStaffInfoDTO = response.getData();
                return clientStaffInfoDTO;
            } else {
                throw new RuntimeException(response.getMessage());
            }
        } catch (HttpClientErrorException e) {
            logger.info("status {}", e.getStatusCode());
            logger.info("response {}", e.getResponseBodyAsString());
            throw new RuntimeException("exception occurred in user micro service " + e.getMessage());
        }

    }

    /**
     * @return
     * @auther Jasgeet
     * map this endpoint on staff controller
     */
    public List<Long> getUnitManagerIds(Long unitId) {
        final String baseUrl = getBaseUrl(true);

        try {
            ParameterizedTypeReference<RestTemplateResponseEnvelope<List<Long>>> typeReference = new ParameterizedTypeReference<RestTemplateResponseEnvelope<List<Long>>>() {
            };
            ResponseEntity<RestTemplateResponseEnvelope<List<Long>>> restExchange =
                    restTemplate.exchange(
                            baseUrl + "/unit_manager_ids/{unitId}",
                            HttpMethod.GET,
                            null, typeReference, unitId);
            RestTemplateResponseEnvelope<List<Long>> response = restExchange.getBody();
            if (restExchange.getStatusCode().is2xxSuccessful()) {

                List<Long> staffDTOList = response.getData();
                return staffDTOList;
            } else {
                throw new RuntimeException(response.getMessage());
            }
        } catch (HttpClientErrorException e) {
            logger.info("status {}", e.getStatusCode());
            logger.info("response {}", e.getResponseBodyAsString());
            throw new RuntimeException("exception occurred in user micro service " + e.getMessage());
        }

    }

    /**
     * @return
     * @auther Jasgeet
     * map this endpoint on staff controller
     */
    public List<Long> getCountryAdminsIds(Long countryAdminsOfUnitId) {
        final String baseUrl = getBaseUrl(false);
        try {
            ParameterizedTypeReference<RestTemplateResponseEnvelope<List<Long>>> typeReference = new ParameterizedTypeReference<RestTemplateResponseEnvelope<List<Long>>>() {
            };

            ResponseEntity<RestTemplateResponseEnvelope<List<Long>>> restExchange =
                    restTemplate.exchange(
                            baseUrl + "/country_admins_ids/{countryAdminsOfUnitId}",
                            HttpMethod.GET,
                            null, typeReference, countryAdminsOfUnitId);
            RestTemplateResponseEnvelope<List<Long>> response = restExchange.getBody();
            if (restExchange.getStatusCode().is2xxSuccessful()) {
                List<Long> staffDTOList = response.getData();
                return staffDTOList;
            } else {
                throw new RuntimeException(response.getMessage());
            }
        } catch (HttpClientErrorException e) {
            logger.info("status {}", e.getStatusCode());
            logger.info("response {}", e.getResponseBodyAsString());
            throw new RuntimeException("exception occurred in user micro service " + e.getMessage());
        }

    }


    /**
     * @param staffId, unitId
     * @return
     * @auther Vipul Pandey
     * <p>
     * enpoint map in staff controller
     */
    public StaffAdditionalInfoDTO verifyUnitEmploymentOfStaff(Long staffId, String type) {

        final String baseUrl = getBaseUrl(true);

        try {
            ParameterizedTypeReference<RestTemplateResponseEnvelope<StaffAdditionalInfoDTO>> typeReference = new ParameterizedTypeReference<RestTemplateResponseEnvelope<StaffAdditionalInfoDTO>>() {
            };
            ResponseEntity<RestTemplateResponseEnvelope<StaffAdditionalInfoDTO>> restExchange =
                    restTemplate.exchange(
                            baseUrl + "/staff/{staffId}/verifyUnitEmployment?type=" + type,
                            HttpMethod.GET, null, typeReference, staffId);
            RestTemplateResponseEnvelope<StaffAdditionalInfoDTO> response = restExchange.getBody();
            if (restExchange.getStatusCode().is2xxSuccessful()) {
                return response.getData();
            } else {
                throw new RuntimeException(response.getMessage());
            }
        } catch (HttpClientErrorException e) {
            logger.info("status {}", e.getStatusCode());
            logger.info("response {}", e.getResponseBodyAsString());
            throw new RuntimeException("exception occurred in user micro service " + e.getMessage());
        }
    }

    public StaffAdditionalInfoDTO verifyUnitEmploymentOfStaff(Long staffId, String type, Long unitEmploymentId,List<Long> activityDayTypes) {

        final String baseUrl = getBaseUrl(true);

        String dayType = "";
        if(activityDayTypes!=null && !activityDayTypes.isEmpty()) {
            String a = activityDayTypes.toString().replace("[","");
            a.replace("]","");
            dayType = a.replace("]","");
        }
        try {
            ParameterizedTypeReference<RestTemplateResponseEnvelope<StaffAdditionalInfoDTO>> typeReference = new ParameterizedTypeReference<RestTemplateResponseEnvelope<StaffAdditionalInfoDTO>>() {
            };
            ResponseEntity<RestTemplateResponseEnvelope<StaffAdditionalInfoDTO>> restExchange =
                    restTemplate.exchange(
                            baseUrl + "7599/staff/{staffId}/verifyUnitEmployment/{unitEmploymentId}/?type=" + type+"&activityDayTypes="+dayType,
                            HttpMethod.GET, null, typeReference, staffId, unitEmploymentId);
            RestTemplateResponseEnvelope<StaffAdditionalInfoDTO> response = restExchange.getBody();
            if (restExchange.getStatusCode().is2xxSuccessful()) {
                return response.getData();
            } else {
                throw new RuntimeException(response.getMessage());
            }
        } catch (HttpClientErrorException e) {
            logger.info("status {}", e.getStatusCode());
            logger.info("response {}", e.getResponseBodyAsString());
            throw new RuntimeException("exception occurred in user micro service " + e.getMessage());
        }
    }

    /**
     * @param userId
     * @return
     * @auther Jasgeet Singh
     * <p>
     * enpoint map in staff controller
     */
    public StaffDTO getStaffByUser(Long userId) {

        final String baseUrl = getBaseUrl(true);

        try {
            ParameterizedTypeReference<RestTemplateResponseEnvelope<StaffDTO>> typeReference = new ParameterizedTypeReference<RestTemplateResponseEnvelope<StaffDTO>>() {
            };
            ResponseEntity<RestTemplateResponseEnvelope<StaffDTO>> restExchange =
                    restTemplate.exchange(
                            baseUrl + "/staff/current_user/{userId}",
                            HttpMethod.GET, null, typeReference, userId);
            RestTemplateResponseEnvelope<StaffDTO> response = restExchange.getBody();
            if (restExchange.getStatusCode().is2xxSuccessful()) {
                return response.getData();
            } else {
                throw new RuntimeException(response.getMessage());
            }
        } catch (HttpClientErrorException e) {
            logger.info("status {}", e.getStatusCode());
            logger.info("response {}", e.getResponseBodyAsString());
            throw new RuntimeException("exception occurred in user micro service " + e.getMessage());
        }
    }

    public List<StaffDTO> getStaffInfo(Long unitId,List<Long> expertiesId){
        final String baseUrl=getBaseUrl(true);

        HttpEntity<List> request = new HttpEntity(expertiesId);
        try {
            ParameterizedTypeReference<RestTemplateResponseEnvelope<List<StaffDTO>>> typeReference = new ParameterizedTypeReference<RestTemplateResponseEnvelope<List<StaffDTO>>>() {};
            ResponseEntity<RestTemplateResponseEnvelope<List<StaffDTO>>> restExchange =
                    restTemplate.exchange(
                            baseUrl + "/staff/getStaffByExperties",
                            HttpMethod.POST,
                            request, typeReference, unitId);
            RestTemplateResponseEnvelope<List<StaffDTO>> response = restExchange.getBody();
            if (restExchange.getStatusCode().is2xxSuccessful()) {

                List<StaffDTO> staffDTOList =  response.getData();
                return staffDTOList;
            } else {
                throw new RuntimeException(response.getMessage());
            }
        }catch (HttpClientErrorException e) {
            logger.info("status {}",e.getStatusCode());
            logger.info("response {}",e.getResponseBodyAsString());
            throw new RuntimeException("exception occurred in user micro service "+e.getMessage());
        }

    }

    public UserAccessRoleDTO getAccessOfCurrentLoggedInStaff() {

        final String baseUrl = getBaseUrl(true);

        try {
            ParameterizedTypeReference<RestTemplateResponseEnvelope<UserAccessRoleDTO>> typeReference = new ParameterizedTypeReference<RestTemplateResponseEnvelope<UserAccessRoleDTO>>() {
            };
            ResponseEntity<RestTemplateResponseEnvelope<UserAccessRoleDTO>> restExchange =
                    restTemplate.exchange(
                            baseUrl + "/current_user/access_role",
                            HttpMethod.GET, null, typeReference);
            RestTemplateResponseEnvelope<UserAccessRoleDTO> response = restExchange.getBody();
            if (restExchange.getStatusCode().is2xxSuccessful()) {
                return response.getData();
            } else {
                throw new RuntimeException(response.getMessage());
            }
        } catch (HttpClientErrorException e) {
            logger.info("status {}", e.getStatusCode());
            logger.info("response {}", e.getResponseBodyAsString());
            throw new RuntimeException("exception occurred in user micro service " + e.getMessage());
        }
    }
}
