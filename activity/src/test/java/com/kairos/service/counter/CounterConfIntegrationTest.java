package com.kairos.service.counter;

import com.kairos.KairosActivityApplication;
import com.kairos.activity.counter.FilterCriteria;
import com.kairos.activity.counter.KPICategoryUpdationDTO;
import com.kairos.activity.counter.enums.CounterType;
import com.kairos.enums.FilterType;
import com.kairos.persistence.model.counter.Counter;
import com.kairos.persistence.model.counter.KPICategory;
import com.kairos.rest_client.RestTemplateResponseEnvelope;
import com.kairos.service.exception.ExceptionService;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit4.SpringRunner;

import javax.inject.Inject;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = KairosActivityApplication.class,webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
public class CounterConfIntegrationTest {
    private Logger logger = LoggerFactory.getLogger(CounterConfIntegrationTest.class);
    @Value("${server.host.http.url}")
    private String url;
    @Inject
    private TestRestTemplate restTemplate;
    @Inject
    private ExceptionService exceptionService;

    @Test
    public void addCounter() throws Exception {
        Counter counter = new Counter("test", CounterType.WORKING_HOUR_PER_SHIFT, false, BigInteger.valueOf(5));
        HttpEntity<Counter> requestBodyData = new HttpEntity<>(counter);
        String baseUrl = getBaseUrl(2567l, 4l);
        ParameterizedTypeReference<com.kairos.client.dto.RestTemplateResponseEnvelope<Counter>> typeReference =
                new ParameterizedTypeReference<com.kairos.client.dto.RestTemplateResponseEnvelope<Counter>>() {
                };
        ResponseEntity<com.kairos.client.dto.RestTemplateResponseEnvelope<Counter>> response = restTemplate.exchange(
                baseUrl + "/counters/conf/counter",
                HttpMethod.POST, requestBodyData, typeReference);
        logger.info("Status Code : " + response.getStatusCode());
        Assert.assertTrue(HttpStatus.OK.equals(response.getStatusCode()));
    }

    @Test
    public void case3_addCategoriesAtCountryLevel() throws Exception {
        List<KPICategory> kpiCategories=new ArrayList<>();
        kpiCategories.add(new KPICategory("Working Time", 1l));
        kpiCategories.add(new KPICategory("Resting Time", 2l));
        String baseUrl = getBaseUrl(2567l, null);
        HttpEntity<List<KPICategory>> requestBodyData = new HttpEntity<>(kpiCategories);
        ParameterizedTypeReference<com.kairos.client.dto.RestTemplateResponseEnvelope<List<KPICategory>>> typeReference =
                new ParameterizedTypeReference<com.kairos.client.dto.RestTemplateResponseEnvelope<List<KPICategory>>>() {
                };
        ResponseEntity<com.kairos.client.dto.RestTemplateResponseEnvelope<List<KPICategory>>> response = restTemplate.exchange(
                baseUrl+"/counters/conf/country/4/category", HttpMethod.POST, requestBodyData, typeReference);
        logger.info("Status Code : " + response.getStatusCode()+""+kpiCategories);
        Assert.assertTrue(HttpStatus.OK.equals(response.getStatusCode()));
    }

    @Test
    public void addCategoriesAtUnitLevel() throws Exception{
        List<KPICategory> kpiCategories=new ArrayList<>();
        kpiCategories.add(new KPICategory("Working Time for unit ", 1l));
        kpiCategories.add(new KPICategory("Resting Time for unit", 2l));
        String baseUrl = getBaseUrl(2567l, null);
        HttpEntity<List<KPICategory>> requestBodyData = new HttpEntity<>(kpiCategories);
        ParameterizedTypeReference<com.kairos.client.dto.RestTemplateResponseEnvelope<List<KPICategory>>> typeReference =
                new ParameterizedTypeReference<com.kairos.client.dto.RestTemplateResponseEnvelope<List<KPICategory>>>() {
                };
        ResponseEntity<com.kairos.client.dto.RestTemplateResponseEnvelope<List<KPICategory>>> response = restTemplate.exchange(
                baseUrl+"/counters/conf/unit/4/category", HttpMethod.POST, requestBodyData, typeReference);
        logger.info("Status Code : " + response.getStatusCode()+""+kpiCategories);
        Assert.assertTrue(HttpStatus.OK.equals(response.getStatusCode()));
    }

    @Test
    public void updateCategoriesForCountry() throws Exception{
        List<KPICategory> kpiCategories=new ArrayList<>();
        KPICategory kpiCategory1=new KPICategory("Working Time for Country ", 3l);
        KPICategory kpiCategory2=new KPICategory("Resting Time for Country", 4l);
        kpiCategory1.setId(BigInteger.valueOf(11));
        kpiCategory2.setId(BigInteger.valueOf(12));
        kpiCategories.add(kpiCategory1);
        kpiCategories.add(kpiCategory2);
        KPICategoryUpdationDTO kpiCategoryUpdationDTO=new KPICategoryUpdationDTO(new ArrayList(),kpiCategories);
        String baseUrl=getBaseUrl(2567l,null);
        HttpEntity<KPICategoryUpdationDTO> requestBodyDate=new HttpEntity<>(kpiCategoryUpdationDTO);
        ParameterizedTypeReference<com.kairos.client.dto.RestTemplateResponseEnvelope<List<KPICategory>>> typeReference=new ParameterizedTypeReference<com.kairos.client.dto.RestTemplateResponseEnvelope<List<KPICategory>>>() {
        };
        ResponseEntity<com.kairos.client.dto.RestTemplateResponseEnvelope<List<KPICategory>>> response=restTemplate.exchange(baseUrl+"/counters/conf/country/4/category",HttpMethod.PUT,requestBodyDate,typeReference);
        logger.info("Status Code : " + response.getStatusCode()+""+kpiCategories);
        Assert.assertTrue(HttpStatus.OK.equals(response.getStatusCode()));
    }


    @Test
    public void updateCategoriesForUnit() throws Exception {
        List<KPICategory> kpiCategories = new ArrayList<>();
        kpiCategories.add(new KPICategory("Working Time for unit ", 3l));
        kpiCategories.add(new KPICategory("Resting Time for unit", 4l));
        KPICategoryUpdationDTO kpiCategoryUpdationDTO = new KPICategoryUpdationDTO(new ArrayList(), kpiCategories);
        String baseUrl = getBaseUrl(2567l, null);
        HttpEntity<KPICategoryUpdationDTO> requestBodyDate = new HttpEntity<>(kpiCategoryUpdationDTO);
        ParameterizedTypeReference<com.kairos.client.dto.RestTemplateResponseEnvelope<List<KPICategory>>> typeReference = new ParameterizedTypeReference<com.kairos.client.dto.RestTemplateResponseEnvelope<List<KPICategory>>>() {
        };
        ResponseEntity<com.kairos.client.dto.RestTemplateResponseEnvelope<List<KPICategory>>> response = restTemplate.exchange(baseUrl + "/counters/conf/unit/4/category", HttpMethod.PUT, requestBodyDate, typeReference);
        logger.info("Status Code : " + response.getStatusCode() + "" + kpiCategories);
        Assert.assertTrue(HttpStatus.OK.equals(response.getStatusCode()));
    }
    public final String getBaseUrl(Long organizationId, Long countryId)throws Exception  {
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
