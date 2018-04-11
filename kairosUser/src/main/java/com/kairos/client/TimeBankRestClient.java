package com.kairos.client;


import com.kairos.client.dto.RestTemplateResponseEnvelope;
import com.kairos.client.dto.timeBank.TimebankWrapper;
import com.kairos.client.dto.time_bank.UnitPositionWithCtaDetailsDTO;
import com.kairos.util.userContext.UserContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import javax.inject.Inject;

import static com.kairos.client.RestClientURLUtil.getBaseUrl;

@Service
public class TimeBankRestClient {

    private static final Logger logger = LoggerFactory.getLogger(PlannerRestClient.class);

    @Inject
    RestTemplate restTemplate;


    @Async
    public Boolean createBlankTimeBank(UnitPositionWithCtaDetailsDTO unitPositionWithCtaDetailsDTO){
        String baseUrl = getBaseUrl(true);
        try {
            HttpEntity<UnitPositionWithCtaDetailsDTO> request = new HttpEntity<>(unitPositionWithCtaDetailsDTO);
            ParameterizedTypeReference<RestTemplateResponseEnvelope<Boolean>> typeReference = new ParameterizedTypeReference<RestTemplateResponseEnvelope<Boolean>>() {};
            ResponseEntity<RestTemplateResponseEnvelope<Boolean>> restExchange =
                    restTemplate.exchange(
                            baseUrl + "/timeBank/createBlankTimebank",
                            HttpMethod.POST, request, typeReference);

            RestTemplateResponseEnvelope<Boolean> response = restExchange.getBody();
            if (restExchange.getStatusCode().is2xxSuccessful()) {
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

    public Boolean updateBlankTimeBank(UnitPositionWithCtaDetailsDTO unitPositionWithCtaDetailsDTO){
        String baseUrl = getBaseUrl(true);
        try {
            HttpEntity<UnitPositionWithCtaDetailsDTO> request = new HttpEntity<>(unitPositionWithCtaDetailsDTO);
            ParameterizedTypeReference<RestTemplateResponseEnvelope<Boolean>> typeReference = new ParameterizedTypeReference<RestTemplateResponseEnvelope<Boolean>>() {};
            ResponseEntity<RestTemplateResponseEnvelope<Boolean>> restExchange =
                    restTemplate.exchange(
                            baseUrl + "/timeBank/updateBlankTimebank",
                            HttpMethod.PUT, request, typeReference);

            RestTemplateResponseEnvelope<Boolean> response = restExchange.getBody();
            if (restExchange.getStatusCode().is2xxSuccessful()) {
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
