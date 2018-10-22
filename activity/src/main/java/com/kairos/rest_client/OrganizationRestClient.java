package com.kairos.rest_client;

import com.kairos.dto.activity.activity.activity_tabs.OrganizationMappingActivityDTO;
import com.kairos.dto.activity.planned_time_type.PresenceTypeWithTimeTypeDTO;
import com.kairos.dto.user.country.day_type.DayType;
import com.kairos.dto.user.organization.OrganizationDTO;
import com.kairos.dto.user.organization.OrganizationSkillAndOrganizationTypesDTO;
import com.kairos.dto.user.organization.OrganizationTypeAndSubTypeDTO;
import com.kairos.dto.user.staff.OrganizationStaffWrapper;
import com.kairos.utils.RestClientUrlUtil;
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

import java.text.SimpleDateFormat;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Component
public class OrganizationRestClient {
    private static final Logger logger = LoggerFactory.getLogger(OrganizationRestClient.class);

    @Autowired
    RestTemplate restTemplate;
    @Autowired
    @Qualifier(value = "restTemplateWithoutAuth")
    RestTemplate restTemplateWithAuth;


    /**
     * this method is used to get an organization, unit id will
     * be used from url
     *
     * @return
     * @auther anil maurya
     * <p>
     * enpoint map in organization controller
     */
    public OrganizationDTO getOrganization(long unitId) {

        final String baseUrl = RestClientUrlUtil.getBaseUrl(true);

        try {
            ParameterizedTypeReference<RestTemplateResponseEnvelope<OrganizationDTO>> typeReference = new ParameterizedTypeReference<RestTemplateResponseEnvelope<OrganizationDTO>>() {
            };
            ResponseEntity<RestTemplateResponseEnvelope<OrganizationDTO>> restExchange =
                    restTemplate.exchange(
                            baseUrl ,
                            HttpMethod.GET, null, typeReference);
            RestTemplateResponseEnvelope<OrganizationDTO> response = restExchange.getBody();
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

    public OrganizationDTO getOrganizationWithCountryId(long unitId) {

        final String baseUrl = RestClientUrlUtil.getBaseUrl(false);

        try {
            ParameterizedTypeReference<RestTemplateResponseEnvelope<OrganizationDTO>> typeReference = new ParameterizedTypeReference<RestTemplateResponseEnvelope<OrganizationDTO>>() {
            };
            ResponseEntity<RestTemplateResponseEnvelope<OrganizationDTO>> restExchange =
                    restTemplate.exchange(
                            baseUrl + "/unit/{unitId}/getOrganisationWithCountryId",
                            HttpMethod.GET, null, typeReference, unitId);
            RestTemplateResponseEnvelope<OrganizationDTO> response = restExchange.getBody();
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
     * @param unitId
     * @return
     */
    public Map<String, Object> getCommonDataOfOrganization(Long unitId) {

        final String baseUrl = RestClientUrlUtil.getBaseUrl(false);

        try {
            ParameterizedTypeReference<RestTemplateResponseEnvelope<Map<String, Object>>> typeReference = new ParameterizedTypeReference<RestTemplateResponseEnvelope<Map<String, Object>>>() {
            };
            ResponseEntity<RestTemplateResponseEnvelope<Map<String, Object>>> restExchange =
                    restTemplate.exchange(
                            baseUrl + "/unit/{unitId}/common_data",
                            HttpMethod.GET,
                            null, typeReference, unitId);
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


    public boolean setOneTimeSyncPerformed(long unitId) {
        final String baseUrl = RestClientUrlUtil.getBaseUrl(false);
        try {
            ParameterizedTypeReference<RestTemplateResponseEnvelope<Boolean>> typeReference = new ParameterizedTypeReference<RestTemplateResponseEnvelope<Boolean>>() {
            };
            ResponseEntity<RestTemplateResponseEnvelope<Boolean>> restExchange =
                    restTemplate.exchange(
                            baseUrl + "/unit/{unitId}/one_time_sync",
                            HttpMethod.PUT,
                            null, typeReference, unitId);
            RestTemplateResponseEnvelope<Boolean> response = restExchange.getBody();
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

    public boolean updateAutoGenerateTaskSettings(long unitId) {

        final String baseUrl = RestClientUrlUtil.getBaseUrl(false);
        try {
            ParameterizedTypeReference<RestTemplateResponseEnvelope<Boolean>> typeReference = new ParameterizedTypeReference<RestTemplateResponseEnvelope<Boolean>>() {
            };
            ResponseEntity<RestTemplateResponseEnvelope<Boolean>> restExchange =
                    restTemplate.exchange(
                            baseUrl + "/unit/{unitId}/auto_generate_task_settings",
                            HttpMethod.PUT,
                            null, typeReference, unitId);
            RestTemplateResponseEnvelope<Boolean> response = restExchange.getBody();
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


    public Map<String, Object> getTimeSlotByUnitIdAndTimeSlotName(Long unitId, Long timeSlotExternalId) {

        final String baseUrl = RestClientUrlUtil.getBaseUrl(false);

        try {
            ParameterizedTypeReference<RestTemplateResponseEnvelope<Map<String, Object>>> typeReference = new ParameterizedTypeReference<RestTemplateResponseEnvelope<Map<String, Object>>>() {
            };

            HttpEntity<Long> entity = new HttpEntity<>(timeSlotExternalId);

            ResponseEntity<RestTemplateResponseEnvelope<Map<String, Object>>> restExchange =
                    restTemplateWithAuth.exchange(
                            baseUrl + "/unit/{unitId}/time_slot_name",
                            HttpMethod.POST,
                            entity, typeReference, unitId);
            System.out.println("base url rest temp   " + restTemplateWithAuth.toString());
            RestTemplateResponseEnvelope<Map<String, Object>> response = restExchange.getBody();
            if (restExchange.getStatusCode().is2xxSuccessful()) {
                Map<String, Object> timeSlots = response.getData();

                return timeSlots;
            } else {
                throw new RuntimeException(response.getMessage());
            }
        } catch (HttpClientErrorException e) {
            logger.info("status {}", e.getStatusCode());
            logger.info("response {}", e.getResponseBodyAsString());
            throw new RuntimeException("exception occurred in user micro service " + e.getMessage());
        }
    }


    public OrganizationDTO getOrganizationByTeamId(Long teamId) {
        //OrganizationDTO unit = organizationGraphRepository.getOrganizationByTeamId(id);
        final String baseUrl = RestClientUrlUtil.getBaseUrl(false);

        try {
            ParameterizedTypeReference<RestTemplateResponseEnvelope<OrganizationDTO>> typeReference = new ParameterizedTypeReference<RestTemplateResponseEnvelope<OrganizationDTO>>() {
            };
            ResponseEntity<RestTemplateResponseEnvelope<OrganizationDTO>> restExchange =
                    restTemplate.exchange(
                            baseUrl + "/getOrganizationByTeamId/{teamId}",
                            HttpMethod.GET, null, typeReference, teamId);
            RestTemplateResponseEnvelope<OrganizationDTO> response = restExchange.getBody();
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


    public OrganizationDTO getParentOrganizationOfCityLevel(Long unitId) {
        //organizationGraphRepository.getParentOrganizationOfCityLevel(organization.getId());
        final String baseUrl = RestClientUrlUtil.getBaseUrl(false);

        try {
            ParameterizedTypeReference<RestTemplateResponseEnvelope<OrganizationDTO>> typeReference = new ParameterizedTypeReference<RestTemplateResponseEnvelope<OrganizationDTO>>() {
            };
            ResponseEntity<RestTemplateResponseEnvelope<OrganizationDTO>> restExchange =
                    restTemplate.exchange(
                            baseUrl + "/unit/{unitId}/getParentOrganizationOfCityLevel",
                            HttpMethod.GET, null, typeReference, unitId);
            RestTemplateResponseEnvelope<OrganizationDTO> response = restExchange.getBody();
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

    public OrganizationDTO getParentOfOrganization(Long unitId) {
        //parent = organizationGraphRepository.getParentOfOrganization(organization.getId());
        final String baseUrl = RestClientUrlUtil.getBaseUrl(false);

        try {
            ParameterizedTypeReference<RestTemplateResponseEnvelope<OrganizationDTO>> typeReference = new ParameterizedTypeReference<RestTemplateResponseEnvelope<OrganizationDTO>>() {
            };
            ResponseEntity<RestTemplateResponseEnvelope<OrganizationDTO>> restExchange =
                    restTemplate.exchange(
                            baseUrl + "/unit/{unitId}/getParentOfOrganization",
                            HttpMethod.GET, null, typeReference, unitId);
            RestTemplateResponseEnvelope<OrganizationDTO> response = restExchange.getBody();
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

    public OrganizationStaffWrapper getOrganizationAndStaffByExternalId(String externalId, String staffTimeCare, String staffTimeCareEmploymentId) {
        final String baseUrl = RestClientUrlUtil.getBaseUrl(false);
        try {
            ParameterizedTypeReference<RestTemplateResponseEnvelope<OrganizationStaffWrapper>> typeReference = new ParameterizedTypeReference<RestTemplateResponseEnvelope<OrganizationStaffWrapper>>() {
            };
            ResponseEntity<RestTemplateResponseEnvelope<OrganizationStaffWrapper>> restExchange =
                    restTemplate.exchange(
                            baseUrl + "/external/{externalId}/?staffTimeCareId=" + staffTimeCare + "&staffTimeCareEmploymentId=" + staffTimeCareEmploymentId,
                            HttpMethod.GET,
                            null, typeReference, externalId);
            RestTemplateResponseEnvelope<OrganizationStaffWrapper> response = restExchange.getBody();
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


    public boolean isExistOrganization(Long orgId) {
        return true;
    }

    public Map<String, Object> getTaskDemandSupplierInfo(Long supplierId) {

        final String baseUrl = RestClientUrlUtil.getBaseUrl(true);

        try {
            ParameterizedTypeReference<RestTemplateResponseEnvelope<Map<String, Object>>> typeReference = new ParameterizedTypeReference<RestTemplateResponseEnvelope<Map<String, Object>>>() {
            };


            ResponseEntity<RestTemplateResponseEnvelope<Map<String, Object>>> restExchange =
                    restTemplate.exchange(
                            baseUrl + "/getTaskDemandSupplierInfo",
                            HttpMethod.GET,
                            null, typeReference, supplierId);


            RestTemplateResponseEnvelope<Map<String, Object>> response = restExchange.getBody();
            if (restExchange.getStatusCode().is2xxSuccessful()) {

                Map<String, Object> taskDemandSupplierInfo = response.getData();
                return taskDemandSupplierInfo;
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
     * @param organizationId
     * @param unitId
     * @return
     * @auther anil maurya
     * map endpoints on organization controller
     */
    public Map<String, Object> getUnitVisitationInfo(long organizationId, long unitId) {

        final String baseUrl = RestClientUrlUtil.getBaseUrl(true);

        try {
            ParameterizedTypeReference<RestTemplateResponseEnvelope<Map<String, Object>>> typeReference = new ParameterizedTypeReference<RestTemplateResponseEnvelope<Map<String, Object>>>() {
            };
            ResponseEntity<RestTemplateResponseEnvelope<Map<String, Object>>> restExchange =
                    restTemplate.exchange(
                            baseUrl + "/unit_visitation",
                            HttpMethod.GET,
                            null, typeReference);
            RestTemplateResponseEnvelope<Map<String, Object>> response = restExchange.getBody();
            if (restExchange.getStatusCode().is2xxSuccessful()) {

                Map<String, Object> unitVisitationInfo = response.getData();
                return unitVisitationInfo;
            } else {
                throw new RuntimeException(response.getMessage());
            }
        } catch (HttpClientErrorException e) {
            logger.info("status {}", e.getStatusCode());
            logger.info("response {}", e.getResponseBodyAsString());
            throw new RuntimeException("exception occurred in user micro service " + e.getMessage());
        }
    }


    public boolean verifyOrganizationExpertizeAndRegions(OrganizationMappingActivityDTO organizationMappingActivityDTO) {

        final String baseUrl = RestClientUrlUtil.getBaseUrl(false);
        HttpEntity<OrganizationMappingActivityDTO> request = new HttpEntity<>(organizationMappingActivityDTO);
        try {
            ParameterizedTypeReference<RestTemplateResponseEnvelope<Boolean>> typeReference = new ParameterizedTypeReference<RestTemplateResponseEnvelope<Boolean>>() {
            };
            ResponseEntity<RestTemplateResponseEnvelope<Boolean>> restExchange =
                    restTemplate.exchange(
                            baseUrl + "/verifyOrganizationExpertise",
                            HttpMethod.POST,
                            request, typeReference);
            RestTemplateResponseEnvelope<Boolean> response = restExchange.getBody();
            if (restExchange.getStatusCode().is2xxSuccessful()) {
                Boolean unitVisitationInfo = response.getData();
                return unitVisitationInfo;
            } else {
                throw new RuntimeException(response.getMessage());
            }
        } catch (HttpClientErrorException e) {
            logger.info("status {}", e.getStatusCode());
            logger.info("response {}", e.getResponseBodyAsString());
            throw new RuntimeException("exception occurred in user micro service " + e.getMessage());
        }
    }

    public List<OrganizationDTO> getOrganizationsByOrganizationType(long organizationTypeId) {
        final String baseUrl = RestClientUrlUtil.getBaseUrl(false);
        try {
            ParameterizedTypeReference<RestTemplateResponseEnvelope<List<OrganizationDTO>>> typeReference = new ParameterizedTypeReference<RestTemplateResponseEnvelope<List<OrganizationDTO>>>() {
            };
            ResponseEntity<RestTemplateResponseEnvelope<List<OrganizationDTO>>> restExchange =
                    restTemplate.exchange(
                            baseUrl + "/organization_type/{organizationTypeId}/organizations",
                            HttpMethod.GET,
                            null, typeReference, organizationTypeId);
            RestTemplateResponseEnvelope<List<OrganizationDTO>> response = restExchange.getBody();
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

    public OrganizationTypeAndSubTypeDTO getOrganizationTypeAndSubTypeByUnitId(long unitId, String type) {
        final String baseUrl = RestClientUrlUtil.getBaseUrl(false);
        try {
            ParameterizedTypeReference<RestTemplateResponseEnvelope<OrganizationTypeAndSubTypeDTO>> typeReference = new ParameterizedTypeReference<RestTemplateResponseEnvelope<OrganizationTypeAndSubTypeDTO>>() {
            };
            ResponseEntity<RestTemplateResponseEnvelope<OrganizationTypeAndSubTypeDTO>> restExchange =
                    restTemplate.exchange(
                            baseUrl + "/unit/{unitId}/organizationTypeAndSubTypes?type=" + type,
                            HttpMethod.GET,
                            null, typeReference, unitId);
            RestTemplateResponseEnvelope<OrganizationTypeAndSubTypeDTO> response = restExchange.getBody();
            if (restExchange.getStatusCode().is2xxSuccessful()) {
                return response.getData();
            } else {
                throw new RuntimeException(response.getMessage());
            }
        } catch (Exception e) {
            // logger.info("status {}",e.getStatusCode());
            //logger.info("response {}",e.getResponseBodyAsString());
            throw new RuntimeException("exception occurred in user micro service " + e.getMessage());
        }
    }


    public Boolean linkOrganizationTypeWithService(Set<Long> organizationTypes, long organizationServiceId) {
        final String baseUrl = RestClientUrlUtil.getBaseUrl(false);
        HttpEntity<Set<Long>> request = new HttpEntity<>(organizationTypes);
        try {
            ParameterizedTypeReference<RestTemplateResponseEnvelope<Boolean>> typeReference = new ParameterizedTypeReference<RestTemplateResponseEnvelope<Boolean>>() {
            };
            ResponseEntity<RestTemplateResponseEnvelope<Boolean>> restExchange =
                    restTemplate.exchange(
                            baseUrl + "/organization_service/{organizationServiceId}/assign/organizationTypes",
                            HttpMethod.POST,
                            request, typeReference, organizationServiceId);
            RestTemplateResponseEnvelope<Boolean> response = restExchange.getBody();
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

    public Boolean deleteLinkingOfOrganizationTypeAndService(Set<Long> organizationTypes, long organizationServiceId) {

        final String baseUrl = RestClientUrlUtil.getBaseUrl(false);
        HttpEntity<Set<Long>> request = new HttpEntity<>(organizationTypes);
        try {
            ParameterizedTypeReference<RestTemplateResponseEnvelope<Boolean>> typeReference = new ParameterizedTypeReference<RestTemplateResponseEnvelope<Boolean>>() {
            };
            ResponseEntity<RestTemplateResponseEnvelope<Boolean>> restExchange =
                    restTemplate.exchange(
                            baseUrl + "/organization_service/{organizationServiceId}/detach/organizationTypes",
                            HttpMethod.DELETE,
                            request, typeReference, organizationServiceId);
            RestTemplateResponseEnvelope<Boolean> response = restExchange.getBody();
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

    //TODO  Add custom Auth to fix this
    public OrganizationDTO getOrganizationWithoutAuth(long unitId) {

        final String baseUrl = RestClientUrlUtil.getBaseUrl(false);

        try {
            ParameterizedTypeReference<RestTemplateResponseEnvelope<OrganizationDTO>> typeReference = new ParameterizedTypeReference<RestTemplateResponseEnvelope<OrganizationDTO>>() {
            };
            ResponseEntity<RestTemplateResponseEnvelope<OrganizationDTO>> restExchange =
                    restTemplate.exchange(
                            baseUrl + "/unit/{unitId}/WithoutAuth",
                            HttpMethod.GET, null, typeReference, unitId);
            RestTemplateResponseEnvelope<OrganizationDTO> response = restExchange.getBody();
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


    public OrganizationSkillAndOrganizationTypesDTO getOrganizationSkillOrganizationSubTypeByUnitId(long unitId) {
        final String baseUrl = RestClientUrlUtil.getBaseUrl(false);
        try {
            ParameterizedTypeReference<RestTemplateResponseEnvelope<OrganizationSkillAndOrganizationTypesDTO>> typeReference = new ParameterizedTypeReference<RestTemplateResponseEnvelope<OrganizationSkillAndOrganizationTypesDTO>>() {
            };
            ResponseEntity<RestTemplateResponseEnvelope<OrganizationSkillAndOrganizationTypesDTO>> restExchange =
                    restTemplate.exchange(
                            baseUrl + "/unit/{unitId}/skill/orgTypes",
                            HttpMethod.GET,
                            null, typeReference, unitId);

            RestTemplateResponseEnvelope<OrganizationSkillAndOrganizationTypesDTO> response = restExchange.getBody();
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

    public List<DayType> getDayType(Date date) {
        final String baseUrl = RestClientUrlUtil.getBaseUrl(true);
        SimpleDateFormat dateFormatter = new SimpleDateFormat("yyyy-MM-dd");
        String dateString = dateFormatter.format(date);
        try {
            ParameterizedTypeReference<RestTemplateResponseEnvelope<List<DayType>>> typeReference = new ParameterizedTypeReference<RestTemplateResponseEnvelope<List<DayType>>>() {
            };
            ResponseEntity<RestTemplateResponseEnvelope<List<DayType>>> restExchange =
                    restTemplate.exchange(
                            baseUrl + "/dayTypebydate?date=" + dateString,
                            HttpMethod.GET,
                            null, typeReference);
            RestTemplateResponseEnvelope<List<DayType>> response = restExchange.getBody();

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

    public List<DayType> getDayTypes(Long unitId) {
        final String baseUrl = RestClientUrlUtil.getBaseUrl(false);
        try {
            ParameterizedTypeReference<RestTemplateResponseEnvelope<List<DayType>>> typeReference = new ParameterizedTypeReference<RestTemplateResponseEnvelope<List<DayType>>>() {
            };
            ResponseEntity<RestTemplateResponseEnvelope<List<DayType>>> restExchange =
                    restTemplate.exchange(
                            baseUrl + "/unit/{unitId}/dayType",
                            HttpMethod.GET,
                            null, typeReference, unitId);
            RestTemplateResponseEnvelope<List<DayType>> response = restExchange.getBody();

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

    public List<DayType> getDayTypesByCountryId(Long countryId) {
        final String baseUrl = RestClientUrlUtil.getBaseUrl(false);
        try {
            ParameterizedTypeReference<RestTemplateResponseEnvelope<List<DayType>>> typeReference = new ParameterizedTypeReference<RestTemplateResponseEnvelope<List<DayType>>>() {
            };
            ResponseEntity<RestTemplateResponseEnvelope<List<DayType>>> restExchange =
                    restTemplate.exchange(
                            baseUrl + "/country/{countryId}/dayType",
                            HttpMethod.GET,
                            null, typeReference, countryId);
            RestTemplateResponseEnvelope<List<DayType>> response = restExchange.getBody();

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

    public boolean showCountryTagForOrganization(long unitId) {
        final String baseUrl = RestClientUrlUtil.getBaseUrl(false);
        try {
            ParameterizedTypeReference<RestTemplateResponseEnvelope<Boolean>> typeReference = new ParameterizedTypeReference<RestTemplateResponseEnvelope<Boolean>>() {
            };
            ResponseEntity<RestTemplateResponseEnvelope<Boolean>> restExchange =
                    restTemplate.exchange(
                            baseUrl + "/unit/" + unitId + "/show_country_tags",
                            HttpMethod.GET,
                            null, typeReference, unitId);

            RestTemplateResponseEnvelope<Boolean> response = restExchange.getBody();
            if (restExchange.getStatusCode().is2xxSuccessful()) {
                logger.info("response ================= : " + response);
                return response.getData();
            } else {
                throw new RuntimeException(response.getMessage());
            }
        } catch (HttpClientErrorException e) {
            logger.info("status {}", e.getStatusCode());
            logger.info("response {}", e.getResponseBodyAsString());
            throw new RuntimeException("exception occurred in user micro service to get check for show country tags" + e.getMessage());
        }

    }

    public PresenceTypeWithTimeTypeDTO getPresenceTypeAndTimeType(long unitId) {
        final String baseUrl = RestClientUrlUtil.getBaseUrl(false);
        try {
            ParameterizedTypeReference<RestTemplateResponseEnvelope<PresenceTypeWithTimeTypeDTO>> typeReference
                    = new ParameterizedTypeReference<RestTemplateResponseEnvelope<PresenceTypeWithTimeTypeDTO>>() {
            };
            ResponseEntity<RestTemplateResponseEnvelope<PresenceTypeWithTimeTypeDTO>> restExchange =
                    restTemplate.exchange(
                            baseUrl + "/unit/{unitId}/timetype_presencetype",
                            HttpMethod.GET, null, typeReference, unitId);
            RestTemplateResponseEnvelope<PresenceTypeWithTimeTypeDTO> response = restExchange.getBody();
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

    public Long getCountryIdOfOrganization(long unitId) {
        final String baseUrl = RestClientUrlUtil.getBaseUrl(false);
        try {
            ParameterizedTypeReference<RestTemplateResponseEnvelope<Long>> typeReference
                    = new ParameterizedTypeReference<RestTemplateResponseEnvelope<Long>>() {
            };
            ResponseEntity<RestTemplateResponseEnvelope<Long>> restExchange =
                    restTemplate.exchange(
                            baseUrl + "/unit/{unitId}/countryId",
                            HttpMethod.GET, null, typeReference, unitId);
            RestTemplateResponseEnvelope<Long> response = restExchange.getBody();
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

    // To get organization id by team id
    public Long getOrganizationIdByTeam(long teamId) {
        final String baseUrl = RestClientUrlUtil.getBaseUrl(true);
        try {
            ParameterizedTypeReference<RestTemplateResponseEnvelope<Long>> typeReference
                    = new ParameterizedTypeReference<RestTemplateResponseEnvelope<Long>>() {
            };
            ResponseEntity<RestTemplateResponseEnvelope<Long>> restExchange =
                    restTemplate.exchange(
                            baseUrl + "/team/organizationId",
                            HttpMethod.GET, null, typeReference, teamId);
            RestTemplateResponseEnvelope<Long> response = restExchange.getBody();
            if (restExchange.getStatusCode().is2xxSuccessful()) {
                return response.getData();
            } else {
                throw new RuntimeException(response.getMessage());
            }
        } catch (HttpClientErrorException e) {
            logger.info("status {}", e.getStatusCode());
            logger.info("response {}", e.getResponseBodyAsString());
            throw new RuntimeException("exception occurred in user micro service to get organization id by team id" + e.getMessage());
        }
    }

    public ZoneId getOrganizationTimeZone(long unitId) {
        final String baseUrl = RestClientUrlUtil.getBaseUrl(true);
        try {
            ParameterizedTypeReference<RestTemplateResponseEnvelope<ZoneId>> typeReference
                    = new ParameterizedTypeReference<RestTemplateResponseEnvelope<ZoneId>>() {
            };
            ResponseEntity<RestTemplateResponseEnvelope<ZoneId>> restExchange =
                    restTemplate.exchange(
                            baseUrl + "/time_zone",
                            HttpMethod.GET, null, typeReference, unitId);
            RestTemplateResponseEnvelope<ZoneId> response = restExchange.getBody();
            if (restExchange.getStatusCode().is2xxSuccessful()) {
                return response.getData();
            } else {
                throw new RuntimeException(response.getMessage());
            }
        } catch (HttpClientErrorException e) {
            logger.info("status {}", e.getStatusCode());
            logger.info("response {}", e.getResponseBodyAsString());
            throw new RuntimeException("exception occurred in user micro service to get organization Time Zone" + e.getMessage());
        }
    }

    public PresenceTypeWithTimeTypeDTO getPresenceTypeAndTimeTypeByCountry(Long countryId) {
        final String baseUrl = RestClientUrlUtil.getBaseUrl(false);
        try {
            ParameterizedTypeReference<RestTemplateResponseEnvelope<PresenceTypeWithTimeTypeDTO>> typeReference
                    = new ParameterizedTypeReference<RestTemplateResponseEnvelope<PresenceTypeWithTimeTypeDTO>>() {
            };
            ResponseEntity<RestTemplateResponseEnvelope<PresenceTypeWithTimeTypeDTO>> restExchange =
                    restTemplate.exchange(
                            baseUrl + "/country/{countryId}/presenceTypeWithTimeType",
                            HttpMethod.GET, null, typeReference, countryId);
            RestTemplateResponseEnvelope<PresenceTypeWithTimeTypeDTO> response = restExchange.getBody();
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