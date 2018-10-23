package com.kairos.rest_client;

import com.kairos.dto.user.country.basic_details.CountryDTO;
import com.kairos.dto.user.country.day_type.DayType;
import com.kairos.dto.user.organization.OrganizationTypeHierarchyQueryResult;
import com.kairos.dto.user.organization.TimeSlot;
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
import java.util.Set;

import static com.kairos.utils.RestClientUrlUtil.getBaseUrl;

@Component
@Deprecated
public class CountryRestClient {
    private static final Logger logger = LoggerFactory.getLogger(CountryRestClient.class);

    @Autowired
    RestTemplate restTemplate;

    /**
     * @return
     */
    public Object getAllContractType(long countryId) {
        final String baseUrl = getBaseUrl(false);
        try {
            ParameterizedTypeReference<RestTemplateResponseEnvelope<Object>> typeReference = new ParameterizedTypeReference<RestTemplateResponseEnvelope<Object>>() {
            };
            ResponseEntity<RestTemplateResponseEnvelope<Object>> restExchange =
                    restTemplate.exchange(
                            baseUrl + "/country/{countryId}/contractType",
                            HttpMethod.GET,
                            null, typeReference, countryId);

            RestTemplateResponseEnvelope<Object> response = restExchange.getBody();
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

    public CountryDTO getCountryByOrganizationService(long organizationServiceId) {
        final String baseUrl = getBaseUrl(false);
        try {
            ParameterizedTypeReference<RestTemplateResponseEnvelope<CountryDTO>> typeReference = new ParameterizedTypeReference<RestTemplateResponseEnvelope<CountryDTO>>() {
            };
            ResponseEntity<RestTemplateResponseEnvelope<CountryDTO>> restExchange =
                    restTemplate.exchange(
                            baseUrl + "/country/organizaton_service/{organizationServiceId}",
                            HttpMethod.GET,
                            null, typeReference, organizationServiceId);

            RestTemplateResponseEnvelope<CountryDTO> response = restExchange.getBody();
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

    public CountryDTO getCountryById(long countryId) {
        final String baseUrl = getBaseUrl(false);
        try {
            ParameterizedTypeReference<RestTemplateResponseEnvelope<CountryDTO>> typeReference = new ParameterizedTypeReference<RestTemplateResponseEnvelope<CountryDTO>>() {
            };
            ResponseEntity<RestTemplateResponseEnvelope<CountryDTO>> restExchange =
                    restTemplate.exchange(
                            baseUrl + "/countryId/{countryId}",
                            HttpMethod.GET,
                            null, typeReference, countryId);

            RestTemplateResponseEnvelope<CountryDTO> response = restExchange.getBody();
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


    public List<Map<String,Object>> getSkillsByCountryForTaskType(long countryId) {
        final String baseUrl = getBaseUrl(false);
        try {
            ParameterizedTypeReference<RestTemplateResponseEnvelope<List<Map<String,Object>>>> typeReference = new ParameterizedTypeReference<RestTemplateResponseEnvelope< List<Map<String,Object>>>>() {
            };
            ResponseEntity<RestTemplateResponseEnvelope<List<Map<String,Object>>>> restExchange =
                    restTemplate.exchange(
                            baseUrl + "/country/{countryId}/task_type/skills",
                            HttpMethod.GET,
                            null, typeReference,countryId);

            RestTemplateResponseEnvelope<List<Map<String,Object>>> response  = restExchange.getBody();
            if (restExchange.getStatusCode().is2xxSuccessful()) {
                return response.getData();
            } else {
                throw new RuntimeException(response.getMessage());
            }
        }catch (HttpClientErrorException e){
            logger.info("status {}", e.getStatusCode());
            logger.info("response {}", e.getResponseBodyAsString());
            throw new RuntimeException("exception occurred in user micro service " + e.getMessage());
        }
    }


    public OrganizationTypeHierarchyQueryResult getOrgTypesHierarchy(long countryId, Set<Long> organizationSubTypes) {
        final String baseUrl = getBaseUrl(false);
        try {
            ParameterizedTypeReference<RestTemplateResponseEnvelope<OrganizationTypeHierarchyQueryResult>> typeReference = new ParameterizedTypeReference<RestTemplateResponseEnvelope<OrganizationTypeHierarchyQueryResult>>() {
            };
            HttpEntity<Set> request = new HttpEntity<>(organizationSubTypes);
            ResponseEntity<RestTemplateResponseEnvelope<OrganizationTypeHierarchyQueryResult>> restExchange =
                    restTemplate.exchange(
                            baseUrl + "/country/{countryId}/organization_types/hierarchy",
                            HttpMethod.POST,
                            request, typeReference,countryId);
            RestTemplateResponseEnvelope<OrganizationTypeHierarchyQueryResult> response = restExchange.getBody();
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

    public boolean isCountryExists(long countryId) {
        final String baseUrl = getBaseUrl(false);
        try {
            ParameterizedTypeReference<RestTemplateResponseEnvelope<Boolean>> typeReference = new ParameterizedTypeReference<RestTemplateResponseEnvelope<Boolean>>() {
            };
            ResponseEntity<RestTemplateResponseEnvelope<Boolean>> restExchange =
                    restTemplate.exchange(
                            baseUrl + "/country/{countryId}",
                            HttpMethod.GET,
                            null, typeReference, countryId);

            RestTemplateResponseEnvelope<Boolean> response = restExchange.getBody();
            if (restExchange.getStatusCode().is2xxSuccessful()) {
                logger.info("response ================= : "+response);
                return response.getData();
            } else {
                throw new RuntimeException(response.getMessage());
            }
        } catch (HttpClientErrorException e) {
            logger.info("status {}", e.getStatusCode());
            logger.info("response {}", e.getResponseBodyAsString());
            throw new RuntimeException("exception occurred in user micro service to check if country exists" + e.getMessage());
        }

    }

    public List<DayType> getDayTypes(List<Long> dayTypes) {
        final String baseUrl = getBaseUrl(false);
        try {
            ParameterizedTypeReference<RestTemplateResponseEnvelope<List<DayType>>> typeReference = new ParameterizedTypeReference<RestTemplateResponseEnvelope<List<DayType>>>() {
            };
            HttpEntity<List<Long>> request = new HttpEntity<>(dayTypes);
            ResponseEntity<RestTemplateResponseEnvelope<List<DayType>>> restExchange =
                    restTemplate.exchange(
                            baseUrl + "/day_types",
                            HttpMethod.POST,
                            request, typeReference);
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

    public List<TimeSlot> getTimeSlotSetsOfCountry(Long countryId) {
        final String baseUrl = getBaseUrl(false);
        try {
            ParameterizedTypeReference<RestTemplateResponseEnvelope<List<TimeSlot>>> typeReference = new ParameterizedTypeReference<RestTemplateResponseEnvelope<List<TimeSlot>>>() {
            };
            ResponseEntity<RestTemplateResponseEnvelope<List<TimeSlot>>> restExchange =
                    restTemplate.exchange(
                            baseUrl + "/country/{countryId}/time_slots",
                            HttpMethod.GET,
                            null, typeReference,countryId);
            RestTemplateResponseEnvelope<List<TimeSlot>> response = restExchange.getBody();
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
