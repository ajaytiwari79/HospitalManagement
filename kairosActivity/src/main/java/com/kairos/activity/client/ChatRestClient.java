package com.kairos.activity.client;

import com.kairos.activity.client.dto.RestTemplateResponseEnvelope;
import com.kairos.activity.util.RestClientUrlUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

@Component
public class ChatRestClient {
    private static final Logger logger = LoggerFactory.getLogger(ChatRestClient.class);


    @Autowired
    @Qualifier("schedulerRestTemplate")
    RestTemplate restTemplate;


    /**
     * @param title
     * @return
     * @auther anil maurya
     * this endpoint map with AbsenceTypes controller
     */
    public Map<String, Object> resgisterUser(String title) {

        final String baseUrl = RestClientUrlUtil.getBaseUrl(false);
        try {
            ParameterizedTypeReference<RestTemplateResponseEnvelope<Map<String, Object>>> typeReference = new ParameterizedTypeReference<RestTemplateResponseEnvelope<Map<String, Object>>>() {
            };
            ResponseEntity<RestTemplateResponseEnvelope<Map<String, Object>>> restExchange =
                    restTemplate.exchange(
                            "http://xyz.example.com:8008/_matrix/client/r0/register?kind=user",
                            HttpMethod.POST,
                            null, typeReference, title);
            RestTemplateResponseEnvelope<Map<String, Object>> response = restExchange.getBody();
            if (restExchange.getStatusCode().is2xxSuccessful()) {
                return response.getData();
            } else {
                throw new RuntimeException(response.getMessage());
            }
        } catch (HttpClientErrorException e) {
            logger.info("status {}", e.getStatusCode());
            logger.info("response {}", e.getResponseBodyAsString());
            throw new RuntimeException("exception occurred in user micro service " + e.getMessage());
        }


    }
}
