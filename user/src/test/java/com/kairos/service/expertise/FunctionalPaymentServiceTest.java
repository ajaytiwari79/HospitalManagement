package com.kairos.service.expertise;

import com.kairos.UserServiceApplication;
import com.kairos.commons.client.RestTemplateResponseEnvelope;
import com.kairos.config.OrderTestRunner;
import com.kairos.persistence.model.user.expertise.Response.FunctionalPaymentDTO;
import com.kairos.service.exception.ExceptionService;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
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

@RunWith(OrderTestRunner.class)
@SpringBootTest(classes = UserServiceApplication.class, webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
public class FunctionalPaymentServiceTest {

    @Value("${server.host.http.url}")
    private String url;
    @Autowired
    private TestRestTemplate restTemplate;
    @Inject
    private ExceptionService exceptionService;

    static private String baseUrlWithCountry;
    static Long expertiseId;

    @Before
    public void setUp() throws Exception {
        baseUrlWithCountry = getBaseUrl(24L, 4L, null);
        expertiseId=34L;
    }

    @After
    public void tearDown() throws Exception {
    }

    @Test
    public void saveFunctionalPayment() {
    }

    @Test
    public void getFunctionalPayment() {
        ParameterizedTypeReference<RestTemplateResponseEnvelope<List<FunctionalPaymentDTO>>> typeReference =
                new ParameterizedTypeReference<RestTemplateResponseEnvelope<List<FunctionalPaymentDTO>>>() {
                };
        ResponseEntity<RestTemplateResponseEnvelope<List<FunctionalPaymentDTO>>> response = restTemplate.exchange(
                baseUrlWithCountry + "/expertise/"+expertiseId+"/functional_payment",
                HttpMethod.GET, null, typeReference);
        RestTemplateResponseEnvelope<List<FunctionalPaymentDTO>> responseBody = response.getBody();
        Assert.assertEquals(HttpStatus.OK, response.getStatusCode());

    }

    @Test
    public void updateFunctionalPayment() {
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
            exceptionService.unsupportedOperationException("message.organization.id.notnull");

        }
        return null;
    }

}