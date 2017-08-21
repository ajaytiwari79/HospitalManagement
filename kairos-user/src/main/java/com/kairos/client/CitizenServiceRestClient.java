package com.kairos.client;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

/**
 * Created by anil on 8/8/17.
 */
@Component
public class CitizenServiceRestClient {
    private static final Logger logger = LoggerFactory.getLogger(SkillServiceTemplateClient.class);
    @Autowired
    RestTemplate restTemplate;
}
