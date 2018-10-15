package com.kairos.service.staff_settings;

import com.kairos.KairosActivityApplication;
import com.kairos.commons.client.RestTemplateResponseEnvelope;
import com.kairos.dto.user.staff.staff_settings.StaffActivitySettingDTO;
import com.kairos.dto.user.staff.staff_settings.StaffAndActivitySettingWrapper;
import org.junit.Assert;
import org.junit.Before;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.MethodSorters;
import org.mockito.InjectMocks;
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
import java.util.Collections;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = KairosActivityApplication.class, webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class StaffActivitySettingServiceTest {
    private final Logger logger = LoggerFactory.getLogger(StaffActivitySettingServiceTest.class);

    @Value("${server.host.http.url}")
    private String url;
    @Autowired
    TestRestTemplate restTemplate;
    @InjectMocks
    static String baseUrlForUnit;


    StaffActivitySettingDTO staffActivitySettingDTO=new StaffActivitySettingDTO(new BigInteger("2594"),2424L,new Short("25"),new Short("36"),40,5,true);
    StaffAndActivitySettingWrapper staffAndActivitySettingWrapper=new StaffAndActivitySettingWrapper(Collections.singleton(7788L),Collections.singletonList(staffActivitySettingDTO));
    @Before
    public void setUp() throws Exception {
        baseUrlForUnit = getBaseUrl(2567L, null,2567L);
    }
    @Test
    public void assignActivitySettingToStaffs() {
        HttpEntity<StaffAndActivitySettingWrapper> requestBodyData = new HttpEntity<>(staffAndActivitySettingWrapper);
        ParameterizedTypeReference<RestTemplateResponseEnvelope<StaffAndActivitySettingWrapper>> typeReference =
                new ParameterizedTypeReference<RestTemplateResponseEnvelope<StaffAndActivitySettingWrapper>>() {
                };
        ResponseEntity<RestTemplateResponseEnvelope<StaffAndActivitySettingWrapper>> response = restTemplate.exchange(
                baseUrlForUnit + "/staff_activity_setting/assign",
                HttpMethod.POST, requestBodyData, typeReference);
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
            return baseUrl;
        } else {
            throw new UnsupportedOperationException("organization ID must not be null");
        }
    }
}