package com.kairos.service.position;

import com.kairos.UserServiceApplication;
import com.kairos.client.dto.RestTemplateResponseEnvelope;
import com.kairos.config.OrderTest;
import com.kairos.config.OrderTestRunner;
import com.kairos.persistence.model.user.agreement.wta.WTADTO;
import com.kairos.persistence.model.user.agreement.wta.WTAWithCountryAndOrganizationTypeDTO;
import com.kairos.persistence.model.user.agreement.wta.WorkingTimeAgreement;
import com.kairos.persistence.model.user.position.PositionCode;
import com.kairos.service.agreement.wta.WTAServiceIntegrationTest;
import org.junit.Assert;
import org.junit.Before;
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

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.List;

import static com.kairos.util.DateUtil.ONLY_DATE;
import static org.junit.Assert.*;

/**
 * Created by vipul on 12/1/18.
 */
@RunWith(OrderTestRunner.class)
@SpringBootTest(classes = UserServiceApplication.class, webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class PositionCodeServiceIntegrationTest {
    @Value("${server.host.http.url}")
    private String url;
    @Autowired
    private TestRestTemplate restTemplate;
    @Autowired
    private WTAServiceIntegrationTest wtaServiceIntegrationTest;
    static private String baseUrlWithCountry;
    static private String baseUrlWithUnit;
    static Long createdId, createdIdDelete, wtaIdForUpdate;
    static private PositionCode positionCode;
    private static final DateFormat df = new SimpleDateFormat(ONLY_DATE);

    @Before
    public void setUp() throws Exception {
        baseUrlWithCountry = wtaServiceIntegrationTest.getBaseUrl(71L, null, 95L);
        baseUrlWithUnit = wtaServiceIntegrationTest.getBaseUrl(71L, 53L, null);
        positionCode = new PositionCode("Doctor" + Math.random(), "hey");


    }


    @Test
    @OrderTest(order = 1)
    public void createPositionCode() throws Exception {
        HttpEntity<PositionCode> requestBodyData = new HttpEntity<>(positionCode);
        ParameterizedTypeReference<RestTemplateResponseEnvelope<PositionCode>> typeReference =
                new ParameterizedTypeReference<RestTemplateResponseEnvelope<PositionCode>>() {
                };
        ResponseEntity<RestTemplateResponseEnvelope<PositionCode>> response = restTemplate.exchange(
                baseUrlWithUnit + "/position_code",
                HttpMethod.POST, requestBodyData, typeReference);
        Assert.assertTrue(HttpStatus.OK.equals(response.getStatusCode()));
        createdId = wtaIdForUpdate = createdIdDelete = response.getBody().getData().getId();
    }

    @Test
    @OrderTest(order = 2)
    public void getPositionCode() throws Exception {
        ParameterizedTypeReference<RestTemplateResponseEnvelope<List<WTAWithCountryAndOrganizationTypeDTO>>> typeReference =
                new ParameterizedTypeReference<RestTemplateResponseEnvelope<List<WTAWithCountryAndOrganizationTypeDTO>>>() {
                };
        ResponseEntity<RestTemplateResponseEnvelope<List<WTAWithCountryAndOrganizationTypeDTO>>> response = restTemplate.exchange(
                baseUrlWithUnit + "/position_code",
                HttpMethod.GET, null, typeReference);
        Assert.assertTrue(HttpStatus.OK.equals(response.getStatusCode()));

    }

}