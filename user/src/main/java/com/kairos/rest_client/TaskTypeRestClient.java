package com.kairos.rest_client;

import com.kairos.dto.activity.task_type.OrgTaskTypeAggregateResult;
import com.kairos.commons.client.RestTemplateResponseEnvelope;
import com.kairos.service.exception.ExceptionService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import javax.inject.Inject;
import java.util.List;

import static com.kairos.rest_client.RestClientURLUtil.getBaseUrl;

@Component
public class TaskTypeRestClient {

    private static final Logger logger = LoggerFactory.getLogger(PlannerRestClient.class);

    @Autowired
    RestTemplate restTemplate;
    @Inject
    private ExceptionService exceptionService;
      /** @auther anil maurya
     * map in task demand controller
     * @param unitId
     * @return
             */
    public List<OrgTaskTypeAggregateResult> getTaskTypesOfUnit(Long unitId) {
        try {
            ParameterizedTypeReference<RestTemplateResponseEnvelope<List<OrgTaskTypeAggregateResult>>> typeReference = new ParameterizedTypeReference<RestTemplateResponseEnvelope<List<OrgTaskTypeAggregateResult>>>() {};
            ResponseEntity<RestTemplateResponseEnvelope<List<OrgTaskTypeAggregateResult>>> restExchange =
                    restTemplate.exchange(
                            getBaseUrl()+"task_demand/unit/{unitId}",
                            HttpMethod.
                                    GET,null, typeReference,unitId);

            RestTemplateResponseEnvelope<List<OrgTaskTypeAggregateResult>> response = restExchange.getBody();
            if (restExchange.getStatusCode().is2xxSuccessful()) {
                return response.getData();
            } else {
                throw new RuntimeException(response.getMessage());
            }
        }catch (HttpClientErrorException e) {

            logger.info("status {}",e.getStatusCode());
            logger.info("response {}",e.getResponseBodyAsString());
            exceptionService.runtimeException("message.exception.taskmicroservice",e.getMessage());
            //throw new RuntimeException("exception occurred in task micro service "+e.getMessage());
        }
return null;
    }

}
