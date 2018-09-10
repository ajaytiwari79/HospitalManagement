package com.kairos.service.country.default_data;

import com.kairos.UserServiceApplication;
import com.kairos.commons.client.RestTemplateResponseEnvelope;
import com.kairos.config.OrderTestRunner;
import com.kairos.persistence.model.country.default_data.UnitTypeQueryResult;
import com.kairos.dto.user.country.system_setting.UnitTypeDTO;
import org.junit.Assert;
import org.junit.Before;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.MethodSorters;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;

import javax.inject.Inject;

import java.util.List;

@RunWith(OrderTestRunner.class)
@SpringBootTest(classes = UserServiceApplication.class, webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
@ActiveProfiles(profiles = "test")
public class UnitTypeServiceIntegrationTest {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Value("${server.host.http.url}")
    private String url;
    @Inject
    TestRestTemplate restTemplate;
    static Long createdUnitTypeId;
    static Long orgId = 24L;
    static Long countryId = 4L;
    static String baseUrlWithCountry;

    @Before
    public void setUp() throws Exception {
        baseUrlWithCountry = getBaseUrl(orgId, countryId);

    }

    @Test
    @Order(1)
    public void addUnitTypeInCountry() {
        UnitTypeDTO unitTypeDTO = new UnitTypeDTO("first", "first desc");
        HttpEntity<UnitTypeDTO> requestBodyData = new HttpEntity<>(unitTypeDTO);
        ParameterizedTypeReference<RestTemplateResponseEnvelope<UnitTypeDTO>> typeReference =
                new ParameterizedTypeReference<RestTemplateResponseEnvelope<UnitTypeDTO>>() {
                };
        ResponseEntity<RestTemplateResponseEnvelope<UnitTypeDTO>> response = restTemplate.exchange(
                baseUrlWithCountry + "/unit_type",
                HttpMethod.POST, requestBodyData, typeReference);
        Assert.assertTrue(HttpStatus.CREATED.equals(response.getStatusCode()));
        createdUnitTypeId = response.getBody().getData().getId();
        logger.info(response.getBody().getData().getId() + "");
    }

    @Test
    @Order(2)
    public void getAllUnitTypeOfCountry() {
        ParameterizedTypeReference<RestTemplateResponseEnvelope<List<UnitTypeQueryResult>>> typeReference =
                new ParameterizedTypeReference<RestTemplateResponseEnvelope<List<UnitTypeQueryResult>>>() {
                };
        ResponseEntity<RestTemplateResponseEnvelope<List<UnitTypeQueryResult>>> response = restTemplate.exchange(
                baseUrlWithCountry + "/unit_type",
                HttpMethod.GET, null, typeReference);
        logger.info(response.toString());
        Assert.assertTrue(HttpStatus.OK.equals(response.getStatusCode()));
    }

    @Test
    @Order(3)
    public void updateUnitTypeOfCountry() {
        createdUnitTypeId = createdUnitTypeId != null ? createdUnitTypeId : 22172L; // change this accordingly
        UnitTypeDTO unitTypeDTO = new UnitTypeDTO("firstUpdate", "first desc");
        unitTypeDTO.setId(createdUnitTypeId);
        HttpEntity<UnitTypeDTO> requestBodyData = new HttpEntity<>(unitTypeDTO);
        ParameterizedTypeReference<RestTemplateResponseEnvelope<UnitTypeDTO>> typeReference =
                new ParameterizedTypeReference<RestTemplateResponseEnvelope<UnitTypeDTO>>() {
                };
        ResponseEntity<RestTemplateResponseEnvelope<UnitTypeDTO>> response = restTemplate.exchange(
                baseUrlWithCountry + "/unit_type/" + createdUnitTypeId,
                HttpMethod.PUT, requestBodyData, typeReference);
        Assert.assertTrue(HttpStatus.OK.equals(response.getStatusCode()));
        createdUnitTypeId = response.getBody().getData().getId();
        logger.info(response.getBody().getData().getId() + "");

    }

    public final String getBaseUrl(Long organizationId, Long countryId) {
        String baseUrl = new StringBuilder(url + "/api/v1/organization/").append(organizationId)
                .append("/country/").append(countryId).toString();
        return baseUrl;
    }

}