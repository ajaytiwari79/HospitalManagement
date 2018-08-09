package com.kairos.service.country.default_data;

import com.kairos.UserServiceApplication;
import com.kairos.client.dto.RestTemplateResponseEnvelope;
import com.kairos.config.OrderTestRunner;
import com.kairos.service.country.equipment.EquipmentService;
import com.kairos.service.exception.ExceptionService;
import com.kairos.user.country.system_setting.UnitTypeDTO;
import com.kairos.user.reason_code.ReasonCodeDTO;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
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

import javax.inject.Inject;

import static org.junit.Assert.*;

@RunWith(OrderTestRunner.class)
@SpringBootTest(classes = UserServiceApplication.class, webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
public class UnitTypeServiceIntegrationTest {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());


    static private String url;
    @Inject
    TestRestTemplate restTemplate;
    static Long createdUnitTypeId;
    static Long orgId = 24L;
    static Long countryId = 4L;
    static  String baseUrlWithCountry;

    @Before
    public void setUp() throws Exception {
        baseUrlWithCountry = getBaseUrl(orgId, countryId);

    }

    @Test
    @Order(1)
    public void addUnitTypeInCountry() {
        UnitTypeDTO unitTypeDTO= new UnitTypeDTO("first","first desc");
        HttpEntity<UnitTypeDTO> requestBodyData = new HttpEntity<>(unitTypeDTO);
        ParameterizedTypeReference<RestTemplateResponseEnvelope<UnitTypeDTO>> typeReference =
                new ParameterizedTypeReference<RestTemplateResponseEnvelope<UnitTypeDTO>>() {
                };
        ResponseEntity<RestTemplateResponseEnvelope<UnitTypeDTO>> response = restTemplate.exchange(
                baseUrlWithCountry + "/unit_type",
                HttpMethod.POST, requestBodyData, typeReference);
        Assert.assertTrue(HttpStatus.CREATED.equals(response.getStatusCode()));
    }

    @Test
    @Order(2)
    public void getAllUnitTypeOfCountry() {
    }

    @Test
    @Order(3)
    public void updateUnitTypeOfCountry() {
    }

    public final String getBaseUrl(Long organizationId, Long countryId) {
        String baseUrl = new StringBuilder(url + "/api/v1/organization/").append(organizationId)
                .append("/country/").append(countryId).toString();
        return baseUrl;
    }

}