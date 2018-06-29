package com.kairos.service.unit_employment_position;

import com.kairos.UserServiceApplication;
import com.kairos.client.dto.RestTemplateResponseEnvelope;
import com.kairos.config.OrderTestRunner;
import com.kairos.activity.web.UnitPositionDTO;
import com.kairos.service.exception.ExceptionService;
import com.kairos.service.staff.EmploymentService;
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
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import javax.inject.Inject;
import java.util.List;
import java.util.Map;

/**
 * Created by vipul on 27/2/18.
 */
@RunWith(OrderTestRunner.class)
@SpringBootTest(classes = UserServiceApplication.class, webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class UnitPositionServiceIntegrationTest {
    private final Logger logger = LoggerFactory.getLogger(UnitPositionServiceIntegrationTest.class);
    @Value("${server.host.http.url}")
    private String url;
    @Autowired
    TestRestTemplate restTemplate;
    @Autowired
    EmploymentService employmentService;
    @Inject
    private ExceptionService exceptionService;
    static private Long createdId;
    static private String baseUrlWithUnit;

    @Before
    public void setUp() throws Exception {
        baseUrlWithUnit = getBaseUrl(71L, null, 95L);
    }

    @After
    public void tearDown() throws Exception {

    }

    /*@Test
    public void test1_createUnitPosition() throws Exception {
        UnitPositionDTO unitPositionDTO = new UnitPositionDTO(5791L, 1507L, 1616174226503L, null,
                11, 11182L, 8364L, 15115L, 14730L, 95L, null);
        HttpEntity<UnitPositionDTO> requestBodyData = new HttpEntity<>(unitPositionDTO);
        ParameterizedTypeReference<RestTemplateResponseEnvelope<Map<String, Object>>> typeReference =
                new ParameterizedTypeReference<RestTemplateResponseEnvelope<Map<String, Object>>>() {
                };
        ResponseEntity<RestTemplateResponseEnvelope<Map<String, Object>>> response = restTemplate.exchange(
                baseUrlWithUnit + "/unit_position?moduleId=tab_23&type=Organization",
                HttpMethod.POST, requestBodyData, typeReference);
        logger.info(response.toString());
        Assert.assertTrue(HttpStatus.OK.equals(response.getStatusCode()));
        createdId = (long) response.getBody().getData().get("id");
    }*/

    @Test

    public void test2_getUnitPositionsOfStaff() throws Exception {
        ParameterizedTypeReference<RestTemplateResponseEnvelope<List<Map<String, Object>>>> typeReference =
                new ParameterizedTypeReference<RestTemplateResponseEnvelope<List<Map<String, Object>>>>() {
                };
        ResponseEntity<RestTemplateResponseEnvelope<List<Map<String, Object>>>> response = restTemplate.exchange(
                baseUrlWithUnit + "/unit_position/staff/" + 8051 + "?moduleId=tab_23&type=Organization",
                HttpMethod.GET, null, typeReference);
        logger.info(response.toString());
        Assert.assertTrue(HttpStatus.OK.equals(response.getStatusCode()));
    }

   /* @Test
    public void test3_updateUnitPosition() throws Exception {
        UnitPositionDTO unitPositionDTO = new UnitPositionDTO(5791L, 1507L, 1616174226503L, null,
                11, 11182L, 8364L, 15115L, 14730L, 95L, null);
        HttpEntity<UnitPositionDTO> requestBodyData = new HttpEntity<>(unitPositionDTO);
        ParameterizedTypeReference<RestTemplateResponseEnvelope<Map<String, Object>>> typeReference =
                new ParameterizedTypeReference<RestTemplateResponseEnvelope<Map<String, Object>>>() {
                };
        ResponseEntity<RestTemplateResponseEnvelope<Map<String, Object>>> response = restTemplate.exchange(
                baseUrlWithUnit + "/unit_position/" + createdId + "?moduleId=tab_23&type=Organization",
                HttpMethod.PUT, requestBodyData, typeReference);
        logger.info(response.toString());
        Assert.assertTrue(HttpStatus.OK.equals(response.getStatusCode()));
    }*/

    public final String getBaseUrl(Long organizationId, Long countryId, Long unitId) {
        if (organizationId != null && countryId != null) {
            String baseUrl = new StringBuilder(url + "/api/v1/organization/").append(organizationId)
                    .append("/country/").append(countryId).toString();
            return baseUrl;
        } else if (organizationId != null && unitId != null) {
            String baseUrl = new StringBuilder(url + "/api/v1/organization/").append(organizationId)
                    .append("/unit/").append(unitId).toString();
            logger.info(baseUrl);
            return baseUrl;
        } else {
            exceptionService.unsupportedOperationException("message.organization.id.notnull");

        }
        return null;
    }



}