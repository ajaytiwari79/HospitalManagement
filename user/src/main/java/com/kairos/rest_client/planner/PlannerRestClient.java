package com.kairos.rest_client.planner;

import com.kairos.dto.activity.wta.UnitPositionWtaDTO;
import com.kairos.dto.activity.wta.basic_details.WTAResponseDTO;
import com.kairos.commons.client.RestTemplateResponseEnvelope;
import com.kairos.enums.IntegrationOperation;
import com.kairos.dto.user.staff.staff.Staff;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;

import static com.kairos.rest_client.RestClientURLUtil.getPlannerBaseUrl;


@Service("optaplannerServiceRestClient")
public class PlannerRestClient {
    private static Logger logger = LoggerFactory.getLogger(PlannerRestClient.class);

    @Autowired
    RestTemplate restTemplate;

    public <T, V> RestTemplateResponseEnvelope<V> publish(T t, Long unitId, IntegrationOperation integrationOperation, Object... pathParams) {
        final String baseUrl = getPlannerBaseUrl();
        return null;
        /*        try {
            String url = baseUrl + unitId + "/" + getURI(t, integrationOperation, pathParams);
            logger.info("calling url:{} with http method:{}", url, integrationOperation);
            ParameterizedTypeReference<RestTemplateResponseEnvelope<V>> typeReference = new ParameterizedTypeReference<RestTemplateResponseEnvelope<V>>() {
            };
            ResponseEntity<RestTemplateResponseEnvelope<V>> restExchange =
                    restTemplate.exchange(
                            url,
                            getHttpMethod(integrationOperation),
                            t == null ? null : new HttpEntity<>(t), typeReference);
            RestTemplateResponseEnvelope<V> response = restExchange.getBody();
            if (!restExchange.getStatusCode().is2xxSuccessful()) {
                throw new RuntimeException(response.getMessage());
            }
            return response;
        } catch (HttpClientErrorException e) {
            logger.info("status {}", e.getStatusCode());
            logger.info("response {}", e.getResponseBodyAsString());
            throw new RuntimeException("exception occurred in activity micro service " + e.getMessage());
        }*/

    }

    public static HttpMethod getHttpMethod(IntegrationOperation integrationOperation) {
        switch (integrationOperation) {
            case CREATE:
                return HttpMethod.POST;
            case DELETE:
                return HttpMethod.DELETE;
            case UPDATE:
                return HttpMethod.PUT;
            default:
                return null;

        }
    }

    public static <T> String getURI(T t, IntegrationOperation integrationOperation, Object... pathParams) {
        String uri = "";
        if (t instanceof Staff) {
            uri = "staff/";
        } else if (t instanceof UnitPositionWtaDTO && integrationOperation.equals(IntegrationOperation.CREATE)) {
            uri = String.format("staff/%s/unitposition/", pathParams);
        } else if (t instanceof UnitPositionWtaDTO && (integrationOperation.equals(IntegrationOperation.UPDATE) || integrationOperation.equals(IntegrationOperation.DELETE))) {
            uri = String.format("staff/%s/unitposition/%s", pathParams);
        } else if (t instanceof UnitPositionWtaDTO) {
            uri = integrationOperation.equals(IntegrationOperation.CREATE) ?
                    String.format("staff/%s/unitposition/", pathParams) : String.format("staff/%s/unitposition/%s", pathParams);
        } else if (t instanceof UnitPositionWtaDTO && ((ArrayList) t).get(0) instanceof UnitPositionWtaDTO) {
            uri = "/unitposition/multiple";
        } else if (t instanceof WTAResponseDTO) {
            uri = String.format("staff/%s/unitposition/%s/wta", pathParams);
        }
        return uri;
    }
}