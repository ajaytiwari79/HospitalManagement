package com.kairos.client;

import com.kairos.client.dto.RestTemplateResponseEnvelope;
import com.kairos.client.dto.TaskTypeAggregateResult;
import com.kairos.response.dto.web.KMDShift;
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


    /**TODO new rest template
     * @auther anil maurya
     * map in task  controller
     * @param unitId
     * @return
     */
    public void createTaskFromKMD(Long staffId, KMDShift shift, Long unitId){
        try {
            HttpEntity<KMDShift> request = new HttpEntity<>(shift);
            ParameterizedTypeReference<RestTemplateResponseEnvelope<List<TaskTypeAggregateResult>>> typeReference = new ParameterizedTypeReference<RestTemplateResponseEnvelope<List<TaskTypeAggregateResult>>>() {};
            ResponseEntity<RestTemplateResponseEnvelope<List<TaskTypeAggregateResult>>> restExchange =
                    restTemplate.exchange(
                            "http://zuulservice/activity/api/v1/unit/{unitId}/createTask/{staffId}",
                            HttpMethod.POST, request, typeReference);

            RestTemplateResponseEnvelope<List<TaskTypeAggregateResult>> response = restExchange.getBody();
            if (restExchange.getStatusCode().is2xxSuccessful()) {

            } else {
                throw new RuntimeException(response.getMessage());
            }
        }catch (HttpClientErrorException e) {

            logger.info("status {}",e.getStatusCode());
            logger.info("response {}",e.getResponseBodyAsString());
            throw new RuntimeException("exception occurred in task micro service "+e.getMessage());
        }

    }

}
