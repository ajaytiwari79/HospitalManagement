package com.kairos.service.counter;


import com.google.api.client.util.Value;
import com.kairos.KairosActivityApplication;
import com.kairos.client.dto.RestTemplateResponseEnvelope;
import com.kairos.controller.counters.CounterDistController;
import com.kairos.persistence.model.counter.Counter;
import com.kairos.persistence.model.counter.KPI;
import com.kairos.service.exception.ExceptionService;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = KairosActivityApplication.class, webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
public class CounterDistIntegrationTest {
    private Logger logger = LoggerFactory.getLogger(CounterManagementService.class);
   @Autowired
   ExceptionService exceptionService;
    @org.springframework.beans.factory.annotation.Value("${server.host.http.url}")
    private String url;
   @Autowired
   TestRestTemplate testRestTemplate;

   @Test
    public void case1_getAvailableKPIsListForCountry(){
       String baseUrl = getBaseUrl(2567l, null);
       ParameterizedTypeReference<RestTemplateResponseEnvelope<List<KPI>>> typeReference=new ParameterizedTypeReference<RestTemplateResponseEnvelope<List<KPI>>>() {
       };
       ResponseEntity<RestTemplateResponseEnvelope<List<KPI>>> response=testRestTemplate.exchange(baseUrl+"/counter/dist/counters/country/4", HttpMethod.GET,null,typeReference);
       logger.info("Status Code : " + response.getStatusCode());
       Assert.assertTrue(HttpStatus.OK.equals(response.getStatusCode()));
   }


    public final String getBaseUrl(Long organizationId, Long countryId) {
        if (organizationId != null && countryId != null) {
            String baseUrl = new StringBuilder(url + "/api/v1/organization/").append(organizationId)
                    .append("/country/").append(countryId).toString();
            return baseUrl;
        } else if (organizationId != null) {
            String baseUrl = new StringBuilder(url + "/api/v1/organization/").append(organizationId).toString();
            return baseUrl;
        } else {
            exceptionService.unsupportedOperationException("message.organization.id.notnull");

        }
        return null;
    }


}
