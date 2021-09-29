package com.kairos.rest_client;

import com.kairos.dto.user.staff.staff.StaffChatDetails;
import com.kairos.service.organization.OrganizationService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import javax.inject.Inject;

/**
 * Created by vipul on 25/9/17.
 */
@Component
public class ChatRestClient {
    private static final Logger logger = LoggerFactory.getLogger(ChatRestClient.class);

    private static String chatServerUrl;

    @Value("${chat.matrix.url}")
    public void setPlannerServiceUrl(String chatServerUrl) {
        ChatRestClient.chatServerUrl = chatServerUrl;
    }

    @Inject
    private OrganizationService organizationService;


    /**
     * @return
     * @auther Vipul Pandey
     * used to register staff to chat server
     */
    public StaffChatDetails registerUser(StaffChatDetails staffChatDetails) {
        try {
            String url = chatServerUrl+"register?kind=user";
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            HttpEntity requestEntity = new HttpEntity(staffChatDetails,headers);
            RestTemplate restTemplate = new RestTemplate();
            restTemplate.getMessageConverters().add(new MappingJackson2HttpMessageConverter());
            ResponseEntity<StaffChatDetails> restExchange =
                    restTemplate.exchange(
                            url,
                            HttpMethod.POST,
                            requestEntity, StaffChatDetails.class);

            StaffChatDetails response = restExchange.getBody();
            if (restExchange.getStatusCode().is2xxSuccessful()) {
                BeanUtils.copyProperties(response,staffChatDetails);
                //System.out.println(restExchange.toString());
            }
        } catch (HttpClientErrorException e) {
            logger.info("status {}", e.getStatusCode());
            logger.info("response {}", e.getResponseBodyAsString());
        }
        return staffChatDetails;
    }


}
