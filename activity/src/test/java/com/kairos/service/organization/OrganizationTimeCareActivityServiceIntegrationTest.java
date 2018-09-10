package com.kairos.service.organization;

import com.kairos.KairosActivityApplication;
import com.kairos.dto.activity.shift.ShiftDTO;
import com.kairos.commons.client.RestTemplateResponseEnvelope;
import com.kairos.wrapper.activity.ActivityTabsWrapper;
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
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.web.util.UriComponentsBuilder;

import javax.inject.Inject;
import java.math.BigInteger;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

/**
 * Created by vipul on 5/12/17.
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = KairosActivityApplication.class, webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class OrganizationTimeCareActivityServiceIntegrationTest {
    private final Logger logger = LoggerFactory.getLogger(OrganizationTimeCareActivityServiceIntegrationTest.class);
    @Value("${server.host.http.url}")
    private String url;
    @Autowired
    private TestRestTemplate restTemplate;
    @Inject
    private OrganizationActivityService organizationActivityService;
    static BigInteger createdId;
    List<ShiftDTO> subShift = new ArrayList<>();
    Date s = new Date("2018-01-19T05:37:38.922Z");

    @Before
    public void setUp() throws Exception {
        String baseUrl = getBaseUrl(71L, 145L);
        /*ShiftDTO shiftDTO = new ShiftDTO("MyShift", s, s, 1, 1, 2, 23, 1, 12, "Done", new BigInteger("2"), 95L, 1005L, subShift);*/
        UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(baseUrl + "/activity/7/copy-settings").queryParam("type", "organization").queryParam("checked", true);
        TimeZone tz = TimeZone.getTimeZone("UTC");
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm'Z'"); // Quoted "Z" to indicate UTC, no timezone offset
        df.setTimeZone(tz);
        String nowAsISO = df.format(new Date());

    }

    @Test
    public void test1_copyActivityType() throws Exception {
        String baseUrl = getBaseUrl(71L, 145L);
        UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(baseUrl + "/activity/7/copy-settings").queryParam("type", "organization").queryParam("checked", true);
        ParameterizedTypeReference<RestTemplateResponseEnvelope<ActivityTabsWrapper>> typeReference =
                new ParameterizedTypeReference<RestTemplateResponseEnvelope<ActivityTabsWrapper>>() {
                };
        ResponseEntity<RestTemplateResponseEnvelope<ActivityTabsWrapper>> response = restTemplate.exchange(
                baseUrl + "/activity/7/copy-settings?type=organization&checked=true", HttpMethod.GET, null, typeReference);
        Assert.assertTrue(HttpStatus.OK.equals(response.getStatusCode()));
        createdId = response.getBody().getData().getActivityId();
        logger.info("response", response + " New Id" + createdId);
    }

    @Test
    public void test2_removeActivityType() throws Exception {
        String baseUrl = getBaseUrl(71L, 145L);
        UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(baseUrl + "/activity/" + createdId + "/copy-settings").queryParam("type", "organization").queryParam("checked", false);
        ResponseEntity<String> response = restTemplate.exchange(
                builder.toUriString(),
                HttpMethod.GET, null, String.class);
        logger.info("response", response);
        Assert.assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @Test
    public void test_getActivityTypeMappingDetails() throws Exception {
        String baseUrl = getBaseUrl(71L, 145L);
        UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(baseUrl + "/activity_type_mapping").queryParam("type", "organization");
        ResponseEntity<String> response = restTemplate.exchange(
                builder.toUriString(),
                HttpMethod.GET, null, String.class);
        logger.info("response", response);
        Assert.assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @Test
    public void test_getActivityTypeByUnitId() throws Exception {
        String baseUrl = getBaseUrl(71L, 145L);
        UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(baseUrl + "/activity_type_with_selected").queryParam("type", "organization");
        ResponseEntity<String> response = restTemplate.exchange(
                builder.toUriString(),
                HttpMethod.GET, null, String.class);
        logger.info("response", response);
        Assert.assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    public final String getBaseUrl(Long organizationId, Long unitId) {
        if (organizationId != null && unitId != null) {
            String baseUrl = new StringBuilder(url + "/api/v1/organization/").append(organizationId)
                    .append("/unit/").append(unitId).toString();
            ;
            return baseUrl;
        } else if (organizationId != null) {
            String baseUrl = new StringBuilder(url + "/api/v1/organization/").append(organizationId).toString();
            return baseUrl;
        } else {
            throw new UnsupportedOperationException("ogranization ID must not be null");
        }

    }

}