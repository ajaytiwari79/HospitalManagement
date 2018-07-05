package com.kairos.service.counter;

import com.kairos.KairosActivityApplication;
import com.kairos.activity.counter.FilterCriteria;
import com.kairos.enums.CounterType;
import com.kairos.enums.FilterType;
import com.kairos.persistence.model.counter.Counter;
import com.kairos.rest_client.RestTemplateResponseEnvelope;
import com.kairos.service.exception.ExceptionService;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit4.SpringRunner;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.List;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = KairosActivityApplication.class,webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
public class CounterConfIntegrationTest {
    @Value("${server.host.http.url}")
    private String url;
    @Inject
    private TestRestTemplate testRestTemplate;
    @Inject
    private ExceptionService exceptionService;

    @Test
    public void addCounter() throws Exception {
        String baseUrl = getBaseUrl(24L, 4L);
        List<FilterCriteria> criteriaList = new ArrayList<FilterCriteria>();
        FilterCriteria filter = new FilterCriteria(FilterType.TIME_TYPE, new ArrayList<>());
        filter.getValues().add(null);
        criteriaList.add(filter);
        filter = new FilterCriteria(FilterType.PLANNED_TIME_TYPE, new ArrayList<>());
        filter.getValues().add(null);
        Counter myCounter = new Counter(CounterType.RESTING_HOURS_PER_PRESENCE_DAY, criteriaList);
        HttpEntity<Counter> requestEntity = new HttpEntity<>(myCounter);
        ParameterizedTypeReference<RestTemplateResponseEnvelope<Object>> typeReference = new ParameterizedTypeReference<RestTemplateResponseEnvelope<Object>>() {};
        ResponseEntity<RestTemplateResponseEnvelope<Object>> response = testRestTemplate.exchange(baseUrl, HttpMethod.POST, requestEntity, typeReference);
        System.out.println("response: "+response.getStatusCode());
        Assert.assertEquals(200, response.getStatusCode());
    }

    public final String getBaseUrl(Long organizationId, Long countryId) throws Exception{
        if (organizationId != null && countryId != null) {
            String baseUrl = new StringBuilder(url + "/api/v1/organization/").append(organizationId)
                    .append("/country/").append(countryId).append("/counters/conf").toString();
            return baseUrl;
        } else {
            exceptionService.unsupportedOperationException("message.organization.id.notnull");

        }
        return null;
    }
}
