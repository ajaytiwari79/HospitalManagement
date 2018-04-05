package com.kairos.service.country;


import com.kairos.client.dto.RestTemplateResponseEnvelope;
import com.kairos.custom_exception.DataNotFoundByIdException;
import com.kairos.persistence.model.timetype.PresenceTypeDTO;
import com.kairos.persistence.model.timetype.TimeTypeDTO;
import com.kairos.persistence.model.user.country.Country;
import com.kairos.persistence.repository.user.country.CountryGraphRepository;
import com.kairos.persistence.repository.user.country.TimeTypeGraphRepository;
import com.kairos.service.UserBaseService;
import com.kairos.util.userContext.UserContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import javax.inject.Inject;
import java.util.List;
import java.util.Optional;

/**
 * Created by vipul on 17/10/17.
 */
@Service
public class TimeTypeRestClient {
    private Logger logger = LoggerFactory.getLogger(TimeTypeRestClient.class);
    @Inject
    private RestTemplate restTemplate;

    public List<TimeTypeDTO> getAllTimeTypes(Long countryId) {
        final String baseUrl = getBaseUrl(false);
        try {
            ParameterizedTypeReference<RestTemplateResponseEnvelope<List<TimeTypeDTO>>> typeReference =
                    new ParameterizedTypeReference<RestTemplateResponseEnvelope<List<TimeTypeDTO>>>() {
                    };
            ResponseEntity<RestTemplateResponseEnvelope<List<TimeTypeDTO>>> restExchange =
                    restTemplate.exchange(
                            baseUrl + "/country/{countryId}/timeType/",
                            HttpMethod.GET,
                            null, typeReference,countryId);

            RestTemplateResponseEnvelope<List<TimeTypeDTO>> response = restExchange.getBody();
            if (restExchange.getStatusCode().is2xxSuccessful()) {
                logger.info("RestExchange", restExchange);
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

    private final String getBaseUrl(boolean hasUnitInUrl) {
        if (hasUnitInUrl) {
            String baseUrl = new StringBuilder("http://zuulservice/kairos/activity/api/v1/organization/").append(UserContext.getOrgId()).append("/unit/").append(UserContext.getUnitId()).toString();
            return baseUrl;
        } else {
            String baseUrl = new StringBuilder("http://zuulservice/kairos/activity/api/v1/organization/").append(UserContext.getOrgId()).toString();
            return baseUrl;
        }

    }


}
