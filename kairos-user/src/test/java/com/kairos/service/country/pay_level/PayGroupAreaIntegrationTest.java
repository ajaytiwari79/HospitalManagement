package com.kairos.service.country.pay_level;

import com.kairos.UserServiceApplication;
import com.kairos.client.dto.RestTemplateResponseEnvelope;
import com.kairos.config.OrderTest;
import com.kairos.config.OrderTestRunner;
import com.kairos.persistence.model.organization.time_slot.TimeSlotSet;
import com.kairos.persistence.model.user.pay_level.PayGroupArea;
import com.kairos.response.dto.web.pay_level.PayGroupAreaDTO;
import com.kairos.util.DateUtil;
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
import org.springframework.http.ResponseEntity;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Created by prabjot on 21/12/17.
 */
@RunWith(OrderTestRunner.class)
@SpringBootTest(classes = UserServiceApplication.class, webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
public class PayGroupAreaIntegrationTest {
    @Value("${server.host.http.url}")
    private String url;
    @Autowired
    TestRestTemplate restTemplate;
    static Long payGroupId;
    static private String baseUrlWithCountry;

    @Before
    public void setUp() throws Exception {
        baseUrlWithCountry = getBaseUrl(71L, 4L, null);
    }


    @Test
    @OrderTest(order = 1)
    public void savePayGroupArea() {
        Set<Long> municipalityId = new HashSet<>();
        municipalityId.add(1032L);
        municipalityId.add(1035L);
        municipalityId.add(1024L);

        PayGroupAreaDTO payGroupAreaDTO = new PayGroupAreaDTO("North", "Pay grp 1", municipalityId, DateUtil.getCurrentDate(), null);
        HttpEntity<PayGroupAreaDTO> entity = new HttpEntity<>(payGroupAreaDTO);
        ParameterizedTypeReference<RestTemplateResponseEnvelope<PayGroupAreaDTO>> typeReference =
                new ParameterizedTypeReference<RestTemplateResponseEnvelope<PayGroupAreaDTO>>() {
                };
        ResponseEntity<RestTemplateResponseEnvelope<PayGroupAreaDTO>> response = restTemplate.exchange(
                baseUrlWithCountry + "/pay_group_area",
                HttpMethod.POST, entity, typeReference);
        RestTemplateResponseEnvelope<PayGroupAreaDTO> responseBody = response.getBody();
        payGroupId = responseBody.getData().getId();
        Assert.assertEquals(201, response.getStatusCodeValue());
        Assert.assertNotNull(payGroupId);
        Assert.assertEquals(responseBody.getData().getName(), payGroupAreaDTO.getName());
    }

    @Test
    @OrderTest(order = 2)
    public void updatePayGroupArea() {
        PayGroupAreaDTO payGroupAreaDTO = new PayGroupAreaDTO("East", "Pay level for 10-20 years experience", null, null, null);
        String baseUrl = getBaseUrl(145L, 53L, null);
        HttpEntity<PayGroupAreaDTO> entity = new HttpEntity<>(payGroupAreaDTO);
        ParameterizedTypeReference<RestTemplateResponseEnvelope<TimeSlotSet>> typeReference =
                new ParameterizedTypeReference<RestTemplateResponseEnvelope<TimeSlotSet>>() {
                };
        ResponseEntity<RestTemplateResponseEnvelope<TimeSlotSet>> response = restTemplate.exchange(
                baseUrl + "/pay_group_area/" + payGroupId,
                HttpMethod.PUT, entity, typeReference);
        RestTemplateResponseEnvelope<TimeSlotSet> responseBody = response.getBody();
        Assert.assertEquals(200, response.getStatusCodeValue());
        Assert.assertEquals(responseBody.getData().getName(), payGroupAreaDTO.getName());
    }

    @Test
    @OrderTest(order = 3)
    public void getPayGroup() {
        String baseUrl = getBaseUrl(145L, 53L, null);
        ParameterizedTypeReference<RestTemplateResponseEnvelope<List<PayGroupAreaDTO>>> typeReference =
                new ParameterizedTypeReference<RestTemplateResponseEnvelope<List<PayGroupAreaDTO>>>() {
                };
        ResponseEntity<RestTemplateResponseEnvelope<List<PayGroupAreaDTO>>> response = restTemplate.exchange(
                baseUrl + "/pay_group_area",
                HttpMethod.GET, null, typeReference);
        RestTemplateResponseEnvelope<List<PayGroupAreaDTO>> responseBody = response.getBody();
        Assert.assertEquals(false, responseBody.getData().isEmpty());
    }

    @Test
    @OrderTest(order = 3)
    public void deletePayGroup() {
        String baseUrl = getBaseUrl(145L, 53L, null);
        ParameterizedTypeReference<RestTemplateResponseEnvelope<Boolean>> typeReference =
                new ParameterizedTypeReference<RestTemplateResponseEnvelope<Boolean>>() {
                };
        ResponseEntity<RestTemplateResponseEnvelope<Boolean>> response = restTemplate.exchange(
                baseUrl + "/pay_group_area/" + payGroupId,
                HttpMethod.DELETE, null, typeReference);
        RestTemplateResponseEnvelope<Boolean> responseBody = response.getBody();
        Assert.assertEquals(200, response.getStatusCodeValue());
        Assert.assertEquals(responseBody.getData(), true);
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


