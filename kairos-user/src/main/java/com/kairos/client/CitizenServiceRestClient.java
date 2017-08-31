package com.kairos.client;

import com.kairos.client.dto.RestTemplateResponseEnvelope;
import com.kairos.client.dto.TaskTypeAggregateResult;
import com.kairos.response.dto.web.KMDShift;
import com.kairos.util.userContext.UserContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.util.List;

/**
 * Created by anil on 8/8/17.
 */
@Component
public class CitizenServiceRestClient {
    private static final Logger logger = LoggerFactory.getLogger(SkillServiceTemplateClient.class);
    @Autowired
    RestTemplate restTemplate;


    /**
     * @auther anil maurya
     * map in task  controller
     * @param unitId
     * @return
     */
    public Boolean createTaskFromKMD(Long staffId, KMDShift shift, Long unitId){
        String baseUrl = getBaseUrl(false);
        try {
            HttpEntity<KMDShift> request = new HttpEntity<>(shift);
            ParameterizedTypeReference<RestTemplateResponseEnvelope<Boolean>> typeReference = new ParameterizedTypeReference<RestTemplateResponseEnvelope<Boolean>>() {};
            ResponseEntity<RestTemplateResponseEnvelope<Boolean>> restExchange =
                    restTemplate.exchange(
                            baseUrl + "/unit/{unitId}/createTask/{staffId}",
                            HttpMethod.POST, request, typeReference,unitId,staffId);

            RestTemplateResponseEnvelope<Boolean> response = restExchange.getBody();
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
