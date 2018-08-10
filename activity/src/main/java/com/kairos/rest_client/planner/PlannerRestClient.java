package com.kairos.rest_client.planner;

import com.kairos.enums.IntegrationOperation;
import com.kairos.enums.solver_config.PlannerUrl;
import com.kairos.rest_client.RestTemplateResponseEnvelope;
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

import static com.kairos.util.RestClientUrlUtil.getPlannerBaseUrl;
@Service
public class PlannerRestClient {
    private Logger logger = LoggerFactory.getLogger(com.kairos.rest_client.planner.PlannerRestClient.class);
    @Autowired
    private RestTemplate restTemplate;

    public <T, V> RestTemplateResponseEnvelope<V> publish(int plannerNo,T t, Long unitId, IntegrationOperation integrationOperation, PlannerUrl plannerUrl, Object... pathParams) {
        final String baseUrl = getPlannerBaseUrl();
        try {
            String url=baseUrl+"/"+plannerNo+"/api/v1/"+"unit/" + unitId + "/planner"+ getURI(plannerUrl,pathParams);
            logger.info("calling url:{} with http method:{}",url,integrationOperation);
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
            case GET:
                return HttpMethod.GET;
            default:return null;

        }
    }
    public static <T>String getURI(PlannerUrl plannerUrl,Object... pathParams){
        String uri=null;
        /*if(t instanceof StaffingLevelDTO){
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
        }*/
        switch (plannerUrl){
            case GET_VRP_SOLUTION:uri = String.format("/vrp/%s",pathParams);
                break;
            case STOP_VRP_PROBLEM:uri = String.format("/vrp/%s",pathParams);
                break;
            case SUBMIT_VRP_PROBLEM:uri = "/submitVRPPlanning";
                break;
            case GET_INDICTMENT:uri=String.format("/vrp/%s/get_indictment",pathParams);
        }/*

        else if (t instanceof VrpTaskPlanningDTO){
            uri = "planner/submitVRPPlanning";
        }
        else if (t==null){

        }*/
        return uri;
    }
}