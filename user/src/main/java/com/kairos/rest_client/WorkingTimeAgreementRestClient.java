package com.kairos.rest_client;

import com.kairos.commons.client.RestTemplateResponseEnvelope;
import com.kairos.commons.utils.ObjectMapperUtils;
import com.kairos.dto.activity.cta.CTAWTAAndAccumulatedTimebankWrapper;
import com.kairos.dto.activity.wta.basic_details.WTADTO;
import com.kairos.dto.activity.wta.basic_details.WTAResponseDTO;
import com.kairos.service.exception.ExceptionService;
import com.kairos.wrapper.ResponseEnvelope;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import javax.inject.Inject;
import java.math.BigInteger;
import java.time.LocalDate;

import static com.kairos.rest_client.RestClientURLUtil.getBaseUrl;

/**
 * @author pradeep
 * @date - 21/4/18
 */

@Service
public class WorkingTimeAgreementRestClient {

    private static final Logger logger = LoggerFactory.getLogger(WorkingTimeAgreementRestClient.class);

    @Inject
    private RestTemplate restTemplate;
    @Inject private
    ExceptionService exceptionService;

    public CTAWTAAndAccumulatedTimebankWrapper getWTAByExpertise(Long expertiseId) {
        String baseUrl = getBaseUrl(true);
        try {
            //HttpEntity<Long> request = new HttpEntity<>(expertiseId);
            ParameterizedTypeReference<RestTemplateResponseEnvelope<CTAWTAAndAccumulatedTimebankWrapper>> typeReference = new ParameterizedTypeReference<RestTemplateResponseEnvelope<CTAWTAAndAccumulatedTimebankWrapper>>() {
            };
            ResponseEntity<RestTemplateResponseEnvelope<CTAWTAAndAccumulatedTimebankWrapper>> restExchange =
                    restTemplate.exchange(
                            baseUrl + "/expertise/{expertiseId}/cta_wta",
                            HttpMethod.GET, null, typeReference, expertiseId);

            RestTemplateResponseEnvelope<CTAWTAAndAccumulatedTimebankWrapper> response = restExchange.getBody();
            if (restExchange.getStatusCode().is2xxSuccessful()) {
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

    public CTAWTAAndAccumulatedTimebankWrapper assignWTAToEmployment(Long employmentId, BigInteger wtaId, BigInteger ctaId, LocalDate startDate) {
        String baseUrl = getBaseUrl(true);
        try {
            ParameterizedTypeReference<RestTemplateResponseEnvelope<CTAWTAAndAccumulatedTimebankWrapper>> typeReference =
                    new ParameterizedTypeReference<RestTemplateResponseEnvelope<CTAWTAAndAccumulatedTimebankWrapper>>() {
            };
            ResponseEntity<RestTemplateResponseEnvelope<CTAWTAAndAccumulatedTimebankWrapper>> restExchange =
                    restTemplate.exchange(
                            baseUrl + "/employment/{employmentId}/wta/{wtaId}/cta/{ctaId}/?startDate="+startDate,
                            HttpMethod.POST, null, typeReference, employmentId,wtaId,ctaId);

            RestTemplateResponseEnvelope<CTAWTAAndAccumulatedTimebankWrapper> response = restExchange.getBody();
            if (restExchange.getStatusCode().is2xxSuccessful()) {
                return response.getData();
            } else {
                throw new RuntimeException(response.getMessage());
            }
        } catch (HttpClientErrorException e) {

            logger.info("status {}", e.getStatusCode());
            logger.info("response {}", e.getResponseBodyAsString());
            exceptionService.exceptionWithoutConvertInRestClient(ObjectMapperUtils.jsonStringToObject(e.getResponseBodyAsString(), ResponseEnvelope.class).getMessage());
            throw new RuntimeException("exception occurred in task micro service " + e.getMessage());
        }
    }

    public WTAResponseDTO updateWTAOfEmployment(WTADTO wtadto, boolean employmentPublished) {
        String baseUrl = getBaseUrl(true);
        try {
            HttpEntity<WTADTO> request = new HttpEntity<>(wtadto);
            ParameterizedTypeReference<RestTemplateResponseEnvelope<WTAResponseDTO>> typeReference = new ParameterizedTypeReference<RestTemplateResponseEnvelope<WTAResponseDTO>>() {
            };
            ResponseEntity<RestTemplateResponseEnvelope<WTAResponseDTO>> restExchange =
                    restTemplate.exchange(
                            baseUrl + "/wta?employmentPublished=" + employmentPublished,
                            HttpMethod.PUT, request, typeReference);

            RestTemplateResponseEnvelope<WTAResponseDTO> response = restExchange.getBody();
            if (restExchange.getStatusCode().is2xxSuccessful()) {
                return response.getData();
            } else {
                exceptionService.exceptionWithoutConvertInRestClient(response.getMessage());
            }
        } catch (HttpClientErrorException e) {

            logger.info("status {}", e.getStatusCode());
            logger.info("response {}", e.getResponseBodyAsString());
            exceptionService.exceptionWithoutConvertInRestClient(ObjectMapperUtils.jsonStringToObject(e.getResponseBodyAsString(),ResponseEnvelope.class).getMessage());
        }
        return null;
    }
}
