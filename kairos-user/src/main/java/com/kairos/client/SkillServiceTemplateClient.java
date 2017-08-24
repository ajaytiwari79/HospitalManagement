package com.kairos.client;

import com.kairos.response.dto.web.ResponseEnvelope;
import com.kairos.util.userContext.UserContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Map;

/**
 * Created by anil on 8/8/17.
 */
@Component
public class SkillServiceTemplateClient {
    private static final Logger logger = LoggerFactory.getLogger(SkillServiceTemplateClient.class);

    @Autowired
    RestTemplate restTemplate;

    /**
     * endpoint map in taskType controller in task micro service
     * @param serviceIds
     * @param orgId
     * @return
     */
    public  Map<String, Object> getTaskTypeList(List<Long> serviceIds, long orgId){
        final String baseUrl=getBaseUrl(false);

        try {

            HttpEntity<List> request = new HttpEntity<>(serviceIds);

            ResponseEntity<ResponseEnvelope> restExchange =
                    restTemplate.exchange(baseUrl+"/task_types/getAllAvlSkill",
                            HttpMethod.
                                    POST,request, ResponseEnvelope.class);

            ResponseEnvelope response = restExchange.getBody();
            if (restExchange.getStatusCode().is2xxSuccessful()) {


                Map<String,Object> taskTypeListInfo= (Map<String,Object>) response.getData();

                return taskTypeListInfo;

            } else {
                throw new RuntimeException(response.getMessage());
            }
        }catch (HttpClientErrorException e) {

            logger.info("status {}",e.getStatusCode());
            logger.info("response {}",e.getResponseBodyAsString());
            throw new RuntimeException("exception occurred in task micro service "+e.getMessage());
        }

    }


    private final String getBaseUrl(boolean hasUnitInUrl){
        if(hasUnitInUrl){
            String baseUrl=new StringBuilder("http://zuulservice/activity/api/v1/organization/").append(UserContext.getOrgId()).append("/unit/").append(UserContext.getUnitId()).toString();
            return baseUrl;
        }else{
            String baseUrl=new StringBuilder("http://zuulservice/activity/api/v1/organization/").append(UserContext.getOrgId()).toString();
            return baseUrl;
        }

    }
}
