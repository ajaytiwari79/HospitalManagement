package com.kairos.rest_client;

import com.kairos.dto.activity.wta.basic_details.WTABasicDetailsDTO;
import com.kairos.dto.activity.wta.basic_details.WTADefaultDataInfoDTO;
import com.kairos.utils.RestClientUrlUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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

    public WTABasicDetailsDTO getWtaRelatedInfo(Long expertiseId, Long organizationSubTypeId, Long countryId, Long organizationId, Long organizationTypeId) {
         StringBuffer baseUrl = new StringBuffer(RestClientUrlUtil.getBaseUrl(false));
         baseUrl.append("/WTARelatedInfo?").append("countryId=").append(countryId).append("&organizationId=").append(organizationId).append("&organizationTypeId=").append(organizationTypeId).append("&organizationSubTypeId=").append(organizationSubTypeId).append("&expertiseId=").append(expertiseId);

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


    public WTADefaultDataInfoDTO getWtaTemplateDefaultDataInfo(Long countryId) {
        StringBuffer baseUrl = new StringBuffer(RestClientUrlUtil.getBaseUrl(false)).append("/country/").append(countryId).append("/getWtaTemplateDefaultDataInfo");
        try {
            ParameterizedTypeReference<RestTemplateResponseEnvelope<WTADefaultDataInfoDTO>> typeReference = new ParameterizedTypeReference<RestTemplateResponseEnvelope<WTADefaultDataInfoDTO>>() {
            };
            ResponseEntity<RestTemplateResponseEnvelope<WTADefaultDataInfoDTO>> restExchange =
                    restTemplate.exchange(
                            baseUrl.toString(),
                            HttpMethod.GET, null, typeReference);
            RestTemplateResponseEnvelope<WTADefaultDataInfoDTO> response = restExchange.getBody();
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


    public WTADefaultDataInfoDTO getWtaTemplateDefaultDataInfoByUnitId() {
        StringBuffer baseUrl = new StringBuffer(RestClientUrlUtil.getBaseUrl(true)).append("/getWtaTemplateDefaultDataInfoByUnitId");
        try {
            ParameterizedTypeReference<RestTemplateResponseEnvelope<WTADefaultDataInfoDTO>> typeReference = new ParameterizedTypeReference<RestTemplateResponseEnvelope<WTADefaultDataInfoDTO>>() {
            };
            ResponseEntity<RestTemplateResponseEnvelope<WTADefaultDataInfoDTO>> restExchange =
                    restTemplate.exchange(
                            baseUrl.toString(),
                            HttpMethod.GET, null, typeReference);
            RestTemplateResponseEnvelope<WTADefaultDataInfoDTO> response = restExchange.getBody();
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


}
