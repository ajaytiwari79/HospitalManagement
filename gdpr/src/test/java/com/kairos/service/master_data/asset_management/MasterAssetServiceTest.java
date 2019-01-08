package com.kairos.service.master_data.asset_management;

import com.kairos.KairosGdprApplication;
import com.kairos.commons.client.RestTemplateResponseEnvelope;
import com.kairos.dto.gdpr.OrganizationSubTypeDTO;
import com.kairos.dto.gdpr.OrganizationTypeDTO;
import com.kairos.dto.gdpr.ServiceCategoryDTO;
import com.kairos.dto.gdpr.SubServiceCategoryDTO;
import com.kairos.dto.gdpr.master_data.MasterAssetDTO;
import com.kairos.response.dto.master_data.AssetTypeResponseDTO;
import com.kairos.response.dto.master_data.MasterAssetResponseDTO;
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

@RunWith(SpringRunner.class)
@SpringBootTest(classes = KairosGdprApplication.class, webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
public class MasterAssetServiceTest {


    Logger LOGGER = LoggerFactory.getLogger(MasterAssetServiceTest.class);

    private final Logger logger = LoggerFactory.getLogger(MasterAssetServiceTest.class);
    @Value("${server.host.http.url}")
    private String url;


    @Inject
    private TestRestTemplate restTemplate;


    private BigInteger createdId;


    @Test
    public void test_createMasterAsset() {
        String baseUrl = getBaseUrl(24L, 4l, null);
        ParameterizedTypeReference<RestTemplateResponseEnvelope<List<AssetTypeResponseDTO>>> assetTypeReference =
                new ParameterizedTypeReference<RestTemplateResponseEnvelope<List<AssetTypeResponseDTO>>>() {
                };
        ResponseEntity<RestTemplateResponseEnvelope<List<AssetTypeResponseDTO>>> assetResponse = restTemplate.exchange(
                baseUrl + "/asset_type/all", HttpMethod.GET, null, assetTypeReference);
        BigInteger assetTypeId = assetResponse.getBody().getData().get(0).getId();
        MasterAssetDTO assetDTO = new MasterAssetDTO();
        assetDTO.setName("Unique name Asset");
        assetDTO.setDescription("asset abc description ");
        assetDTO.setOrganizationSubTypeDTOS(new ArrayList<>(Arrays.asList(new OrganizationSubTypeDTO(32l, "xsyz"))));
        assetDTO.setOrganizationTypeDTOS(new ArrayList<>(Arrays.asList(new OrganizationTypeDTO(32l, "xyz"))));
        assetDTO.setOrganizationSubServices(new ArrayList<>(Arrays.asList(new SubServiceCategoryDTO(35l, "poiuy"))));
        assetDTO.setOrganizationServices(new ArrayList<>(Arrays.asList(new ServiceCategoryDTO(34l, "abc"))));
        assetDTO.setAssetTypeId(assetTypeId);
        assetDTO.setAssetSubTypes(new ArrayList<>(Arrays.asList(BigInteger.ONE, BigInteger.TEN)));

        HttpEntity<MasterAssetDTO> entity = new HttpEntity<>(assetDTO);
        ResponseEntity<Map> response = restTemplate.exchange(
                baseUrl + "/master_asset/add_asset",        // createdId = response.getBody().getData().getId();

                HttpMethod.POST, entity, Map.class);
        logger.info("response", response);
        LinkedHashMap<String, Object> masterAsset = (LinkedHashMap<String, Object>) response.getBody().get("data");
        createdId = BigInteger.valueOf( (Integer)masterAsset.get("id"));
        Assert.assertEquals(HttpStatus.OK, response.getStatusCode());
        Assert.assertNotEquals(null, createdId);


    }

    @Test
    public void test1_getAllMasterAsset() {
        String baseUrl = getBaseUrl(24L, 4l, null);
        ParameterizedTypeReference<RestTemplateResponseEnvelope<List<MasterAssetResponseDTO>>> typeReference =
                new ParameterizedTypeReference<RestTemplateResponseEnvelope<List<MasterAssetResponseDTO>>>() {
                };
        ResponseEntity<RestTemplateResponseEnvelope<List<MasterAssetResponseDTO>>> response = restTemplate.exchange(
                baseUrl + "/master_asset/all", HttpMethod.GET, null, typeReference);
        logger.info("response", response);
        Assert.assertEquals(HttpStatus.OK, response.getStatusCode());
    }


    @Test
    public void test2_getAllMasterAssetById() {

        String baseUrl = getBaseUrl(24L, 4l, null);
        ParameterizedTypeReference<RestTemplateResponseEnvelope<MasterAssetResponseDTO>> typeReference =
                new ParameterizedTypeReference<RestTemplateResponseEnvelope<MasterAssetResponseDTO>>() {
                };
        ResponseEntity<RestTemplateResponseEnvelope<MasterAssetResponseDTO>> response = restTemplate.exchange(
                baseUrl + "/master_asset/" + createdId + "", HttpMethod.GET, null, typeReference);
        logger.info("response", response.getBody().getData());
        Assert.assertEquals(response.getBody().getData().getId(), BigInteger.valueOf(16));
        Assert.assertEquals(HttpStatus.OK, response.getStatusCode());
    }


    @Test
    public void test3_removeMasterAssetById() {

        String baseUrl = getBaseUrl(24L, 4l, null);
        UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(baseUrl + "/master_asset/delete/" + createdId);
        ResponseEntity<String> response = restTemplate.exchange(
                builder.toUriString(),
                HttpMethod.DELETE, null, String.class);
        logger.info("response", response);
        Assert.assertEquals(HttpStatus.OK, response.getStatusCode());
    }


    public final String getBaseUrl(Long organizationId, Long countryId, Long unitId) {
        if (organizationId != null && unitId != null && countryId != null) {
            return new StringBuilder(url + "/api/v1/organization/").append(organizationId).append("/country/").append(countryId)
                    .append("/unit/").append(unitId).toString();
        } else if (organizationId != null && countryId != null) {
           return new StringBuilder(url + "/api/v1/organization/").append(organizationId).append("/country/").append(countryId).toString();
        } else {
            throw new UnsupportedOperationException("organization ID must not be null");
        }

    }


}