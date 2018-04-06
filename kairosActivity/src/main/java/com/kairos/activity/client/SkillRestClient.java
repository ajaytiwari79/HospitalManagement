package com.kairos.activity.client;

import com.kairos.activity.client.dto.RestTemplateResponseEnvelope;
import com.kairos.activity.client.dto.skill.Skill;
import com.kairos.activity.util.userContext.UserContext;
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

@Component
public class SkillRestClient {
    private static final Logger logger = LoggerFactory.getLogger(CountryRestClient.class);

    @Autowired
    RestTemplate restTemplate;


    public List<Map<String,Object>> getSkillsOfOrganization(long unitId) {
        final String baseUrl = getBaseUrl(false);
        try {
            ParameterizedTypeReference<RestTemplateResponseEnvelope<List<Map<String,Object>>>> typeReference = new ParameterizedTypeReference<RestTemplateResponseEnvelope< List<Map<String,Object>>>>() {
            };
            ResponseEntity<RestTemplateResponseEnvelope<List<Map<String,Object>>>> restExchange =
                    restTemplate.exchange(
                            baseUrl + "/unit/{unitId}/skills",
                            HttpMethod.GET,
                            null, typeReference,unitId);

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

    public List<Skill> getSkillsByName(Set<String> skills, long countryId){
        final String baseUrl = getBaseUrl(false);
        try {
            HttpEntity<Set<String>> entity = new HttpEntity<>(skills);
            ParameterizedTypeReference<RestTemplateResponseEnvelope<List<Skill>>> typeReference = new ParameterizedTypeReference<RestTemplateResponseEnvelope< List<Skill>>>() {
            };
            ResponseEntity<RestTemplateResponseEnvelope<List<Skill>>> restExchange =
                    restTemplate.exchange(
                            baseUrl + "/country/{countryId}/skills_by_name",
                            HttpMethod.POST,
                            entity, typeReference,countryId);

            RestTemplateResponseEnvelope<List<Skill>> response  = restExchange.getBody();
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





    private final String getBaseUrl(boolean hasUnitInUrl) {
        if (hasUnitInUrl) {
            String baseUrl = new StringBuilder("http://zuulservice/kairos/user/api/v1/organization/").append(UserContext.getOrgId()).append("/unit/").append(UserContext.getUnitId()).toString();
            return baseUrl;
        } else {
            String baseUrl = new StringBuilder("http://zuulservice/kairos/user/api/v1/organization/").append(UserContext.getOrgId()).toString();
            return baseUrl;
        }

    }


}
