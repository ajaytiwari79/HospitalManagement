package com.kairos.client;

import com.kairos.response.dto.web.ResponseEnvelope;
import com.kairos.utils.userContext.UserContext;
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
public class StaffServiceRestTemplate {
    private static final Logger logger = LoggerFactory.getLogger(ClientServiceRestClient.class);
    @Autowired
    RestTemplate restTemplate;


    /**
     *  @auther anil maurya
     *  endpoint map in task controller
     * @param staffId
     * @param anonymousStaffId
     * @return
     */
    public boolean  updateTaskForStaff(Long staffId,Long anonymousStaffId){

        final String baseUrl=getBaseUrl();

        try {
            ResponseEntity<ResponseEnvelope> restExchange =
                    restTemplate.exchange(baseUrl+"/task/staff/{staffId}/{anonymousStaffId}",
                            HttpMethod.
                                    GET,null, ResponseEnvelope.class);

            ResponseEnvelope response = restExchange.getBody();
            if (restExchange.getStatusCode().is2xxSuccessful()) {


              return true;

            } else {
                throw new RuntimeException(response.getMessage());
            }
        }catch (HttpClientErrorException e) {

            logger.info("status {}",e.getStatusCode());
            logger.info("response {}",e.getResponseBodyAsString());
            throw new RuntimeException("exception occurred in task micro service "+e.getMessage());
        }


    }
    private final String getBaseUrl(){
        String baseUrl=new StringBuilder("http://zuulservice/activity/api/v1/organization/").append(UserContext.getOrgId()).append("/unit/").append(UserContext.getUnitId()).toString();
        return baseUrl;
    }
}
