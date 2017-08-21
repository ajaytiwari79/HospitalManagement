package com.kairos.client;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;

/**
 * Created by anil on 4/8/17.
 */
@Component
public class PlannerServiceRestTemplateClient {
    @Autowired
    RestTemplate restTemplate;


    private static final Logger logger = LoggerFactory.getLogger(ClientServiceRestClient.class);


    public boolean deleteTaskForCitizen(Long organizationId,Long unitId, Long citizenId){
        ResponseEntity<Map> restExchange =
                restTemplate.exchange(
                        "http://zuulservice/activity/api/v1/organization/{organizationId}/unit/{unitId}/planner/citizen/{citizenId}",
                         HttpMethod.DELETE,
                        null,Map.class);

      Map<String, Object>response= restExchange.getBody();
        boolean success= (boolean) response.get("isSuccess");
        return success?true:false;
    }

}
