package com.kairos.rest_client;

import com.kairos.commons.utils.ObjectMapperUtils;
import com.kairos.dto.activity.cta.CTAWTAAndAccumulatedTimebankWrapper;
import com.kairos.dto.activity.wta.basic_details.WTADTO;
import com.kairos.dto.activity.wta.basic_details.WTAResponseDTO;
import com.kairos.dto.activity.wta.version.WTATableSettingWrapper;
import com.kairos.commons.client.RestTemplateResponseEnvelope;
import com.kairos.service.exception.ExceptionService;
import com.kairos.wrapper.ResponseEnvelope;
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
import java.time.LocalDate;
import java.util.List;

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

    public WTAResponseDTO getWTAById(Long unitPositionId) {
        String baseUrl = getBaseUrl(true);
        try {
            // HttpEntity<BigInteger> request = new HttpEntity<>(wtaId);
            ParameterizedTypeReference<RestTemplateResponseEnvelope<WTAResponseDTO>> typeReference = new ParameterizedTypeReference<RestTemplateResponseEnvelope<WTAResponseDTO>>() {
            };
            ResponseEntity<RestTemplateResponseEnvelope<WTAResponseDTO>> restExchange =
                    restTemplate.exchange(
                            baseUrl + "/wta/unitPosition/{unitPositionId}",
                            HttpMethod.GET, null, typeReference, unitPositionId);

            RestTemplateResponseEnvelope<WTAResponseDTO> response = restExchange.getBody();
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

    public List<WTAResponseDTO> getWTAByIds(List<Long> upIds) {
        String baseUrl = getBaseUrl(true);
        String param = upIds.toString().replace("[", "").replace("]", "");
        try {
            //HttpEntity<List<BigInteger>> request = new HttpEntity<>(wtaIds);
            ParameterizedTypeReference<RestTemplateResponseEnvelope<List<WTAResponseDTO>>> typeReference = new ParameterizedTypeReference<RestTemplateResponseEnvelope<List<WTAResponseDTO>>>() {
            };
            ResponseEntity<RestTemplateResponseEnvelope<List<WTAResponseDTO>>> restExchange =
                    restTemplate.exchange(
                            baseUrl + "/wta/getWTAByIds?upIds=" + param,
                            HttpMethod.GET, null, typeReference);

            RestTemplateResponseEnvelope<List<WTAResponseDTO>> response = restExchange.getBody();
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

    public CTAWTAAndAccumulatedTimebankWrapper assignWTAToUnitPosition(Long unitPositionId, BigInteger wtaId, BigInteger ctaId, LocalDate startDate) {
        String baseUrl = getBaseUrl(true);
        try {
            ParameterizedTypeReference<RestTemplateResponseEnvelope<CTAWTAAndAccumulatedTimebankWrapper>> typeReference =
                    new ParameterizedTypeReference<RestTemplateResponseEnvelope<CTAWTAAndAccumulatedTimebankWrapper>>() {
            };
            ResponseEntity<RestTemplateResponseEnvelope<CTAWTAAndAccumulatedTimebankWrapper>> restExchange =
                    restTemplate.exchange(
                            baseUrl + "/unitPosition/{unitPositionId}/wta/{wtaId}/cta/{ctaId}/?startDate="+startDate,
                            HttpMethod.POST, null, typeReference, unitPositionId,wtaId,ctaId);

            RestTemplateResponseEnvelope<CTAWTAAndAccumulatedTimebankWrapper> response = restExchange.getBody();
            if (restExchange.getStatusCode().is2xxSuccessful()) {
                return response.getData();
            } else {
                throw new RuntimeException(response.getMessage());
            }
        } catch (HttpClientErrorException e) {

            logger.info("status {}", e.getStatusCode());
            logger.info("response {}", e.getResponseBodyAsString());
            exceptionService.exceptionWithoutConvertInRestClient(ObjectMapperUtils.JsonStringToObject(e.getResponseBodyAsString(), ResponseEnvelope.class).getMessage());
            throw new RuntimeException("exception occurred in task micro service " + e.getMessage());
        }
    }

    public WTAResponseDTO updateWTAOfUnitPosition(WTADTO wtadto, boolean unitPositionPublished) {
        String baseUrl = getBaseUrl(true);
        try {
            HttpEntity<WTADTO> request = new HttpEntity<>(wtadto);
            ParameterizedTypeReference<RestTemplateResponseEnvelope<WTAResponseDTO>> typeReference = new ParameterizedTypeReference<RestTemplateResponseEnvelope<WTAResponseDTO>>() {
            };
            ResponseEntity<RestTemplateResponseEnvelope<WTAResponseDTO>> restExchange =
                    restTemplate.exchange(
                            baseUrl + "/wta?unitPositionPublished=" + unitPositionPublished,
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
            exceptionService.exceptionWithoutConvertInRestClient(ObjectMapperUtils.JsonStringToObject(e.getResponseBodyAsString(),ResponseEnvelope.class).getMessage());
        }
        return null;
    }

    public Boolean makeDefaultDateForOrganization(List<Long> subTypeIds, Long unitId, Long countryId) {
        String baseUrl = getBaseUrl(false) + "/country/" + countryId;
        try {
            HttpEntity<List<Long>> request = new HttpEntity<>(subTypeIds);
            ParameterizedTypeReference<RestTemplateResponseEnvelope<Boolean>> typeReference = new ParameterizedTypeReference<RestTemplateResponseEnvelope<Boolean>>() {
            };
            ResponseEntity<RestTemplateResponseEnvelope<Boolean>> restExchange =
                    restTemplate.exchange(
                            baseUrl + "/wta/organization/{unitId}",
                            HttpMethod.POST, request, typeReference, unitId);

            RestTemplateResponseEnvelope<Boolean> response = restExchange.getBody();
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

    public WTATableSettingWrapper getWTAWithVersionIds(List<BigInteger> wtaIds) {
        String baseUrl = getBaseUrl(true);
        String param = wtaIds.toString().replace("[", "").replace("]", "");
        try {
            ParameterizedTypeReference<RestTemplateResponseEnvelope<WTATableSettingWrapper>> typeReference =
                    new ParameterizedTypeReference<RestTemplateResponseEnvelope<WTATableSettingWrapper>>() {
                    };
            ResponseEntity<RestTemplateResponseEnvelope<WTATableSettingWrapper>> restExchange =
                    restTemplate.exchange(
                            baseUrl + "/wta/versions?wtaIds=" + param,
                            HttpMethod.GET, null, typeReference);

            RestTemplateResponseEnvelope<WTATableSettingWrapper> response = restExchange.getBody();
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


}
