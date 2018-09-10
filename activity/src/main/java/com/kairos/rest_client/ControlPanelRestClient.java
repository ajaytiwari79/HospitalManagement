package com.kairos.rest_client;

import com.kairos.dto.activity.control_panel.ControlPanelDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import static com.kairos.utils.RestClientUrlUtil.getBaseUrl;

@Component
public class ControlPanelRestClient {
    private static final Logger logger = LoggerFactory.getLogger(CountryRestClient.class);

    @Autowired
    RestTemplate restTemplate;


    public ControlPanelDTO getRequiredControlPanelData(long controlPanelId){
        final String baseUrl=getBaseUrl(true);

        try {
            ParameterizedTypeReference<RestTemplateResponseEnvelope<ControlPanelDTO>> typeReference = new ParameterizedTypeReference<RestTemplateResponseEnvelope<ControlPanelDTO>>() {};
            ResponseEntity<RestTemplateResponseEnvelope<ControlPanelDTO>> restExchange =
                    restTemplate.exchange(
                            baseUrl + "/control_panel/settings/{controlPanelId}/control_panel_details",
                            HttpMethod.GET,
                            null, typeReference, controlPanelId);
            RestTemplateResponseEnvelope<ControlPanelDTO> response = restExchange.getBody();
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
