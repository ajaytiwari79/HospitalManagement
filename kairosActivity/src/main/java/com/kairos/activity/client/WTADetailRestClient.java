package com.kairos.activity.client;

import com.kairos.activity.client.dto.RestTemplateResponseEnvelope;
import com.kairos.activity.util.userContext.UserContext;
import com.kairos.response.dto.web.wta.WTABasicDetailsDTO;
import com.sun.javafx.fxml.builder.URLBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import javax.inject.Inject;

/**
 * @author pradeep
 * @date - 11/4/18
 */

@Service
public class WTADetailRestClient {

    private static final Logger logger = LoggerFactory.getLogger(StaffRestClient.class);


    @Inject
    private RestTemplate restTemplate;

    public WTABasicDetailsDTO getWtaRelatedInfo(Long expertiseId,Long organizationSubTypeId,Long countryId,Long organizationId) {
         StringBuffer baseUrl = new StringBuffer(getBaseUrl(true));
         baseUrl.append("/WTARelatedInfo?").append("countryId").append(countryId).append("&organizationId").append(organizationId).append("&organizationSubTypeId").append(organizationSubTypeId).append("&expertiseId").append(expertiseId);

        try {
            ParameterizedTypeReference<RestTemplateResponseEnvelope<WTABasicDetailsDTO>> typeReference = new ParameterizedTypeReference<RestTemplateResponseEnvelope<WTABasicDetailsDTO>>() {
            };
            ResponseEntity<RestTemplateResponseEnvelope<WTABasicDetailsDTO>> restExchange =
                    restTemplate.exchange(
                            baseUrl.toString(),
                            HttpMethod.GET, null, typeReference);
            RestTemplateResponseEnvelope<WTABasicDetailsDTO> response = restExchange.getBody();
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


    private final String getBaseUrl(boolean hasUnitInUrl){
        if(hasUnitInUrl){
            String baseUrl=new StringBuilder("http://zuulservice/kairos/user/api/v1/organization/").append(UserContext.getOrgId()).append("/unit/").append(UserContext.getUnitId()).toString();
            return baseUrl;
        } else {
            String baseUrl = new StringBuilder("http://zuulservice/kairos/user/api/v1/organization/").append(UserContext.getOrgId()).toString();
            return baseUrl;
        }

    }


}
