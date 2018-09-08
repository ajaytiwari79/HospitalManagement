package com.kairos.service.position_code;

import com.kairos.UserServiceApplication;
import com.kairos.commons.client.RestTemplateResponseEnvelope;
import com.kairos.config.OrderTest;
import com.kairos.config.OrderTestRunner;
import com.kairos.persistence.model.user.position_code.PositionCode;
import com.kairos.persistence.repository.organization.OrganizationGraphRepository;
import com.kairos.service.exception.ExceptionService;
import com.kairos.service.unit_position.UnitPositionService;
import org.junit.Assert;
import org.junit.Before;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.MethodSorters;
import org.mockito.Mock;
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

import javax.inject.Inject;
import java.text.DateFormat;
import java.text.SimpleDateFormat;

import static com.kairos.utils.DateUtil.ONLY_DATE;

/**
 * Created by vipul on 12/1/18.
 */
@RunWith(OrderTestRunner.class)
@SpringBootTest(classes = UserServiceApplication.class, webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class PositionCodeServiceIntegrationTest {
    private final Logger logger = LoggerFactory.getLogger(PositionCodeServiceIntegrationTest.class);
    @Value("${server.host.http.url}")
    private String url;
    @Autowired
    TestRestTemplate restTemplate;
    @Autowired
    UnitPositionService unitPositionService;
    @Inject
    private ExceptionService exceptionService;
    @Mock
    private OrganizationGraphRepository organizationGraphRepository;
    static private String baseUrlWithCountry;
    static private String baseUrlWithUnit;
    static Long createdId, createdIdDelete, wtaIdForUpdate;
    static private PositionCode positionCode = null;
    private static final DateFormat df = new SimpleDateFormat(ONLY_DATE);

    @Before
    public void setUp() throws Exception {
        baseUrlWithUnit = getBaseUrl(71L, null, 95L);
        baseUrlWithCountry = getBaseUrl(71L, 53L, null);
    }


    @Test
    @OrderTest(order = 1)
    public void createPositionCode() throws Exception {
        HttpEntity<PositionCode> requestBodyData = new HttpEntity<>(positionCode);
        ParameterizedTypeReference<RestTemplateResponseEnvelope<PositionCode>> typeReference =
                new ParameterizedTypeReference<RestTemplateResponseEnvelope<PositionCode>>() {
                };
        ResponseEntity<RestTemplateResponseEnvelope<PositionCode>> response = restTemplate.exchange(
                baseUrlWithUnit + "/position_code?moduleId=tab_23&type=Organization",
                HttpMethod.POST, requestBodyData, typeReference);
        Assert.assertTrue(HttpStatus.OK.equals(response.getStatusCode()));
        createdId = wtaIdForUpdate = createdIdDelete = response.getBody().getData().getId();
    }

  /*  @Test
    @OrderTest(order = 2)
    public void getPositionCode() throws Exception {
        ParameterizedTypeReference<RestTemplateResponseEnvelope<List<WTAResponseDTO>>> typeReference =
                new ParameterizedTypeReference<RestTemplateResponseEnvelope<List<WTAResponseDTO>>>() {
                };
        ResponseEntity<RestTemplateResponseEnvelope<List<WTAResponseDTO>>> response = restTemplate.exchange(
                baseUrlWithUnit + "/position_code",
                HttpMethod.GET, null, typeReference);
        Assert.assertTrue(HttpStatus.OK.equals(response.getStatusCode()));
    }*/

    @Test
    @OrderTest(order = 3)
    public void getUnionsAndPositionCodes() throws Exception {
        HttpEntity<PositionCode> requestBodyData = new HttpEntity<>(positionCode);
        ParameterizedTypeReference<RestTemplateResponseEnvelope<PositionCode>> typeReference =
                new ParameterizedTypeReference<RestTemplateResponseEnvelope<PositionCode>>() {
                };
        ResponseEntity<RestTemplateResponseEnvelope<PositionCode>> response = restTemplate.exchange(
                baseUrlWithUnit + "/unions_with_position_code?moduleId=tab_73&type=Organization",
                HttpMethod.GET, requestBodyData, typeReference);
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
            logger.info(baseUrl);
            return baseUrl;
        } else {
            exceptionService.unsupportedOperationException("message.organization.id.notnull");

        }
        return null;
    }
}