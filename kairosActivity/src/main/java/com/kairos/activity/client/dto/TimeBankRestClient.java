package com.kairos.activity.client.dto;

import com.kairos.activity.response.dto.time_bank.UnitPositionWithCtaDetailsDTO;
import com.kairos.activity.util.userContext.UserContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import static com.kairos.activity.util.RestClientUrlUtil.getBaseUrl;

/*
* Created By Pradeep singh rajawat
*  Date-27/01/2018
*
* */

@Service
public class TimeBankRestClient {

    private static final Logger logger = LoggerFactory.getLogger(TimeBankRestClient.class);

    @Autowired
    private RestTemplate restTemplate;

    public UnitPositionWithCtaDetailsDTO getCTAbyUnitEmployementPosition(Long unitPositionId) {
        String baseUrl=new StringBuilder(getBaseUrl(false)).append(UserContext.getOrgId()).append("/unit/").append(UserContext.getUnitId()).toString();

        try {
            ParameterizedTypeReference<RestTemplateResponseEnvelope<UnitPositionWithCtaDetailsDTO>> typeReference = new ParameterizedTypeReference<RestTemplateResponseEnvelope<UnitPositionWithCtaDetailsDTO>>(){};
            ResponseEntity<RestTemplateResponseEnvelope<UnitPositionWithCtaDetailsDTO>> restExchange =
                    restTemplate.exchange(
                            baseUrl + "/getCTAbyUnitPosition/{unitPositionId}",
                            HttpMethod.GET,
                            null, typeReference,unitPositionId);
            RestTemplateResponseEnvelope<UnitPositionWithCtaDetailsDTO> response = restExchange.getBody();
            if (restExchange.getStatusCode().is2xxSuccessful()) {
                return response.getData();
            } else {
                throw new RuntimeException(response.getMessage());
            }
        }catch (HttpClientErrorException e) {
            logger.info("status {}",e.getStatusCode());
            logger.info("response {}",e.getResponseBodyAsString());
            throw new RuntimeException("exception occurred in user micro service "+e.getMessage());
        }
    }

    /*public List<UnitPositionWithCtaDetailsDTO> getCTAbyUnitEmployementPosition() {
        try {
            ParameterizedTypeReference<RestTemplateResponseEnvelope<List<UnitPositionWithCtaDetailsDTO>>> typeReference = new ParameterizedTypeReference<RestTemplateResponseEnvelope<List<UnitPositionWithCtaDetailsDTO>>>(){};
            ResponseEntity<RestTemplateResponseEnvelope<List<UnitPositionWithCtaDetailsDTO>>> restExchange =
                    restTemplate.exchange(
                            "" + "/getAllCTASWithUnitEmployementPositionIds",
                            HttpMethod.GET,
                            null, typeReference);
            RestTemplateResponseEnvelope<List<UnitPositionWithCtaDetailsDTO>> response = restExchange.getBody();
            if (restExchange.getStatusCode().is2xxSuccessful()) {
                return response.getData();
            } else {
                throw new RuntimeException(response.getMessage());
            }
        }catch (HttpClientErrorException e) {
            logger.info("status {}",e.getStatusCode());
            logger.info("response {}",e.getResponseBodyAsString());
            throw new RuntimeException("exception occurred in user micro service "+e.getMessage());
        }
    }*/


}
