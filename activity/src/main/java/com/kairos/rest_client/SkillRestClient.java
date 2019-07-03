package com.kairos.rest_client;

import com.kairos.dto.user.organization.skill.Skill;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Set;

import static com.kairos.utils.RestClientUrlUtil.getBaseUrl;

@Component
@Deprecated
public class SkillRestClient {
    private static final Logger logger = LoggerFactory.getLogger(SkillRestClient.class);

    @Autowired
    RestTemplate restTemplate;



    //TODO Needs to be Change to GenricIntegrationService
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


}
