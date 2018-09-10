package com.kairos.service.country;

import com.kairos.UserServiceApplication;
import com.kairos.dto.activity.shift.FunctionDTO;
import com.kairos.commons.client.RestTemplateResponseEnvelope;
import org.junit.Assert;
import org.junit.Before;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.MethodSorters;
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

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * Created by pavan on 14/3/18.
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = UserServiceApplication.class,webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class FunctionServiceIntegrationTest {

    @Value("${server.host.http.url}")
    private String url ;
    @Autowired
    TestRestTemplate restTemplate;

    static FunctionDTO functionDTO=new FunctionDTO();
    static String baseUrlWithCountry;
    private final Logger logger = LoggerFactory.getLogger(FunctionServiceIntegrationTest.class);


    @Before
    public void setUp() throws Exception {
        baseUrlWithCountry = getBaseUrl(24L, 4L, null);
        logger.info("Base Url is:"+baseUrlWithCountry);

    }
    @Test
    public void createFunction() throws Exception {
        functionDTO.setName("Gruppeleder i integrerede ordninger");
        functionDTO.setDescription("function test Description");
        functionDTO.setStartDate(new Date(2015,12,10));
        functionDTO.setEndDate(new Date(2019,12,10));
        List<Long> unionIds=new ArrayList<>();
        unionIds.add(2597L);
        unionIds.add(1794L);
        functionDTO.setUnionIds(unionIds);
        List<Long> organizationLevelIds=new ArrayList<>();
        organizationLevelIds.add(1792L);
        organizationLevelIds.add(1800L);
        functionDTO.setOrganizationLevelIds(organizationLevelIds);
        HttpEntity<FunctionDTO> requestBodyData = new HttpEntity<>(functionDTO);
        ParameterizedTypeReference<RestTemplateResponseEnvelope<Map>> typeReference =
                new ParameterizedTypeReference<RestTemplateResponseEnvelope<Map>>() {
                };
        ResponseEntity<RestTemplateResponseEnvelope<Map>> response = restTemplate.exchange(
                baseUrlWithCountry + "/function",
                HttpMethod.POST, requestBodyData, typeReference);
        Assert.assertTrue(HttpStatus.OK.equals(response.getStatusCode()));


    }

    @Test
    public void getFunctions() throws Exception {
        ParameterizedTypeReference<RestTemplateResponseEnvelope<List<Map>>> typeReference =
                new ParameterizedTypeReference<RestTemplateResponseEnvelope<List<Map>>>() {
                };
        ResponseEntity<RestTemplateResponseEnvelope<List<Map>>> response = restTemplate.exchange(
                baseUrlWithCountry + "/functions",
                HttpMethod.GET, null, typeReference);
        Assert.assertTrue(HttpStatus.OK.equals(response.getStatusCode()));

    }

    @Test
    public void updateFunction() throws Exception {
        functionDTO.setId(1826L);
        functionDTO.setName("Gruppeleder i integrerede ordninger test ");
        functionDTO.setDescription("Test Description for Test Case");
        functionDTO.setStartDate(new Date(2018,10,10));
        functionDTO.setEndDate(new Date(2019,10,10));
        List<Long> unionIds=new ArrayList<>();
        unionIds.add(1794L);
        functionDTO.setUnionIds(unionIds);
        List<Long> organizationLevelIds=new ArrayList<>();
        organizationLevelIds.add(1792L);
        functionDTO.setOrganizationLevelIds(organizationLevelIds);
        HttpEntity<FunctionDTO> requestBodyData = new HttpEntity<>(functionDTO);
        ParameterizedTypeReference<RestTemplateResponseEnvelope<Map>> typeReference =
                new ParameterizedTypeReference<RestTemplateResponseEnvelope<Map>>() {
                };
        ResponseEntity<RestTemplateResponseEnvelope<Map>> response = restTemplate.exchange(
                baseUrlWithCountry + "/function/1822",
                HttpMethod.PUT, requestBodyData, typeReference);
        Assert.assertTrue(HttpStatus.OK.equals(response.getStatusCode()));


    }

    @Test
    public void deleteFunction() throws Exception {
        ResponseEntity<FunctionDTO> response = restTemplate.exchange(
                baseUrlWithCountry + "/function/1821",
                HttpMethod.DELETE, null, FunctionDTO.class);
        Assert.assertTrue(HttpStatus.OK.equals(response.getStatusCode()));
    }

    @Test
    public void getUnionAndOrganizationLevels() throws Exception {
        ParameterizedTypeReference<RestTemplateResponseEnvelope<List<Map>>> typeReference =
                new ParameterizedTypeReference<RestTemplateResponseEnvelope<List<Map>>>() {
                };
        ResponseEntity<RestTemplateResponseEnvelope<List<Map>>> response = restTemplate.exchange(
                baseUrlWithCountry + "/unions_and_levels",
                HttpMethod.GET, null, typeReference);
        Assert.assertTrue(HttpStatus.OK.equals(response.getStatusCode()));

    }

    public final String getBaseUrl(Long organizationId, Long countryId, Long unitId) {
        if (organizationId != null && countryId != null) {
            String baseUrl = new StringBuilder(url + "/api/v1/organization/").append(organizationId)
                    .append("/country/").append(countryId).toString();
            return baseUrl;
        } else if (organizationId != null && unitId != null) {
            String baseUrl = new StringBuilder(url + "/api/v1/organization/").append(organizationId)
                    .append("/unit/").append(unitId).toString();
            return baseUrl;
        } else {
            throw new UnsupportedOperationException("organization ID must not be null");
        }
    }

}