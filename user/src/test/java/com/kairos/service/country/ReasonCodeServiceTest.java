package com.kairos.service.country;

import com.kairos.UserServiceApplication;
import com.kairos.commons.client.RestTemplateResponseEnvelope;
import com.kairos.persistence.model.country.reason_code.ReasonCode;
import com.kairos.persistence.model.country.reason_code.ReasonCodeResponseDTO;
import com.kairos.dto.user.reason_code.ReasonCodeDTO;
import com.kairos.service.exception.ExceptionService;
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
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit4.SpringRunner;

import javax.inject.Inject;
import java.util.List;

import static com.kairos.enums.reason_code.ReasonCodeType.ABSENCE;
import static com.kairos.enums.reason_code.ReasonCodeType.EMPLOYMENT;

/**
 * Created by pavan on 24/3/18.
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = UserServiceApplication.class,webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class ReasonCodeServiceTest {
    @Value("${server.host.http.url}")
    private String url ;
    @Autowired
    TestRestTemplate restTemplate;
    @Inject
    private ExceptionService exceptionService;
    static String baseUrlWithCountry;
    private final Logger logger = LoggerFactory.getLogger(ReasonCodeServiceTest.class);
    ReasonCodeDTO reasonCodeDTO=new ReasonCodeDTO();
    @Before
    public void setUp() throws Exception {
        baseUrlWithCountry = getBaseUrl(24L, 4L, null);
        logger.info("Base Url is:"+baseUrlWithCountry);
    }

    @Test
    public void createReasonCode() throws Exception {
        reasonCodeDTO.setName("Part-timers hour increase");
        reasonCodeDTO.setCode("Part-timers hour increase Code");
        reasonCodeDTO.setDescription("Part-timers hour increase Description");
        reasonCodeDTO.setReasonCodeType(EMPLOYMENT);
        HttpEntity<ReasonCodeDTO> requestBodyData = new HttpEntity<>(reasonCodeDTO);
        ParameterizedTypeReference<RestTemplateResponseEnvelope<ReasonCodeDTO>> typeReference =
                new ParameterizedTypeReference<RestTemplateResponseEnvelope<ReasonCodeDTO>>() {
                };
        ResponseEntity<RestTemplateResponseEnvelope<ReasonCodeDTO>> response = restTemplate.exchange(
                baseUrlWithCountry + "/reason_code",
                HttpMethod.POST, requestBodyData, typeReference);
        Assert.assertTrue(HttpStatus.OK.equals(response.getStatusCode()));
    }

    @Test
    public void getReasonCodes() throws Exception {
        ParameterizedTypeReference<RestTemplateResponseEnvelope<List<ReasonCodeResponseDTO>>> typeReference =
                new ParameterizedTypeReference<RestTemplateResponseEnvelope<List<ReasonCodeResponseDTO>>>() {
                };
        ResponseEntity<RestTemplateResponseEnvelope<List<ReasonCodeResponseDTO>>> response = restTemplate.exchange(
                baseUrlWithCountry + "/reason_codes",
                HttpMethod.GET, null, typeReference);
        Assert.assertTrue(HttpStatus.OK.equals(response.getStatusCode()));
    }

    @Test
    public void updateReasonCode() throws Exception {
        reasonCodeDTO.setId(3610L);
        reasonCodeDTO.setName("Part-timers hour increases");
        reasonCodeDTO.setCode("Part-timers hour increase Code");
        reasonCodeDTO.setDescription("Part-timers hour increase Description");
        reasonCodeDTO.setReasonCodeType(ABSENCE);
        HttpEntity<ReasonCodeDTO> requestBodyData = new HttpEntity<>(reasonCodeDTO);
        ParameterizedTypeReference<RestTemplateResponseEnvelope<ReasonCodeResponseDTO>> typeReference =
                new ParameterizedTypeReference<RestTemplateResponseEnvelope<ReasonCodeResponseDTO>>() {
                };
        ResponseEntity<RestTemplateResponseEnvelope<ReasonCodeResponseDTO>> response = restTemplate.exchange(
                baseUrlWithCountry + "/reason_code/3610",
                HttpMethod.PUT, requestBodyData, typeReference);
        Assert.assertTrue(HttpStatus.OK.equals(response.getStatusCode()));

    }

    @Test
    public void deleteReasonCode() throws Exception {
        ResponseEntity<ReasonCode> response = restTemplate.exchange(
                baseUrlWithCountry + "/reason_code/3610",
                HttpMethod.DELETE, null, ReasonCode.class);
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
            exceptionService.unsupportedOperationException("message.organization.id.notnull");

        }
        return null;
    }

}