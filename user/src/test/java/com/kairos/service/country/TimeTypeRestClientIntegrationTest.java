package com.kairos.service.country;

import com.kairos.UserServiceApplication;
import com.kairos.activity.client.dto.RestTemplateResponseEnvelope;
import com.kairos.persistence.model.timetype.TimeTypeDTO;
import com.kairos.util.DateUtil;
import org.junit.Assert;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.MethodSorters;
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
import org.springframework.web.util.UriComponentsBuilder;

import java.util.Date;

/**
 * Created by vipul on 1/11/17.
 */

@RunWith(SpringRunner.class)
@SpringBootTest(classes = UserServiceApplication.class, webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class TimeTypeRestClientIntegrationTest {
    @Value("${server.host.http.url}")
    private String url;
    @Autowired
    TestRestTemplate restTemplate;

    static Long createdId;
    static Long createdIdForDelete;
    String name = "ABC" + DateUtil.getCurrentDate().toString();

    /*@Test
    public void test1_addTimeType() throws Exception {
        String baseUrl = getBaseUrl(71L, 53L);
        UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(baseUrl + "/timeType/");
        String expectedUrl = "http://localhost:8095/kairos/user/api/v1/organization/71/country/53/timeType";
        TimeTypeDTO timeTypeDTO = new TimeTypeDTO(name, "PRESENCE DAY", false, false, false, false);
        HttpEntity<TimeTypeDTO> entity = new HttpEntity<>(timeTypeDTO);
        ParameterizedTypeReference<RestTemplateResponseEnvelope<TimeTypeDTO>> typeReference =
                new ParameterizedTypeReference<RestTemplateResponseEnvelope<TimeTypeDTO>>() {
                };
        ResponseEntity<RestTemplateResponseEnvelope<TimeTypeDTO>> response = restTemplate.exchange(
                baseUrl + "/timeType",
                HttpMethod.POST, entity, typeReference);

        Assert.assertTrue(HttpStatus.CREATED.equals(response.getStatusCode()) || HttpStatus.CONFLICT.equals(response.getStatusCode()));
        Assert.assertTrue(HttpStatus.CREATED.equals(response.getStatusCode()));
        createdIdForDelete = createdId = response.getBody().getData().getId();

    }*/

/*    @Test
    public void test2_getAllTimeTypes() throws Exception {
        String baseUrl = getBaseUrl(71L, 53L);
        UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(baseUrl + "/timeType/");
        ResponseEntity<String> response = restTemplate.exchange(
                builder.toUriString(),
                HttpMethod.GET, null, String.class);
        Assert.assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @Test

    public void test4_deleteTimeType() throws Exception {
        String baseUrl = getBaseUrl(71L, 53L);
        UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(baseUrl + "/timeType/" + createdIdForDelete);
        ResponseEntity<String> response = restTemplate.exchange(
                builder.toUriString(),
                HttpMethod.DELETE, null, String.class);
        Assert.assertEquals(HttpStatus.OK, response.getStatusCode());

    }*/

/*
    @Test
    public void test3_updateTimeType() throws Exception {
        String baseUrl = getBaseUrl(71L, 53L);
        UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(baseUrl + "/timeType/" + createdId);
        name = "ABC" + DateUtil.getCurrentDate().toString();
        TimeTypeDTO timeTypeDTO = new TimeTypeDTO(name, "PRESENCE DAY", false, false, false, false);
        HttpEntity<TimeTypeDTO> entity = new HttpEntity<>(timeTypeDTO);
        ResponseEntity<TimeTypeDTO> response = restTemplate.exchange(
                builder.toUriString(),
                HttpMethod.PUT, entity, TimeTypeDTO.class);
        Assert.assertTrue(HttpStatus.OK.equals(response.getStatusCode()));

    }
*/

    public final String getBaseUrl(Long organizationId, Long countryId) {
        if (organizationId != null && countryId != null) {
            String baseUrl = new StringBuilder(url + "/api/v1/organization/").append(organizationId)
                    .append("/country/").append(countryId).toString();
            return baseUrl;
        } else if (organizationId != null) {
            String baseUrl = new StringBuilder(url + "/api/v1/organization/").append(organizationId).toString();
            return baseUrl;
        } else {
            throw new UnsupportedOperationException("organization ID must not be null");
        }

    }
}