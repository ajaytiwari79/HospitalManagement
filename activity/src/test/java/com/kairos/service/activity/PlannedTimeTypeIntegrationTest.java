package com.kairos.service.activity;

import com.kairos.KairosActivityApplication;
import com.kairos.dto.activity.planned_time_type.PresenceTypeDTO;
import com.kairos.rest_client.RestTemplateResponseEnvelope;
import com.kairos.service.exception.ExceptionService;
import org.junit.Assert;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.MethodSorters;
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
import java.util.Date;
import java.util.List;

/*
 * @author: mohit shakya
 * @usage: Integration test cases for planned time type functionality.
 */

@RunWith(SpringRunner.class)
@SpringBootTest(classes = KairosActivityApplication.class, webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class PlannedTimeTypeIntegrationTest {
    @Value("${server.host.http.url}")
    private String url;
    @Inject
    private TestRestTemplate testRestTemplate;
    @Inject
    private ExceptionService exceptionService;
    static BigInteger createdId;

    @Test
    public void case1_createPlannedTimeType() throws Exception{
        String baseUrl = getBaseUrl(24L, 4L);
        PresenceTypeDTO presenceTypeDTO = new PresenceTypeDTO("PlannedTimeType01"+(new Date().getTime()));
        HttpEntity<PresenceTypeDTO> requestEntity = new HttpEntity<>(presenceTypeDTO);
        ParameterizedTypeReference<RestTemplateResponseEnvelope<PresenceTypeDTO>> typeReference =
                new ParameterizedTypeReference<RestTemplateResponseEnvelope<PresenceTypeDTO>>() {};
        ResponseEntity<RestTemplateResponseEnvelope<PresenceTypeDTO>> response = testRestTemplate.exchange(baseUrl, HttpMethod.POST, requestEntity, typeReference);
        System.out.println("reponse: "+response.getStatusCode());
        Assert.assertEquals(HttpStatus.CREATED, response.getStatusCode());
        createdId = response.getBody().getData().getId();
    }

    @Test
    public void case2_getAllPlannedTimeType() throws Exception{
        String baseUrl = getBaseUrl(24L, 4L);
        ParameterizedTypeReference<RestTemplateResponseEnvelope<List<PresenceTypeDTO>>> typeReference = new ParameterizedTypeReference<RestTemplateResponseEnvelope<List<PresenceTypeDTO>>>() {
        };
        ResponseEntity<RestTemplateResponseEnvelope<List<PresenceTypeDTO>>> response = testRestTemplate.exchange(baseUrl, HttpMethod.GET, null, typeReference,24,4);
        System.out.println("reponse: "+response.getStatusCode());
        Assert.assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @Test
    public void case3_updatePlannedTimeType() throws Exception{
        String baseUrl = getBaseUrl(24L, 4L)+"/"+createdId;
        System.out.println("baseUrl: "+baseUrl);
        PresenceTypeDTO presenceTypeDTO = new PresenceTypeDTO("PlannedTimeType-updated"+createdId, createdId);
        HttpEntity<PresenceTypeDTO> requestEntity = new HttpEntity<>(presenceTypeDTO);
        ParameterizedTypeReference<RestTemplateResponseEnvelope<PresenceTypeDTO>> responseRef = new ParameterizedTypeReference<RestTemplateResponseEnvelope<PresenceTypeDTO>>() {
        };
        ResponseEntity<RestTemplateResponseEnvelope<PresenceTypeDTO>> response = testRestTemplate.exchange(baseUrl, HttpMethod.PUT, requestEntity, responseRef);
        System.out.println("reponse: "+response.getStatusCode());
        Assert.assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @Test
    public void case4_deletePlannedTimeType() throws Exception {
        String baseUrl = getBaseUrl(24L, 4L)+"/"+createdId;
        ParameterizedTypeReference<RestTemplateResponseEnvelope<PresenceTypeDTO>> responseRef = new ParameterizedTypeReference<RestTemplateResponseEnvelope<PresenceTypeDTO>>() {
        };
        ResponseEntity<RestTemplateResponseEnvelope<PresenceTypeDTO>> response = testRestTemplate.exchange(baseUrl, HttpMethod.DELETE, null, responseRef);
        System.out.println("reponse: "+response.getStatusCode());
        Assert.assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    public final String getBaseUrl(Long organizationId, Long countryId) throws Exception{
        if (organizationId != null && countryId != null) {
            String baseUrl = new StringBuilder(url + "/api/v1/organization/").append(organizationId)
                    .append("/country/").append(countryId).append("/plannedTimeType").toString();
            return baseUrl;
        } else if (organizationId != null) {
            String baseUrl = new StringBuilder(url + "/api/v1/organization/").append(organizationId).append("/plannedTimeType").toString();
            return baseUrl;
        } else {
            exceptionService.unsupportedOperationException("message.organization.id.notnull");

        }
        return null;
    }
}
