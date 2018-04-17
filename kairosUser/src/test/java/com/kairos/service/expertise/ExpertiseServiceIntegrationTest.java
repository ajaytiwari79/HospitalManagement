package com.kairos.service.expertise;

import com.kairos.UserServiceApplication;
import com.kairos.client.dto.RestTemplateResponseEnvelope;
import com.kairos.config.OrderTest;
import com.kairos.config.OrderTestRunner;
import com.kairos.persistence.model.user.expertise.ExpertiseQueryResult;
import com.kairos.response.dto.web.experties.CountryExpertiseDTO;
import com.kairos.response.dto.web.experties.PaidOutFrequencyEnum;
import com.kairos.response.dto.web.experties.SeniorityLevelDTO;
import com.kairos.response.dto.web.experties.UnionServiceWrapper;
import com.kairos.util.DateUtil;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Ignore;
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

import java.math.BigDecimal;
import java.util.List;

/**
 * Created by vipul on 27/3/18.
 */
@RunWith(OrderTestRunner.class)
@SpringBootTest(classes = UserServiceApplication.class, webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
public class ExpertiseServiceIntegrationTest {

    @Value("${server.host.http.url}")
    private String url;
    @Autowired
    private TestRestTemplate restTemplate;


    static private String baseUrlWithCountry;
    static Long organizationLevelId, serviceId, unionId, payTableId, expertiseId;

    @Before
    public void setUp() throws Exception {
        baseUrlWithCountry = getBaseUrl(24L, 4L, null);
        organizationLevelId = 2942L;
        serviceId = 177L;
        unionId = 2597L;

    }

    @Test
    @OrderTest(order = 1)
    public void saveExpertise() throws Exception {
        SeniorityLevelDTO seniorityLevelDTO = new SeniorityLevelDTO(1, 4, 1L, new BigDecimal(1.5), new BigDecimal(2.5), new BigDecimal(5.6));
        CountryExpertiseDTO expertiseDTO = new CountryExpertiseDTO("Ex1", "", DateUtil.getCurrentDate(), null, organizationLevelId, serviceId
                , unionId, 12, 12, PaidOutFrequencyEnum.MONTHLY, seniorityLevelDTO);
        HttpEntity<CountryExpertiseDTO> entity = new HttpEntity<>(expertiseDTO);
        ParameterizedTypeReference<RestTemplateResponseEnvelope<CountryExpertiseDTO>> typeReference =
                new ParameterizedTypeReference<RestTemplateResponseEnvelope<CountryExpertiseDTO>>() {
                };
        ResponseEntity<RestTemplateResponseEnvelope<CountryExpertiseDTO>> response = restTemplate.exchange(
                baseUrlWithCountry + "/expertise",
                HttpMethod.POST, entity, typeReference);
        RestTemplateResponseEnvelope<CountryExpertiseDTO> responseBody = response.getBody();

        expertiseId = responseBody.getData().getId();
        Assert.assertEquals(201, response.getStatusCodeValue());
        Assert.assertNotNull(expertiseId);
    }

    @Test
    @OrderTest(order = 2)
    public void addSeniorityLevelInExpertise() throws Exception {
        expertiseId = 2955L;
        SeniorityLevelDTO seniorityLevelDTO = new SeniorityLevelDTO(0, 6, 1L, new BigDecimal(1.5), new BigDecimal(2.5), new BigDecimal(5.6));
        CountryExpertiseDTO expertiseDTO = new CountryExpertiseDTO("Ex1", "", DateUtil.getCurrentDate(), null, organizationLevelId, serviceId
                , unionId, 12, 12, PaidOutFrequencyEnum.MONTHLY, seniorityLevelDTO);
        expertiseDTO.setId(expertiseId);
        HttpEntity<CountryExpertiseDTO> entity = new HttpEntity<>(expertiseDTO);
        ParameterizedTypeReference<RestTemplateResponseEnvelope<CountryExpertiseDTO>> typeReference =
                new ParameterizedTypeReference<RestTemplateResponseEnvelope<CountryExpertiseDTO>>() {
                };
        ResponseEntity<RestTemplateResponseEnvelope<CountryExpertiseDTO>> response = restTemplate.exchange(
                baseUrlWithCountry + "/expertise",
                HttpMethod.POST, entity, typeReference);
        RestTemplateResponseEnvelope<CountryExpertiseDTO> responseBody = response.getBody();

        expertiseId = responseBody.getData().getId();
        Assert.assertEquals(201, response.getStatusCodeValue());
        Assert.assertNotNull(expertiseId);
    }


    @Test
    @OrderTest(order = 3)
    public void getAllExpertise() throws Exception {
        ParameterizedTypeReference<RestTemplateResponseEnvelope<List<ExpertiseQueryResult>>> typeReference =
                new ParameterizedTypeReference<RestTemplateResponseEnvelope<List<ExpertiseQueryResult>>>() {
                };
        ResponseEntity<RestTemplateResponseEnvelope<List<ExpertiseQueryResult>>> response = restTemplate.exchange(
                baseUrlWithCountry + "/expertise",
                HttpMethod.GET, null, typeReference);
        RestTemplateResponseEnvelope<List<ExpertiseQueryResult>> responseBody = response.getBody();
        Assert.assertEquals(HttpStatus.OK, response.getStatusCode());
        Assert.assertNotNull(responseBody.getData());


    }

    @Test
    @Ignore
    public void updateExpertise() throws Exception {

    }

    @Test
    @Ignore
    public void deleteExpertise() throws Exception {

    }

    @Test
    @Ignore
    public void getExpertiseByCountryId() throws Exception {

    }

    @Test
    public void getUnionsAndService() throws Exception {
        ParameterizedTypeReference<RestTemplateResponseEnvelope<UnionServiceWrapper>> typeReference =
                new ParameterizedTypeReference<RestTemplateResponseEnvelope<UnionServiceWrapper>>() {
                };
        ResponseEntity<RestTemplateResponseEnvelope<UnionServiceWrapper>> response = restTemplate.exchange(
                baseUrlWithCountry + "/union_with_Service",
                HttpMethod.GET, null, typeReference);
        RestTemplateResponseEnvelope<UnionServiceWrapper> responseBody = response.getBody();
        Assert.assertEquals(HttpStatus.OK, response.getStatusCode());

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