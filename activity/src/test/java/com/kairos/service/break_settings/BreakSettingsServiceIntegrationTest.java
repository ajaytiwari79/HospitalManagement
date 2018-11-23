package com.kairos.service.break_settings;

import com.kairos.KairosActivityApplication;
import com.kairos.rest_client.RestTemplateResponseEnvelope;
import com.kairos.dto.activity.break_settings.BreakSettingsDTO;
import org.junit.*;
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

import java.math.BigInteger;
import java.util.List;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = KairosActivityApplication.class, webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class BreakSettingsServiceIntegrationTest {

    private final Logger logger = LoggerFactory.getLogger(BreakSettingsServiceIntegrationTest.class);
    static String baseUrlForUnit;
    static BigInteger createdId = new BigInteger("1");

    @Before
    public void setUp() throws Exception {
        baseUrlForUnit = getBaseUrl(24L, null, 64L) + "/break";

    }

    @Value("${server.host.http.url}")
    private String url;

    @Autowired
    TestRestTemplate restTemplate;

    @Test
    public void case1_createBreakSettings() {
        BreakSettingsDTO breakSettingsDTO = new BreakSettingsDTO(1360L, 30L);
        HttpEntity<BreakSettingsDTO> entity = new HttpEntity<>(breakSettingsDTO);

        ParameterizedTypeReference<RestTemplateResponseEnvelope<BreakSettingsDTO>> typeReference =
                new ParameterizedTypeReference<RestTemplateResponseEnvelope<BreakSettingsDTO>>() {
                };
        ResponseEntity<RestTemplateResponseEnvelope<BreakSettingsDTO>> response = restTemplate.exchange(
                baseUrlForUnit,
                HttpMethod.POST, entity, typeReference);
        Assert.assertEquals(HttpStatus.CREATED, response.getStatusCode());
    }

    @Test
    public void case2_getBreakSettings() {
        ParameterizedTypeReference<RestTemplateResponseEnvelope<List<BreakSettingsDTO>>> typeReference =
                new ParameterizedTypeReference<RestTemplateResponseEnvelope<List<BreakSettingsDTO>>>() {
                };
        ResponseEntity<RestTemplateResponseEnvelope<List<BreakSettingsDTO>>> response = restTemplate.exchange(
                baseUrlForUnit,
                HttpMethod.GET, null, typeReference);

        Assert.assertEquals(HttpStatus.OK, response.getStatusCode());
    }


    @Test
    public void case3_updateBreakSettings() {
        BreakSettingsDTO breakSettingsDTO = new BreakSettingsDTO(500L, 30L);
        HttpEntity<BreakSettingsDTO> entity = new HttpEntity<>(breakSettingsDTO);

        ParameterizedTypeReference<RestTemplateResponseEnvelope<BreakSettingsDTO>> typeReference =
                new ParameterizedTypeReference<RestTemplateResponseEnvelope<BreakSettingsDTO>>() {
                };
        ResponseEntity<RestTemplateResponseEnvelope<BreakSettingsDTO>> response = restTemplate.exchange(
                baseUrlForUnit + "/" + createdId,
                HttpMethod.PUT, entity, typeReference);
        Assert.assertEquals(HttpStatus.ACCEPTED, response.getStatusCode());
    }

    @Test
    public void case4_removeBreakSettings() {
        ParameterizedTypeReference<RestTemplateResponseEnvelope<BreakSettingsDTO>> typeReference =
                new ParameterizedTypeReference<RestTemplateResponseEnvelope<BreakSettingsDTO>>() {
                };
        ResponseEntity<RestTemplateResponseEnvelope<BreakSettingsDTO>> response = restTemplate.exchange(
                baseUrlForUnit + "/" + createdId,
                HttpMethod.DELETE, null, typeReference);

        Assert.assertEquals(HttpStatus.RESET_CONTENT, response.getStatusCode());
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