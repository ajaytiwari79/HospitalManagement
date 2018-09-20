package com.kairos.rest_client;

import com.kairos.commons.client.RestTemplateResponseEnvelope;
import com.kairos.service.exception.ExceptionService;
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

import javax.inject.Inject;
import java.util.List;
import java.util.Map;

import static com.kairos.rest_client.RestClientURLUtil.getBaseUrl;

/**
 * Created by anil on 8/8/17.
 */
@Component
public class SkillServiceTemplateClient {
    private static final Logger logger = LoggerFactory.getLogger(SkillServiceTemplateClient.class);

    @Autowired
    RestTemplate restTemplate;
    @Inject
    private ExceptionService exceptionService;
    /**
     * endpoint map in taskType controller in task micro service
     * @param serviceIds
     * @param organizationId
     * @return
     */
    public  Map<String, Object> getTaskTypeList(List<Long> serviceIds, long organizationId) {

        try {

            HttpEntity<List> request = new HttpEntity<>(serviceIds);

            ParameterizedTypeReference<RestTemplateResponseEnvelope<Object>> typeReference = new ParameterizedTypeReference<RestTemplateResponseEnvelope<Object>>() {
            };

            logger.debug("typeReference "+typeReference);
            ResponseEntity<RestTemplateResponseEnvelope<Object>> restExchange =
                    restTemplate.exchange(RestClientURLUtil.getBaseUrl(false)+"/task_types/getAllAvlSkill",//"http://zuulservice/kairos/activity/api/v1/organization/{organizationId}/task_types/getAllAvlSkill"
                            HttpMethod.POST, request, typeReference, organizationId);

            logger.info("restExchange.getBody() "+ restExchange.getBody());

            RestTemplateResponseEnvelope<Object> response = restExchange.getBody();

            if (restExchange.getStatusCode().is2xxSuccessful()) {

                Map<String, Object> taskTypeListInfo = (Map<String, Object>)response.getData();

                return taskTypeListInfo;

            } else {
                throw new RuntimeException(response.getMessage());
            }
        } catch (HttpClientErrorException e) {

            logger.info("status {}", e.getStatusCode());
            logger.info("response {}", e.getResponseBodyAsString());
            exceptionService.runtimeException("message.exception.taskmicroservice",e.getMessage());
            //throw new RuntimeException("exception occurred in task micro service " + e.getMessage());
        }
        return null;
    }
}
