package com.kairos.client;

import com.kairos.client.dto.PhaseAndActivityTypeWrapper;
import com.kairos.client.dto.RestTemplateResponseEnvelope;
import com.kairos.response.dto.web.cta.PhaseDTO;
import com.kairos.service.organization.OrganizationService;
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
import java.util.List;

import static com.kairos.client.RestClientURLUtil.getBaseUrl;

/**
 * Created by vipul on 25/9/17.
 */
@Component
public class PhaseRestClient {
    private static final Logger logger = LoggerFactory.getLogger(PhaseRestClient.class);

    @Autowired
    RestTemplate restTemplate;

    @Inject
    private OrganizationService organizationService;

    /**
     * @auther Vipul Pandey
     * used to create the phases in default
     * @param unitId
     * @return
     */
    public void createDefaultPhases (Long unitId){

        final String baseUrl=getBaseUrl(false);

        try {
            Long countryId = organizationService.getCountryIdOfOrganization(unitId);
            ParameterizedTypeReference<RestTemplateResponseEnvelope<Boolean>> typeReference =
                    new ParameterizedTypeReference<RestTemplateResponseEnvelope<Boolean>>() {};
            ResponseEntity<RestTemplateResponseEnvelope<Boolean>> restExchange =
                    restTemplate.exchange(
                            baseUrl + "/unit/{unitId}/phase/default?countryId="+countryId,
                            HttpMethod.POST,
                            null, typeReference, unitId);

            RestTemplateResponseEnvelope<Boolean> response = restExchange.getBody();
            if (restExchange.getStatusCode().is2xxSuccessful()) {
                logger.info("RestExchange",restExchange);
            } else {
                throw new RuntimeException(response.getMessage());
            }
        }catch (HttpClientErrorException e) {

            logger.info("status {}",e.getStatusCode());
            logger.info("response {}",e.getResponseBodyAsString());
            throw new RuntimeException("exception occurred in task micro service "+e.getMessage());
        }

    }

    /**
     * @Auther anil maurya
     * @param unitId
     * @return
     */
    public PhaseAndActivityTypeWrapper getPhaseAndActivityType (Long unitId){

        final String baseUrl=getBaseUrl(false);
        try {
            ParameterizedTypeReference<RestTemplateResponseEnvelope<PhaseAndActivityTypeWrapper>> typeReference =
                    new ParameterizedTypeReference<RestTemplateResponseEnvelope<PhaseAndActivityTypeWrapper>>() {};
            ResponseEntity<RestTemplateResponseEnvelope<PhaseAndActivityTypeWrapper>> restExchange =
                    restTemplate.exchange(
                            baseUrl + "/unit/{unitId}/phase/default",
                            HttpMethod.POST,
                            null, typeReference, unitId);

            RestTemplateResponseEnvelope<PhaseAndActivityTypeWrapper> response = restExchange.getBody();
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

    public List<PhaseDTO> getPhases (Long countryId){

        final String baseUrl=getBaseUrl(false);
        try {
            ParameterizedTypeReference<RestTemplateResponseEnvelope<List<PhaseDTO>>> typeReference =
                    new ParameterizedTypeReference<RestTemplateResponseEnvelope<List<PhaseDTO>>>() {};
            ResponseEntity<RestTemplateResponseEnvelope<List<PhaseDTO>>> restExchange =
                    restTemplate.exchange(
                            baseUrl + "/country/"+countryId+"/phase",
                            HttpMethod.GET,
                            null, typeReference,countryId);

            RestTemplateResponseEnvelope<List<PhaseDTO>> response = restExchange.getBody();
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
