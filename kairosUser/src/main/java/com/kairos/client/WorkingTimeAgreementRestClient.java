package com.kairos.client;

import com.kairos.client.dto.RestTemplateResponseEnvelope;
import com.kairos.response.dto.web.wta.WTADTO;
import com.kairos.response.dto.web.wta.WTAResponseDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import javax.inject.Inject;

import java.math.BigInteger;
import java.util.List;

import static com.kairos.client.RestClientURLUtil.getBaseUrl;

/**
 * @author pradeep
 * @date - 21/4/18
 */

public class WorkingTimeAgreementRestClient {

    private static final Logger logger = LoggerFactory.getLogger(WorkingTimeAgreementRestClient.class);

    @Inject
    private RestTemplate restTemplate;

    public List<WTAResponseDTO> getWTAByExpertise(Long expertiseId){
        String baseUrl = getBaseUrl(true);
        try {
            HttpEntity<Long> request = new HttpEntity<>(expertiseId);
            ParameterizedTypeReference<RestTemplateResponseEnvelope<List<WTAResponseDTO>>> typeReference = new ParameterizedTypeReference<RestTemplateResponseEnvelope<List<WTAResponseDTO>>>() {};
            ResponseEntity<RestTemplateResponseEnvelope<List<WTAResponseDTO>>> restExchange =
                    restTemplate.exchange(
                            baseUrl + "/expertise/{expertiseId}/wta",
                            HttpMethod.GET, request, typeReference);

            RestTemplateResponseEnvelope<List<WTAResponseDTO>> response = restExchange.getBody();
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

    public WTAResponseDTO getWTAById(BigInteger wtaId){
        String baseUrl = getBaseUrl(true);
        try {
            HttpEntity<BigInteger> request = new HttpEntity<>(wtaId);
            ParameterizedTypeReference<RestTemplateResponseEnvelope<WTAResponseDTO>> typeReference = new ParameterizedTypeReference<RestTemplateResponseEnvelope<WTAResponseDTO>>() {};
            ResponseEntity<RestTemplateResponseEnvelope<WTAResponseDTO>> restExchange =
                    restTemplate.exchange(
                            baseUrl + "/wta/{wtaId}",
                            HttpMethod.GET, request, typeReference);

            RestTemplateResponseEnvelope<WTAResponseDTO> response = restExchange.getBody();
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

    public WTAResponseDTO assignWTAToUnitPosition(BigInteger wtaId){
        String baseUrl = getBaseUrl(true);
        try {
            HttpEntity<BigInteger> request = new HttpEntity<>(wtaId);
            ParameterizedTypeReference<RestTemplateResponseEnvelope<WTAResponseDTO>> typeReference = new ParameterizedTypeReference<RestTemplateResponseEnvelope<WTAResponseDTO>>() {};
            ResponseEntity<RestTemplateResponseEnvelope<WTAResponseDTO>> restExchange =
                    restTemplate.exchange(
                            baseUrl + "/wta/{wtaId}",
                            HttpMethod.POST, request, typeReference);

            RestTemplateResponseEnvelope<WTAResponseDTO> response = restExchange.getBody();
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

    public WTAResponseDTO updateWTAOfUnitPosition(WTADTO wtadto){
        String baseUrl = getBaseUrl(true);
        try {
            HttpEntity<WTADTO> request = new HttpEntity<>(wtadto);
            ParameterizedTypeReference<RestTemplateResponseEnvelope<WTAResponseDTO>> typeReference = new ParameterizedTypeReference<RestTemplateResponseEnvelope<WTAResponseDTO>>() {};
            ResponseEntity<RestTemplateResponseEnvelope<WTAResponseDTO>> restExchange =
                    restTemplate.exchange(
                            baseUrl + "/wta",
                            HttpMethod.PUT, request, typeReference);

            RestTemplateResponseEnvelope<WTAResponseDTO> response = restExchange.getBody();
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
