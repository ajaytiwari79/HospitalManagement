package com.kairos.service.account_type;

import com.kairos.KairosGdprApplication;
import com.kairos.client.dto.RestTemplateResponseEnvelope;
import com.kairos.persistance.model.account_type.AccountType;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit4.SpringRunner;

import javax.inject.Inject;
import java.math.BigInteger;
import java.util.*;


@RunWith(SpringRunner.class)
@SpringBootTest(classes = KairosGdprApplication.class, webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
public class AccountTypeServiceTest {


    private final Logger logger = LoggerFactory.getLogger(AccountTypeServiceTest.class);
    @Value("${server.host.http.url}")
    private String url;


    @Inject
    private TestRestTemplate restTemplate;


    private BigInteger createdId;



    @Test
    public void test_getAllAccountType() throws Exception {
        String baseUrl = getBaseUrl(24L, 4l, null);
        ParameterizedTypeReference<RestTemplateResponseEnvelope<List<AccountType>>> typeReference =
                new ParameterizedTypeReference<RestTemplateResponseEnvelope<List<AccountType>>>() {
                };
        ResponseEntity<RestTemplateResponseEnvelope<List<AccountType>>> response = restTemplate.exchange(
                baseUrl + "/account/all", HttpMethod.GET, null, typeReference);
        logger.info("response", response);
        Assert.assertEquals(HttpStatus.OK, response.getStatusCode());
    }


    @Test
    public void test2_accountTypeById() throws Exception {

        String baseUrl = getBaseUrl(24L, 4l, null);
        ParameterizedTypeReference<RestTemplateResponseEnvelope<AccountType>> typeReference =
                new ParameterizedTypeReference<RestTemplateResponseEnvelope<AccountType>>() {
                };
        ResponseEntity<RestTemplateResponseEnvelope<AccountType>> response = restTemplate.exchange(
                baseUrl + "/account/" + createdId + "", HttpMethod.GET, null, typeReference);
        logger.info("response", response.getBody().getData());
        Assert.assertEquals(response.getBody().getData().getId(), createdId);
        Assert.assertEquals(HttpStatus.OK, response.getStatusCode());
    }



    public final String getBaseUrl(Long organizationId, Long countryId, Long unitId) {
        if (organizationId != null && unitId != null && countryId != null) {
            String baseUrl = new StringBuilder(url + "/api/v1/organization/").append(organizationId).append("/country/").append(countryId)
                    .append("/unit/").append(unitId).toString();
            return baseUrl;
        } else if (organizationId != null && countryId != null) {
            String baseUrl = new StringBuilder(url + "/api/v1/organization/").append(organizationId).append("/country/").append(countryId).toString();
            return baseUrl;
        } else {
            throw new UnsupportedOperationException("organization ID must not be null");
        }

    }

}