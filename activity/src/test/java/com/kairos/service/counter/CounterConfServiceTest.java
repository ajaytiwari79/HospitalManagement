package com.kairos.service.counter;

import com.google.inject.Inject;
import com.kairos.KairosActivityApplication;
import com.kairos.activity.counter.enums.CounterType;
import com.kairos.client.dto.RestTemplateResponseEnvelope;
import com.kairos.persistence.model.counter.Counter;
import com.kairos.persistence.model.counter.KPI;
import com.kairos.persistence.repository.counter.CounterRepository;
import com.kairos.service.exception.ExceptionService;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit4.SpringRunner;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = KairosActivityApplication.class, webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
public class CounterConfServiceTest {
    private Logger logger = LoggerFactory.getLogger(CounterConfService.class);
    @Autowired
    ExceptionService exceptionService;
    @Autowired
    CounterConfService counterConfService;
    @Autowired
    @Value("${server.host.http.url}")
    private String url;
    @Autowired
    TestRestTemplate restTemplate;

//    @Test
//    public void case1_addEntries() {
//        counterConfService.addEntries(4l);
//    }

    @Test
    public void case2_addCounter(){
        Counter counter=new Counter("test", CounterType.WORKING_HOUR_PER_SHIFT,false,BigInteger.valueOf(5));
        String baseUrl = getBaseUrl(2567l, 4l);
        HttpEntity<Counter> requestBodyData = new HttpEntity<>(counter);
        ParameterizedTypeReference<RestTemplateResponseEnvelope<Counter>> typeReference =
                new ParameterizedTypeReference<RestTemplateResponseEnvelope<Counter>>() {
                };
    ResponseEntity<RestTemplateResponseEnvelope<Counter>> response = restTemplate.exchange(
            baseUrl + "/counters/conf/counter",
            HttpMethod.POST, requestBodyData, typeReference);
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
