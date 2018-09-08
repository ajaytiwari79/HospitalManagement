package com.kairos.service.pay_table;

import com.kairos.UserServiceApplication;
import com.kairos.commons.client.RestTemplateResponseEnvelope;
import com.kairos.config.OrderTest;
import com.kairos.config.OrderTestRunner;
import com.kairos.persistence.model.country.pay_table.PayTableResponseWrapper;
import com.kairos.persistence.model.pay_table.OrganizationLevelPayGroupAreaDTO;
import com.kairos.persistence.model.pay_table.PayTableResponse;
import com.kairos.dto.user.country.pay_table.PayTableDTO;
import com.kairos.dto.user.country.pay_table.PayTableUpdateDTO;
import com.kairos.utils.DateUtil;
import org.joda.time.DateTime;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.List;

import static com.kairos.utils.DateUtil.ONLY_DATE;

/**
 * Created by vipul on 15/3/18.
 */
@RunWith(OrderTestRunner.class)
@SpringBootTest(classes = UserServiceApplication.class, webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)

public class PayTableServiceIntegrationTest {
    @Value("${server.host.http.url}")
    private String url;
    @Autowired
    TestRestTemplate restTemplate;

    static Long payTableId;
    static private String baseUrlWithCountry;
    Long municipalityId = 1032L;
    Long organizationLevel = 2942L;
    static DateTime currentDate;


    private static final DateFormat df = new SimpleDateFormat(ONLY_DATE);

    @Before
    public void setUp() throws Exception {
        baseUrlWithCountry = getBaseUrl(24L, 4L, null);
        currentDate = new DateTime();
        currentDate.withHourOfDay(0).withMinuteOfHour(0).withSecondOfMinute(0).withMillisOfSecond(0);

    }

    @Test
    @OrderTest(order = 2)
    public void getPayTablesByOrganizationLevel() {
        ParameterizedTypeReference<RestTemplateResponseEnvelope<PayTableResponseWrapper>> typeReference =
                new ParameterizedTypeReference<RestTemplateResponseEnvelope<PayTableResponseWrapper>>() {
                };
        ResponseEntity<RestTemplateResponseEnvelope<PayTableResponseWrapper>> response = restTemplate.exchange(
                baseUrlWithCountry + "/pay_table_data?organizationLevel=" + organizationLevel,
                HttpMethod.GET, null, typeReference);
        RestTemplateResponseEnvelope<PayTableResponseWrapper> responseBody = response.getBody();
        Assert.assertEquals(HttpStatus.OK, response.getStatusCode());
        Assert.assertEquals(false, responseBody.getData().getPayTables());
    }

    @Test
    @OrderTest(order = 2)
    public void getOrganizationLevelWisePayTables() {
        ParameterizedTypeReference<RestTemplateResponseEnvelope<List<OrganizationLevelPayGroupAreaDTO>>> typeReference =
                new ParameterizedTypeReference<RestTemplateResponseEnvelope<List<OrganizationLevelPayGroupAreaDTO>>>() {
                };
        ResponseEntity<RestTemplateResponseEnvelope<List<OrganizationLevelPayGroupAreaDTO>>> response = restTemplate.exchange(
                baseUrlWithCountry + "/organization_level_pay_table",
                HttpMethod.GET, null, typeReference);
        RestTemplateResponseEnvelope<List<OrganizationLevelPayGroupAreaDTO>> responseBody = response.getBody();
        Assert.assertEquals(HttpStatus.OK, response.getStatusCode());

    }

    @Test
    @OrderTest(order = 1)
    public void createPayTable() {
        PayTableDTO payTableDTO = new PayTableDTO("Test pay level", "SF", "", DateUtil.getCurrentDate(), null, "monthly", organizationLevel);
        HttpEntity<PayTableDTO> entity = new HttpEntity<>(payTableDTO);

        ParameterizedTypeReference<RestTemplateResponseEnvelope<PayTableResponse>> typeReference =
                new ParameterizedTypeReference<RestTemplateResponseEnvelope<PayTableResponse>>() {
                };
        ResponseEntity<RestTemplateResponseEnvelope<PayTableResponse>> response = restTemplate.exchange(
                baseUrlWithCountry + "/pay_table",
                HttpMethod.POST, entity, typeReference);
        RestTemplateResponseEnvelope<PayTableResponse> responseBody = response.getBody();

        payTableId = responseBody.getData().getId();
        Assert.assertEquals(201, response.getStatusCodeValue());
        Assert.assertNotNull(payTableId);
        //  Assert.assertEquals(responseBody.getData().getName(), payTableDTO.getName());
    }

    @Test
    @OrderTest(order = 3)
    public void updatePayLevel() {
        PayTableUpdateDTO payTableUpdateDTO = new PayTableUpdateDTO("Test pay level", "SF", "By test", currentDate.toDate(), null, "monthly", organizationLevel);
        HttpEntity<PayTableUpdateDTO> entity = new HttpEntity<>(payTableUpdateDTO);

        ParameterizedTypeReference<RestTemplateResponseEnvelope<PayTableResponse>> typeReference =
                new ParameterizedTypeReference<RestTemplateResponseEnvelope<PayTableResponse>>() {
                };
        ResponseEntity<RestTemplateResponseEnvelope<PayTableResponse>> response = restTemplate.exchange(
                baseUrlWithCountry + "/pay_table/" + 2958,
                HttpMethod.PUT, entity, typeReference);
        RestTemplateResponseEnvelope<PayTableResponse> responseBody = response.getBody();
        payTableId = responseBody.getData().getId();
        Assert.assertEquals(200, response.getStatusCodeValue());
        Assert.assertNotNull(payTableId);
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
        } else if (organizationId != null) {
            String baseUrl = new StringBuilder(url + "/api/v1/organization/").append(organizationId).toString();
            return baseUrl;
        } else {
            throw new UnsupportedOperationException("organization ID must not be null");
        }

    }

}