package com.kairos.activity.client;

import com.kairos.activity.client.dto.RestTemplateResponseEnvelope;
import com.kairos.activity.util.userContext.UserContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.util.Map;
@Component
public class AbsenceTypeRestClient {
    private static final Logger logger = LoggerFactory.getLogger(AbsenceTypeRestClient.class);


    @Autowired
    RestTemplate restTemplate;


    /**
     * @auther anil maurya
     * this endpoint map with AbsenceTypes controller
     * @param title
     * @return
     */
    public Map<String,Object> getAbsenceTypeByName(String title){

        final String baseUrl=getBaseUrl(false);
        try {
            ParameterizedTypeReference<RestTemplateResponseEnvelope<Map<String,Object>>> typeReference = new ParameterizedTypeReference<RestTemplateResponseEnvelope<Map<String,Object>>>() {};
            ResponseEntity<RestTemplateResponseEnvelope<Map<String,Object>>> restExchange =
                    restTemplate.exchange(
                            baseUrl + " /absenceTypes/{title}",
                            HttpMethod.GET,
                            null, typeReference,title);
            RestTemplateResponseEnvelope<Map<String,Object>> response = restExchange.getBody();
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

    private final String getBaseUrl(boolean hasUnitInUrl){
        if(hasUnitInUrl){
            String baseUrl=new StringBuilder("http://zuulservice/kairos/user/api/v1/organization/").append(UserContext.getOrgId()).append("/unit/").append(UserContext.getUnitId()).toString();
            return baseUrl;
        }else{
            String baseUrl=new StringBuilder("http://zuulservice/kairos/user/api/v1/organization/").append(UserContext.getOrgId()).toString();
            return baseUrl;
        }

    }
}
