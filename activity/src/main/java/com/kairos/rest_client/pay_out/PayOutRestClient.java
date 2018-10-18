package com.kairos.rest_client.pay_out;

import com.kairos.dto.activity.pay_out.UnitPositionWithCtaDetailsDTO;
import com.kairos.rest_client.RestTemplateResponseEnvelope;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import static com.kairos.utils.RestClientUrlUtil.getBaseUrl;

/*
* Created By Pradeep singh rajawat
*  Date-27/01/2018
*
* */

@Service
@Deprecated
public class PayOutRestClient {

    private static final Logger logger = LoggerFactory.getLogger(PayOutRestClient.class);

    @Autowired
    private RestTemplate restTemplate;

    public UnitPositionWithCtaDetailsDTO getCTAbyUnitEmployementPosition(Long unitPositionId) {
        String baseUrl=getBaseUrl(true);

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

}
