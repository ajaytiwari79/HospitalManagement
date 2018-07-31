package com.kairos.service.master_data.processing_activity_masterdata;

import com.kairos.KairosGdprApplication;
import com.kairos.client.dto.RestTemplateResponseEnvelope;
import com.kairos.dto.OrganizationSubTypeDTO;
import com.kairos.dto.OrganizationTypeDTO;
import com.kairos.dto.ServiceCategoryDTO;
import com.kairos.dto.SubServiceCategoryDTO;
import com.kairos.dto.master_data.MasterProcessingActivityDTO;
import com.kairos.response.dto.master_data.MasterProcessingActivityResponseDTO;
import com.kairos.service.master_data.asset_management.MasterAssetServiceTest;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.web.util.UriComponentsBuilder;

import javax.inject.Inject;
import java.math.BigInteger;
import java.util.*;

import static org.junit.Assert.*;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = KairosGdprApplication.class, webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
public class MasterProcessingActivityServiceTest {



    Logger LOGGER = LoggerFactory.getLogger(MasterAssetServiceTest.class);

    private final Logger logger = LoggerFactory.getLogger(MasterAssetServiceTest.class);
    @Value("${server.host.http.url}")
    private String url;


    @Inject
    private TestRestTemplate restTemplate;


    private BigInteger createdId;


    @Test
    public void test_createMasterProcessingActivity() throws Exception {
        String baseUrl = getBaseUrl(24L, 4l, null);

        MasterProcessingActivityDTO processingActivity = new MasterProcessingActivityDTO();
        processingActivity.setName("processing Activity");
        processingActivity.setDescription("Activity  description ");
        processingActivity.setOrganizationSubTypes(new ArrayList<>(Arrays.asList(new OrganizationSubTypeDTO(32l, "xsyz"))));
        processingActivity.setOrganizationTypes(new ArrayList<>(Arrays.asList(new OrganizationTypeDTO(32l, "xyz"))));
        processingActivity.setOrganizationSubServices(new ArrayList<>(Arrays.asList(new SubServiceCategoryDTO(35l, "poiuy"))));
        processingActivity.setOrganizationServices(new ArrayList<>(Arrays.asList(new ServiceCategoryDTO(34l, "abc"))));

        HttpEntity<MasterProcessingActivityDTO> entity = new HttpEntity<>(processingActivity);
        ResponseEntity<Map> response = restTemplate.exchange(
                baseUrl + "/master_processing_activity/add",
                HttpMethod.POST, entity, Map.class);

        logger.info("response", response);
        LinkedHashMap<String, Object> masterAsset = (LinkedHashMap<String, Object>) response.getBody().get("data");
        createdId = BigInteger.valueOf( (Integer)masterAsset.get("id"));
        Assert.assertEquals(HttpStatus.OK, response.getStatusCode());
        Assert.assertNotEquals(null, createdId);

    }

    @Test
    public void test1_getMasterProcessingActivityListWithSubProcessing() throws Exception {
        String baseUrl = getBaseUrl(24L, 4l, null);
        ParameterizedTypeReference<RestTemplateResponseEnvelope<List<MasterProcessingActivityResponseDTO>>> typeReference =
                new ParameterizedTypeReference<RestTemplateResponseEnvelope<List<MasterProcessingActivityResponseDTO>>>() {
                };
        ResponseEntity<RestTemplateResponseEnvelope<List<MasterProcessingActivityResponseDTO>>> response = restTemplate.exchange(
                baseUrl + "/master_processing_activity/all", HttpMethod.GET, null, typeReference);
        logger.info("response", response);
        Assert.assertEquals(HttpStatus.OK, response.getStatusCode());
    }


    @Test
    public void test2_getMasterProcessingActivityWithSubProcessingById() throws Exception {

        String baseUrl = getBaseUrl(24L, 4l, null);
        ParameterizedTypeReference<RestTemplateResponseEnvelope<MasterProcessingActivityResponseDTO>> typeReference =
                new ParameterizedTypeReference<RestTemplateResponseEnvelope<MasterProcessingActivityResponseDTO>>() {
                };
        ResponseEntity<RestTemplateResponseEnvelope<MasterProcessingActivityResponseDTO>> response = restTemplate.exchange(
                baseUrl + "/master_processing_activity/" + createdId + "", HttpMethod.GET, null, typeReference);
        logger.info("response", response.getBody().getData());
        Assert.assertEquals(response.getBody().getData().getId(), createdId);
        Assert.assertEquals(HttpStatus.OK, response.getStatusCode());
    }


    @Test
    public void test3_removeProcessingActivity() throws Exception {

        String baseUrl = getBaseUrl(24L, 4l, null);
        UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(baseUrl + "/master_processing_activity/delete/" + createdId);
        ResponseEntity<String> response = restTemplate.exchange(
                builder.toUriString(),
                HttpMethod.DELETE, null, String.class);
        logger.info("response", response);
        Assert.assertEquals(HttpStatus.OK, response.getStatusCode());
    }


    public final String getBaseUrl(Long organizationId, Long countryId, Long unitId) {
        if (organizationId != null && unitId != null && countryId != null) {
            String baseUrl = new StringBuilder(url + "/api/v1/organization/").append(organizationId).append("/country/").append(countryId)
                    .append("/unit/").append(unitId).toString();
            ;
            return baseUrl;
        } else if (organizationId != null && countryId != null) {
            String baseUrl = new StringBuilder(url + "/api/v1/organization/").append(organizationId).append("/country/").append(countryId).toString();
            return baseUrl;
        } else {
            throw new UnsupportedOperationException("ogranization ID must not be null");
        }

    }

}