package com.kairos.activity.client.planner;

import com.kairos.activity.client.dto.RestTemplateResponseEnvelope;
import com.kairos.activity.enums.IntegrationOperation;
import com.kairos.activity.response.dto.staffing_level.StaffingLevelDTO;
import com.kairos.activity.response.dto.staffing_level.StaffingLevelDto;
import com.kairos.client.dto.activity.ActivityNoTabsDTO;
import com.kairos.persistence.model.user.staff.StaffBasicDetailsDTO;
import com.kairos.response.dto.web.UnitPositionWtaDTO;
import com.kairos.response.dto.web.wta.WTAResponseDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;

import static com.kairos.activity.util.RestClientUrlUtil.getPlannerBaseUrl;
@Service
public class PlannerRestClient {
    private Logger logger = LoggerFactory.getLogger(PlannerRestClient.class);
    @Autowired
    RestTemplate restTemplate;

    public <T, V> RestTemplateResponseEnvelope<V> publish(T t, Long unitId, IntegrationOperation integrationOperation,Object... pathParams) {
        final String baseUrl = getPlannerBaseUrl();

        try {
            String url=baseUrl + unitId + "/"+ getURI(t,integrationOperation,pathParams);
            ParameterizedTypeReference<RestTemplateResponseEnvelope<V>> typeReference = new ParameterizedTypeReference<RestTemplateResponseEnvelope<V>>() {
            };
            ResponseEntity<RestTemplateResponseEnvelope<V>> restExchange =
                    restTemplate.exchange(
                            url,
                            getHttpMethod(integrationOperation),
                            new HttpEntity<>(t), typeReference);
            RestTemplateResponseEnvelope<V> response = restExchange.getBody();
            if (!restExchange.getStatusCode().is2xxSuccessful()) {
                throw new RuntimeException(response.getMessage());
            }
            return response;
        } catch (HttpClientErrorException e) {
            logger.info("status {}", e.getStatusCode());
            logger.info("response {}", e.getResponseBodyAsString());
            throw new RuntimeException("exception occurred in activity micro service " + e.getMessage());
        }

    }

    public static HttpMethod getHttpMethod(IntegrationOperation integrationOperation) {
        switch (integrationOperation) {
            case CREATE:
                return HttpMethod.POST;
            case DELETE:
                return HttpMethod.DELETE;
            case UPDATE:
                return HttpMethod.PUT;
            default:return null;

        }
    }
    public static <T>String getURI(T t,IntegrationOperation integrationOperation,Object... pathParams){
        String uri=null;
        if(t instanceof StaffingLevelDTO){
            uri= "staffing_level/";
        }else if(t instanceof ActivityNoTabsDTO){
            uri= "activity/";
        }else if(t instanceof ArrayList && ((List)t).get(0) instanceof StaffingLevelDTO){
            uri= "staffing_level/multiple";
        }
        else if(t instanceof ArrayList && ((List)t).get(0) instanceof ActivityNoTabsDTO){
            uri= "activity/multiple";
        }
        else if(t instanceof WTAResponseDTO){
            uri= String.format("staff/%s/unitposition/%s/wta",pathParams);
        }
        return uri;
    }
}