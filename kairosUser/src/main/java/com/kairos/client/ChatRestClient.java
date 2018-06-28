package com.kairos.client;

import com.kairos.response.dto.web.staff.StaffChatDetails;
import com.kairos.service.organization.OrganizationService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import javax.inject.Inject;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by vipul on 25/9/17.
 */
@Component
public class ChatRestClient {
    private static final Logger logger = LoggerFactory.getLogger(ChatRestClient.class);

    @Autowired
    @Qualifier("schedulerRestTemplate")
    RestTemplate restTemplate;

    @Inject
    private OrganizationService organizationService;


    /**
     * @return
     * @auther Vipul Pandey
     * used to register staff to chat server
     */
    public StaffChatDetails registerUser() {
        Map<String, String> auth = new HashMap();
        auth.put("type", "m.login.dummy");
        auth.put("session", "EERT345");
        StaffChatDetails staffChatDetails = new StaffChatDetails(auth, "arvind10", "arvind@kairos");
        try {

            HttpEntity requestEntity = new HttpEntity(staffChatDetails);

            RestTemplate restTemplate = new RestTemplate();
            restTemplate.getMessageConverters().add(new MappingJackson2HttpMessageConverter());
            ResponseEntity<StaffChatDetails> restExchange =
                    restTemplate.exchange(
                            "http://localhost/_matrix/client/r0/register?kind=user", //URL coming from property file
                            HttpMethod.POST,
                            requestEntity, StaffChatDetails.class);

            StaffChatDetails response = restExchange.getBody();
            if (restExchange.getStatusCode().is2xxSuccessful()) {
                BeanUtils.copyProperties(response,staffChatDetails);
                System.out.println(restExchange.toString());
            }
        } catch (HttpClientErrorException e) {

            logger.info("status {}", e.getStatusCode());
            logger.info("response {}", e.getResponseBodyAsString());
            throw new RuntimeException("exception occurred in task micro service " + e.getMessage());

        }
        return staffChatDetails;
    }


}
