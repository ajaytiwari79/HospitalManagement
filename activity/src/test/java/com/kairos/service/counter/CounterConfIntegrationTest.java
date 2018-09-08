package com.kairos.service.counter;

import com.kairos.KairosActivityApplication;
import com.kairos.commons.client.RestTemplateResponseEnvelope;
import com.kairos.dto.activity.counter.FilterCriteria;
import com.kairos.dto.activity.counter.distribution.category.KPICategoryUpdationDTO;
import com.kairos.dto.activity.counter.enums.CounterType;
import com.kairos.persistence.model.counter.Counter;
import com.kairos.persistence.model.counter.KPICategory;
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

import static com.kairos.constants.ApiConstants.COUNTER_CONF_URL;

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
    public void addCounterEntries() throws Exception{
        String baseUrl = getBaseUrl(2567l, 5l);
        ResponseEntity<String> response = restTemplate.exchange(
                baseUrl + COUNTER_CONF_URL+"/counter",
                HttpMethod.GET, null, String.class);
        logger.info("Status Code : " + response.getStatusCode());
        Assert.assertTrue(HttpStatus.OK.equals(response.getStatusCode()));
    }


    @Test
    public void addCounter() throws Exception {
        Counter counter = new Counter("test", CounterType.WORKING_HOUR_PER_SHIFT, false, BigInteger.valueOf(5));
        HttpEntity<Counter> requestBodyData = new HttpEntity<>(counter);
        String baseUrl = getBaseUrl(2567l, 4l);
        ParameterizedTypeReference<RestTemplateResponseEnvelope<Counter>> typeReference =
                new ParameterizedTypeReference<RestTemplateResponseEnvelope<Counter>>() {
                };
        ResponseEntity<RestTemplateResponseEnvelope<Counter>> response = restTemplate.exchange(
                baseUrl + COUNTER_CONF_URL+"/counter",
                HttpMethod.POST, requestBodyData, typeReference);
        logger.info("Status Code : " + response.getStatusCode());
        Assert.assertTrue(HttpStatus.OK.equals(response.getStatusCode()));
    }


    //Todo harish to be modify this code
    @Test
    public void updateCounterCriteria() throws Exception {
        List<FilterCriteria> filterCriterias = new ArrayList<>();
        HttpEntity<List<FilterCriteria>> requestBodyData = new HttpEntity<>(filterCriterias);
        String baseUrl = getBaseUrl(2567l, 4l);
        ParameterizedTypeReference<RestTemplateResponseEnvelope<List<FilterCriteria>>> typeReference =
                new ParameterizedTypeReference<RestTemplateResponseEnvelope<List<FilterCriteria>>>() {
                };
        ResponseEntity<RestTemplateResponseEnvelope<List<FilterCriteria>>> response = restTemplate.exchange(
                baseUrl + COUNTER_CONF_URL+"/counter/5",
                HttpMethod.PUT, requestBodyData, typeReference);
        logger.info("Status Code : " + response.getStatusCode());
        Assert.assertTrue(HttpStatus.OK.equals(response.getStatusCode()));
    }

    @Test
    public void addCategoriesAtCountryLevel() throws Exception {
        List<KPICategory> kpiCategories=new ArrayList<>();
        kpiCategories.add(new KPICategory("Working Time after update", null,null,null));
        kpiCategories.add(new KPICategory("Resting Time after update", null,null,null));
        String baseUrl = getBaseUrl(2567l, 4l);
        HttpEntity<List<KPICategory>> requestBodyData = new HttpEntity<>(kpiCategories);
        ParameterizedTypeReference<RestTemplateResponseEnvelope<List<KPICategory>>> typeReference =
                new ParameterizedTypeReference<RestTemplateResponseEnvelope<List<KPICategory>>>() {
                };
        ResponseEntity<RestTemplateResponseEnvelope<List<KPICategory>>> response = restTemplate.exchange(
                baseUrl+COUNTER_CONF_URL+"/category", HttpMethod.POST, requestBodyData, typeReference);
        logger.info("Status Code : " + response.getStatusCode()+""+kpiCategories);
        Assert.assertTrue(HttpStatus.OK.equals(response.getStatusCode()));
    }

    @Test
    public void addCategoriesAtUnitLevel() throws Exception{
        List<KPICategory> kpiCategories=new ArrayList<>();
        kpiCategories.add(new KPICategory("Working Time for unit for again ", null,null,null));
        kpiCategories.add(new KPICategory("Resting Time for unit for again", null,null,null));
        String baseUrl = getBaseUrl(2567l, null);
        HttpEntity<List<KPICategory>> requestBodyData = new HttpEntity<>(kpiCategories);
        ParameterizedTypeReference<RestTemplateResponseEnvelope<List<KPICategory>>> typeReference =
                new ParameterizedTypeReference<RestTemplateResponseEnvelope<List<KPICategory>>>() {
                };
        ResponseEntity<RestTemplateResponseEnvelope<List<KPICategory>>> response = restTemplate.exchange(
                baseUrl+"/unit/4/counter/conf/category", HttpMethod.POST, requestBodyData, typeReference);
        logger.info("Status Code : " + response.getStatusCode()+""+kpiCategories);
        Assert.assertTrue(HttpStatus.OK.equals(response.getStatusCode()));
    }

    @Test
    public void updateCategoriesForCountry() throws Exception{
        List<KPICategory> kpiCategories=new ArrayList<>();
        List<KPICategory> kpiCategories1=new ArrayList<>();
        KPICategory kpiCategory1=new KPICategory("Working Time for Country after update ", null,null,null);
        KPICategory kpiCategory2=new KPICategory("Resting Time for Country after update", null,null,null);
        kpiCategory1.setId(BigInteger.valueOf(20));
        kpiCategory2.setId(BigInteger.valueOf(21));
        kpiCategories.add(kpiCategory1);
        kpiCategories.add(kpiCategory2);
        KPICategory kpiCategory3=new KPICategory("", null,null,null);
        kpiCategory3.setId(BigInteger.valueOf(14));
        kpiCategories1.add(kpiCategory3);
        KPICategoryUpdationDTO kpiCategoryUpdationDTO=new KPICategoryUpdationDTO(kpiCategories,kpiCategories1);
        String baseUrl=getBaseUrl(2567l,4l);
        HttpEntity<KPICategoryUpdationDTO> requestBodyDate=new HttpEntity<>(kpiCategoryUpdationDTO);
        ParameterizedTypeReference<RestTemplateResponseEnvelope<List<KPICategory>>> typeReference=new ParameterizedTypeReference<RestTemplateResponseEnvelope<List<KPICategory>>>() {
        };
        ResponseEntity<RestTemplateResponseEnvelope<List<KPICategory>>> response=restTemplate.exchange(baseUrl+COUNTER_CONF_URL+"/category",HttpMethod.PUT,requestBodyDate,typeReference);
        logger.info("Status Code : " + response.getStatusCode()+""+kpiCategories);
        Assert.assertTrue(HttpStatus.OK.equals(response.getStatusCode()));
    }


    @Test
    public void updateCategoriesForUnit() throws Exception {
        List<KPICategory> kpiCategories = new ArrayList<>();
        kpiCategories.add(new KPICategory("Working Time for unit ", null,null,null));
        kpiCategories.add(new KPICategory("Resting Time for unit", null,null,null));
        KPICategoryUpdationDTO kpiCategoryUpdationDTO = new KPICategoryUpdationDTO(new ArrayList(), kpiCategories);
        String baseUrl = getBaseUrl(2567l, null);
        HttpEntity<KPICategoryUpdationDTO> requestBodyDate = new HttpEntity<>(kpiCategoryUpdationDTO);
        ParameterizedTypeReference<RestTemplateResponseEnvelope<List<KPICategory>>> typeReference = new ParameterizedTypeReference<RestTemplateResponseEnvelope<List<KPICategory>>>() {
        };
        ResponseEntity<RestTemplateResponseEnvelope<List<KPICategory>>> response = restTemplate.exchange(baseUrl + "/unit/4/counter/conf/category", HttpMethod.PUT, requestBodyDate, typeReference);
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
