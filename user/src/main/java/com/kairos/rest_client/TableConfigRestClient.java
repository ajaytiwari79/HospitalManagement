package com.kairos.rest_client;

import com.kairos.commons.client.RestTemplateResponseEnvelope;
import com.kairos.dto.activity.activity.TableConfiguration;
import com.kairos.service.exception.ExceptionService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import javax.inject.Inject;

import static com.kairos.rest_client.RestClientURLUtil.getBaseUrl;

@Component
public class TableConfigRestClient {
    @Autowired
    RestTemplate restTemplate;
    @Inject
    private ExceptionService exceptionService;


    private static final Logger logger = LoggerFactory.getLogger(TableConfigRestClient.class);

    /**
     * @auther anil maurya
     *
     * @param staffId
     * @param unitId
     * @return
     */
    public TableConfiguration getTableConfiguration(long unitId, Long staffId){

        final String baseUrl=getBaseUrl(true);

        try {
            ParameterizedTypeReference<RestTemplateResponseEnvelope<TableConfiguration>> typeReference = new ParameterizedTypeReference<RestTemplateResponseEnvelope<TableConfiguration>>(){};
            ResponseEntity<RestTemplateResponseEnvelope<TableConfiguration>> restExchange =
                    restTemplate.exchange(
                            baseUrl + "/table/{staffId}",
                            HttpMethod.
                                    GET,null, typeReference,staffId);

            RestTemplateResponseEnvelope<TableConfiguration> response = restExchange.getBody();
            if (restExchange.getStatusCode().is2xxSuccessful()) {
                return response.getData();
            } else {
                throw new RuntimeException(response.getMessage());
            }
        }catch (HttpClientErrorException e) {

            logger.info("status {}",e.getStatusCode());
            logger.info("response {}",e.getResponseBodyAsString());
            exceptionService.runtimeException("message.exception.taskmicroservice",e.getMessage());

        }
        return null;
    }

}
