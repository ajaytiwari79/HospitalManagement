package com.kairos.rest_client;

import com.kairos.commons.utils.DateUtils;
import com.kairos.dto.activity.shift.StaffUnitPositionDetails;
import com.kairos.dto.user.access_group.UserAccessRoleDTO;
import com.kairos.dto.user.staff.ClientStaffInfoDTO;
import com.kairos.dto.user.staff.staff.StaffDTO;
import com.kairos.dto.user.staff.staff.UnitStaffResponseDTO;
import com.kairos.dto.user.user.staff.StaffAdditionalInfoDTO;
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

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

import static com.kairos.utils.RestClientUrlUtil.getBaseUrl;

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
    public com.kairos.dto.user.staff.StaffDTO getStaff(Long staffId) {

        final String baseUrl = getBaseUrl(true);

        try {
            ParameterizedTypeReference<RestTemplateResponseEnvelope<com.kairos.dto.user.staff.StaffDTO>> typeReference = new ParameterizedTypeReference<RestTemplateResponseEnvelope<com.kairos.dto.user.staff.StaffDTO>>() {
            };
            ResponseEntity<RestTemplateResponseEnvelope<com.kairos.dto.user.staff.StaffDTO>> restExchange =
                    restTemplate.exchange(
                            baseUrl + "/staff/{staffId}",
                            HttpMethod.GET, null, typeReference, staffId);
            RestTemplateResponseEnvelope<com.kairos.dto.user.staff.StaffDTO> response = restExchange.getBody();
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

    public List<UnitStaffResponseDTO> getUnitWiseStaffList() {

        final String baseUrl = getBaseUrl(true);

        try {
            ParameterizedTypeReference<RestTemplateResponseEnvelope<List<UnitStaffResponseDTO>>> typeReference = new ParameterizedTypeReference<RestTemplateResponseEnvelope<List<UnitStaffResponseDTO>>>() {
            };
            ResponseEntity<RestTemplateResponseEnvelope<List<UnitStaffResponseDTO>>> restExchange =
                    restTemplate.exchange(
                            baseUrl + "/staff/unitwise",
                            HttpMethod.GET, null, typeReference);
            RestTemplateResponseEnvelope<List<UnitStaffResponseDTO>> response = restExchange.getBody();
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

    public Map<Long,Long> getUnitPositionExpertiseMap(Long organizationId, Long unitId) {

        final String baseUrl = getBaseUrl(organizationId, unitId, null);

        try {
            ParameterizedTypeReference<RestTemplateResponseEnvelope<Map<Long,Long>>> typeReference = new ParameterizedTypeReference<RestTemplateResponseEnvelope<Map<Long,Long>>>() {
            };
            ResponseEntity<RestTemplateResponseEnvelope<Map<Long,Long>>> restExchange =
                    restTemplate.exchange(
                            baseUrl + "/unit_position/expertise",
                            HttpMethod.GET, null, typeReference);
            RestTemplateResponseEnvelope<Map<Long,Long>> response = restExchange.getBody();
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


    public List<StaffDTO> getStaffListByUnit() {

        final String baseUrl = getBaseUrl(true);

        try {
            ParameterizedTypeReference<RestTemplateResponseEnvelope<List<StaffDTO>>> typeReference = new ParameterizedTypeReference<RestTemplateResponseEnvelope<List<StaffDTO>>>() {
            };
            ResponseEntity<RestTemplateResponseEnvelope<List<StaffDTO>>> restExchange =
                    restTemplate.exchange(
                            baseUrl + "/staff/get_Staff_By_Unit",
                            HttpMethod.GET, null, typeReference);
            RestTemplateResponseEnvelope<List<StaffDTO>> response = restExchange.getBody();
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
    public StaffUnitPositionDetails verifyUnitEmploymentOfStaff(Long staffId,Long unitId, String type) {

        final String baseUrl = getBaseUrl(true,unitId);

        try {
            ParameterizedTypeReference<RestTemplateResponseEnvelope<StaffUnitPositionDetails>> typeReference = new ParameterizedTypeReference<RestTemplateResponseEnvelope<StaffUnitPositionDetails>>() {
            };
            ResponseEntity<RestTemplateResponseEnvelope<StaffUnitPositionDetails>> restExchange =
                    restTemplate.exchange(
                            baseUrl + "/staff/{staffId}/verifyUnitEmployment?type=" + type,
                            HttpMethod.GET, null, typeReference, staffId);
            RestTemplateResponseEnvelope<StaffUnitPositionDetails> response = restExchange.getBody();
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

    public StaffAdditionalInfoDTO verifyUnitEmploymentOfStaff(LocalDate shiftDate,Long staffId, String type, Long unitEmploymentId) {
        final String baseUrl = getBaseUrl(true);
         if(shiftDate==null){
             shiftDate=DateUtils.getCurrentLocalDate();
         }
        try {
            ParameterizedTypeReference<RestTemplateResponseEnvelope<StaffAdditionalInfoDTO>> typeReference = new ParameterizedTypeReference<RestTemplateResponseEnvelope<StaffAdditionalInfoDTO>>() {
            };
            ResponseEntity<RestTemplateResponseEnvelope<StaffAdditionalInfoDTO>> restExchange =
                    restTemplate.exchange(
                            baseUrl + "/staff/{staffId}/verifyUnitEmployment/{unitEmploymentId}?type=" + type+"&shiftDate="+shiftDate,
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
    public com.kairos.dto.user.staff.StaffDTO getStaffByUser(Long userId) {

        final String baseUrl = getBaseUrl(true);

        try {
            ParameterizedTypeReference<RestTemplateResponseEnvelope<com.kairos.dto.user.staff.StaffDTO>> typeReference = new ParameterizedTypeReference<RestTemplateResponseEnvelope<com.kairos.dto.user.staff.StaffDTO>>() {
            };
            ResponseEntity<RestTemplateResponseEnvelope<com.kairos.dto.user.staff.StaffDTO>> restExchange =
                    restTemplate.exchange(
                            baseUrl + "/staff/current_user/{userId}",
                            HttpMethod.GET, null, typeReference, userId);
            RestTemplateResponseEnvelope<com.kairos.dto.user.staff.StaffDTO> response = restExchange.getBody();
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

    public List<com.kairos.dto.user.staff.StaffDTO> getStaffInfo(Long unitId, List<Long> expertiesId){
        final String baseUrl=getBaseUrl(true);

        HttpEntity<List> request = new HttpEntity(expertiesId);
        try {
            ParameterizedTypeReference<RestTemplateResponseEnvelope<List<com.kairos.dto.user.staff.StaffDTO>>> typeReference = new ParameterizedTypeReference<RestTemplateResponseEnvelope<List<com.kairos.dto.user.staff.StaffDTO>>>() {};
            ResponseEntity<RestTemplateResponseEnvelope<List<com.kairos.dto.user.staff.StaffDTO>>> restExchange =
                    restTemplate.exchange(
                            baseUrl + "/staff/getStaffByExperties",
                            HttpMethod.POST,
                            request, typeReference, unitId);
            RestTemplateResponseEnvelope<List<com.kairos.dto.user.staff.StaffDTO>> response = restExchange.getBody();
            if (restExchange.getStatusCode().is2xxSuccessful()) {

                List<com.kairos.dto.user.staff.StaffDTO> staffDTOList =  response.getData();
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
