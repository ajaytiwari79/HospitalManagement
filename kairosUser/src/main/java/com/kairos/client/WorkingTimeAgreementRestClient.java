package com.kairos.client;

import com.kairos.client.dto.RestTemplateResponseEnvelope;
import com.kairos.response.dto.web.wta.WTADTO;
import com.kairos.response.dto.web.wta.WTAResponseDTO;
import com.kairos.util.userContext.UserContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
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

@Service
public class WorkingTimeAgreementRestClient {

    private static final Logger logger = LoggerFactory.getLogger(WorkingTimeAgreementRestClient.class);

    @Inject
    private RestTemplate restTemplate;

    public List<WTAResponseDTO> getWTAByExpertise(Long expertiseId){
        String baseUrl = getBaseUrl(true);
        try {
            //HttpEntity<Long> request = new HttpEntity<>(expertiseId);
            ParameterizedTypeReference<RestTemplateResponseEnvelope<List<WTAResponseDTO>>> typeReference = new ParameterizedTypeReference<RestTemplateResponseEnvelope<List<WTAResponseDTO>>>() {};
            ResponseEntity<RestTemplateResponseEnvelope<List<WTAResponseDTO>>> restExchange =
                    restTemplate.exchange(
                            baseUrl + "/expertise/{expertiseId}/wta",
                            HttpMethod.GET, null, typeReference,expertiseId);

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
           // HttpEntity<BigInteger> request = new HttpEntity<>(wtaId);
            ParameterizedTypeReference<RestTemplateResponseEnvelope<WTAResponseDTO>> typeReference = new ParameterizedTypeReference<RestTemplateResponseEnvelope<WTAResponseDTO>>() {};
            ResponseEntity<RestTemplateResponseEnvelope<WTAResponseDTO>> restExchange =
                    restTemplate.exchange(
                            baseUrl + "/wta/{wtaId}",
                            HttpMethod.GET, null, typeReference,wtaId);

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

    public List<WTAResponseDTO> getWTAByIds(List<BigInteger> wtaIds){
        String baseUrl = getBaseUrl(true);
        String param = wtaIds.toString().replace("[","").replace("]","");
        try {
            //HttpEntity<List<BigInteger>> request = new HttpEntity<>(wtaIds);
            ParameterizedTypeReference<RestTemplateResponseEnvelope<List<WTAResponseDTO>>> typeReference = new ParameterizedTypeReference<RestTemplateResponseEnvelope<List<WTAResponseDTO>>>() {};
            ResponseEntity<RestTemplateResponseEnvelope<List<WTAResponseDTO>>> restExchange =
                    restTemplate.exchange(
                            baseUrl + "/wta/getWTAByIds?wtaIds="+param,
                            HttpMethod.GET, null, typeReference);

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

    public WTAResponseDTO assignWTAToUnitPosition(BigInteger wtaId){
        String baseUrl = getBaseUrl(true);
        try {
            HttpEntity<BigInteger> request = new HttpEntity<>(wtaId);
            ParameterizedTypeReference<RestTemplateResponseEnvelope<WTAResponseDTO>> typeReference = new ParameterizedTypeReference<RestTemplateResponseEnvelope<WTAResponseDTO>>() {};
            ResponseEntity<RestTemplateResponseEnvelope<WTAResponseDTO>> restExchange =
                    restTemplate.exchange(
                            baseUrl + "/wta/{wtaId}",
                            HttpMethod.POST, null, typeReference,wtaId);

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

    public Boolean assignWTAToOrganization(List<Long> subTypeIds,Long unitId,Long countryId){
        String baseUrl = getBaseUrl(false)+"/country/"+countryId;
        try {
            HttpEntity<List<Long>> request = new HttpEntity<>(subTypeIds);
            ParameterizedTypeReference<RestTemplateResponseEnvelope<Boolean>> typeReference = new ParameterizedTypeReference<RestTemplateResponseEnvelope<Boolean>>() {};
            ResponseEntity<RestTemplateResponseEnvelope<Boolean>> restExchange =
                    restTemplate.exchange(
                            baseUrl + "/wta/organization/{unitId}",
                            HttpMethod.POST, request, typeReference,unitId);

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
