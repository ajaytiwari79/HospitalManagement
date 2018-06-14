package com.kairos.activity.client.vrpRestClient;

import com.kairos.activity.client.OrganizationRestClient;
import com.kairos.activity.client.dto.RestTemplateResponseEnvelope;
import com.kairos.activity.client.dto.organization.OrganizationDTO;
import com.kairos.activity.util.RestClientUrlUtil;
import com.kairos.response.dto.web.client.VRPClientDTO;
import com.kairos.response.dto.web.presence_type.PresenceTypeWithTimeTypeDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.util.List;

/**
 * @author pradeep
 * @date - 13/6/18
 */
@Component
public class VRPRestClient {

    private static final Logger logger = LoggerFactory.getLogger(VRPRestClient.class);
    @Autowired
    private RestTemplate restTemplate;

    public List<VRPClientDTO> getAllVRPClient() {
        final String baseUrl = RestClientUrlUtil.getBaseUrl(true);
        try {
            ParameterizedTypeReference<RestTemplateResponseEnvelope<List<VRPClientDTO>>> typeReference = new ParameterizedTypeReference<RestTemplateResponseEnvelope<List<VRPClientDTO>>>() {
            };
            ResponseEntity<RestTemplateResponseEnvelope<List<VRPClientDTO>>> restExchange =
                    restTemplate.exchange(
                            baseUrl + "/vrpClient",
                            HttpMethod.GET, null, typeReference);
            RestTemplateResponseEnvelope<List<VRPClientDTO>> response = restExchange.getBody();
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
