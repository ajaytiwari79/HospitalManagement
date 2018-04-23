package com.kairos.activity.client.planner;

import com.kairos.activity.client.dto.RestTemplateResponseEnvelope;
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

import static com.kairos.activity.util.RestClientUrlUtil.getPlannerBaseUrl;
@Service
public class PlannerRestClient {
    private Logger logger= LoggerFactory.getLogger(PlannerRestClient.class);

    @Autowired
    RestTemplate restTemplate;

    public <T,V>  RestTemplateResponseEnvelope<V> publish(T t,Long unitId){
        final String baseUrl=getPlannerBaseUrl();

        try {
            ParameterizedTypeReference<RestTemplateResponseEnvelope<V>> typeReference = new ParameterizedTypeReference<RestTemplateResponseEnvelope<V>>(){};
            ResponseEntity<RestTemplateResponseEnvelope<V>> restExchange =
                    restTemplate.exchange(
                            baseUrl + unitId+"/staffing_level/",
                            HttpMethod.POST,
                            new HttpEntity<>(t), typeReference);
            RestTemplateResponseEnvelope<V> response = restExchange.getBody();
            if (!restExchange.getStatusCode().is2xxSuccessful()) {
                throw new RuntimeException(response.getMessage());
            }
            return response;
        }catch (HttpClientErrorException e) {
            logger.info("status {}",e.getStatusCode());
            logger.info("response {}",e.getResponseBodyAsString());
            throw new RuntimeException("exception occurred in user micro service "+e.getMessage());
        }

    }
}
