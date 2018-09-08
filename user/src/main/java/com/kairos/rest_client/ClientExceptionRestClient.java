package com.kairos.rest_client;

import com.kairos.commons.client.RestTemplateResponseEnvelope;
import com.kairos.dto.user.staff.client.ClientExceptionTypesDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.util.List;

/**
 * Created by prabjot on 13/12/17.
 */
@Component
public class ClientExceptionRestClient {

    private static final Logger logger = LoggerFactory.getLogger(PhaseRestClient.class);

    @Autowired
    RestTemplate restTemplate;

    public List<ClientExceptionTypesDTO> getClientExceptionTypes(){

        final String baseUrl=RestClientURLUtil.getBaseUrl(true);

        try {
            ParameterizedTypeReference<RestTemplateResponseEnvelope<List<ClientExceptionTypesDTO>>> typeReference =
                    new ParameterizedTypeReference<RestTemplateResponseEnvelope<List<ClientExceptionTypesDTO>>>() {};
            ResponseEntity<RestTemplateResponseEnvelope<List<ClientExceptionTypesDTO>>> restExchange =
                    restTemplate.exchange(
                            baseUrl + "/client_exception_type",
                            HttpMethod.GET,
                            null, typeReference);

            RestTemplateResponseEnvelope<List<ClientExceptionTypesDTO>> response = restExchange.getBody();
            if (restExchange.getStatusCode().is2xxSuccessful()) {
                logger.info("RestExchange",restExchange);
                return response.getData();
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
