package com.kairos.client;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import com.kairos.client.dto.RestTemplateResponseEnvelope;
import com.kairos.util.userContext.UserContext;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.HttpClientErrorException;
import static com.kairos.client.RestClientURLUtil.getBaseUrl;

/**
 * Created by prerna on 6/4/18.
 */
@Component
public class PeriodRestClient {
    private static final Logger logger = LoggerFactory.getLogger(PhaseRestClient.class);

    @Autowired
    RestTemplate restTemplate;

    public void createDefaultPeriodSettings (Long unitId){

        final String baseUrl=getBaseUrl(false);

        try {
            ParameterizedTypeReference<RestTemplateResponseEnvelope<Boolean>> typeReference =
                    new ParameterizedTypeReference<RestTemplateResponseEnvelope<Boolean>>() {};
            ResponseEntity<RestTemplateResponseEnvelope<Boolean>> restExchange =
                    restTemplate.exchange(
                            baseUrl + "/unit/{unitId}/period_setting",
                            HttpMethod.POST,
                            null, typeReference, unitId);

            RestTemplateResponseEnvelope<Boolean> response = restExchange.getBody();
            if (restExchange.getStatusCode().is2xxSuccessful()) {
                logger.info("RestExchange",restExchange);
            } else {
                throw new RuntimeException(response.getMessage());
            }
        }catch (HttpClientErrorException e) {

            logger.info("status {}",e.getStatusCode());
            logger.info("response {}",e.getResponseBodyAsString());
            throw new RuntimeException("exception occurred in task micro service "+e.getMessage());
        }

    }

}
