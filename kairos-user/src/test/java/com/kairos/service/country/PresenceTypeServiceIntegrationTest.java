package com.kairos.service.country;

import com.kairos.UserServiceApplication;
import com.kairos.client.dto.RestTemplateResponseEnvelope;
import com.kairos.persistence.model.timetype.PresenceTypeDTO;
import com.kairos.persistence.model.timetype.PresenceTypeWithTimeTypeDTO;
import org.junit.Assert;
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

import java.util.Date;

/**
 * Created by vipul on 10/11/17.
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = UserServiceApplication.class, webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class PresenceTypeServiceIntegrationTest {
    @Value("${server.host.http.url}")
    private String url;
    @Autowired
    TestRestTemplate restTemplate;
    static Long createdId;
    static Long createdIdForDelete;

    @Test
    public void test1_AddPresenceType() throws Exception {
        String baseUrl = getBaseUrl(71L, 53L);
        PresenceTypeDTO presenceTypeDTO = new PresenceTypeDTO("PRESENCE TYPE" + new Date().getTime());
        HttpEntity<PresenceTypeDTO> requestBodyData = new HttpEntity<>(presenceTypeDTO);

        ParameterizedTypeReference<RestTemplateResponseEnvelope<PresenceTypeDTO>> typeReference =
                new ParameterizedTypeReference<RestTemplateResponseEnvelope<PresenceTypeDTO>>() {
                };

        ResponseEntity<RestTemplateResponseEnvelope<PresenceTypeDTO>> response = restTemplate.exchange(
                baseUrl + "/presenceType",
                HttpMethod.POST, requestBodyData, typeReference);
        Assert.assertTrue(HttpStatus.CREATED.equals(response.getStatusCode()));
        createdIdForDelete= createdId = response.getBody().getData().getId();
    }

    @Test
    public void test2_getAllPresenceTypeByCountry() throws Exception {
        String baseUrl = getBaseUrl(71L, 53L);
        ResponseEntity<PresenceTypeDTO> response = restTemplate.exchange(
                baseUrl + "/presenceType",
                HttpMethod.GET, null, PresenceTypeDTO.class);
        Assert.assertTrue(HttpStatus.OK.equals(response.getStatusCode()));
    }
    @Test
    public void test5_getAllPresenceTypeAndTimeTypesByCountry() throws Exception {
        String baseUrl = getBaseUrl(71L, 53L);
        ResponseEntity<PresenceTypeWithTimeTypeDTO> response = restTemplate.exchange(
                baseUrl + "/presenceTypeWithTimeType",
                HttpMethod.GET, null, PresenceTypeWithTimeTypeDTO.class);
        Assert.assertTrue(HttpStatus.OK.equals(response.getStatusCode()));
    }

    @Test
    public void test4_DeletePresenceTypeById() throws Exception {
        String baseUrl = getBaseUrl(71L, 53L);
        ResponseEntity<PresenceTypeDTO> response = restTemplate.exchange(
                baseUrl + "/presenceType/" + createdIdForDelete,
                HttpMethod.DELETE, null, PresenceTypeDTO.class);
        Assert.assertTrue(HttpStatus.OK.equals(response.getStatusCode()));
    }

    @Test
    public void test3_updatePresenceType() throws Exception {
        String baseUrl = getBaseUrl(71L, 53L);
        PresenceTypeDTO presenceTypeDTO = new PresenceTypeDTO("PRESENCE TYPE" + new Date().getTime());
        HttpEntity<PresenceTypeDTO> requestBodyData = new HttpEntity<>(presenceTypeDTO);
        ResponseEntity<PresenceTypeDTO> response = restTemplate.exchange(
                baseUrl + "/presenceType/" + createdId,
                HttpMethod.PUT, requestBodyData, PresenceTypeDTO.class);
        Assert.assertTrue(HttpStatus.OK.equals(response.getStatusCode()));
        createdId = response.getBody().getId();
    }

    public final String getBaseUrl(Long organizationId, Long countryId) {
        if (organizationId != null && countryId != null) {
            String baseUrl = new StringBuilder(url + "/api/v1/organization/").append(organizationId)
                    .append("/country/").append(countryId).toString();
            return baseUrl;
        } else if (organizationId != null) {
            String baseUrl = new StringBuilder(url + "/api/v1/organization/").append(organizationId).toString();
            return baseUrl;
        } else {
            throw new UnsupportedOperationException("organization ID must not be null");
        }

    }
}