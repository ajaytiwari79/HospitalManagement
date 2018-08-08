package com.kairos.service.master_data.asset_management;

import com.kairos.KairosGdprApplication;
import com.kairos.client.dto.RestTemplateResponseEnvelope;
import com.kairos.dto.master_data.AssetTypeDTO;
import com.kairos.dto.master_data.MasterAssetDTO;
import com.kairos.persistance.model.master_data.default_asset_setting.AssetType;
import com.kairos.response.dto.master_data.AssetTypeResponseDTO;
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
public class AssetTypeServiceTest {


    Logger LOGGER = LoggerFactory.getLogger(MasterAssetServiceTest.class);

    private final Logger logger = LoggerFactory.getLogger(MasterAssetServiceTest.class);
    @Value("${server.host.http.url}")
    private String url;


    @Inject
    private TestRestTemplate restTemplate;


    private BigInteger createdId;


    @Test
    public void test_createAssetTypeWithSubAssetType() throws Exception {

        String baseUrl = getBaseUrl(24L, 4l, null);
        AssetTypeDTO assetTypeDTO = new AssetTypeDTO("asset type1");
        assetTypeDTO.setSubAssetTypes(new ArrayList<>(Arrays.asList(new AssetTypeDTO("sub asset A"), new AssetTypeDTO("sub Asset b"))));

        HttpEntity<AssetTypeDTO> entity = new HttpEntity<>(assetTypeDTO);
        ResponseEntity<Map> response = restTemplate.exchange(
                baseUrl + "/asset_type/add",        // createdId = response.getBody().getData().getId();
                HttpMethod.POST, entity, Map.class);
        logger.info("response", response);
        LinkedHashMap<String, Object> assetType = (LinkedHashMap<String, Object>) response.getBody().get("data");
        createdId = BigInteger.valueOf((Integer) assetType.get("id"));
        Assert.assertEquals(HttpStatus.OK, response.getStatusCode());
        Assert.assertNotEquals(null, createdId);
        Assert.assertNotEquals(0, ((List<AssetType>) assetType.get("subAssetTypes")).size());


    }

    @Test
    public void test1_getAllAssetType() throws Exception {
        String baseUrl = getBaseUrl(24L, 4l, null);
        ParameterizedTypeReference<RestTemplateResponseEnvelope<List<AssetTypeResponseDTO>>> typeReference =
                new ParameterizedTypeReference<RestTemplateResponseEnvelope<List<AssetTypeResponseDTO>>>() {
                };
        ResponseEntity<RestTemplateResponseEnvelope<List<AssetTypeResponseDTO>>> response = restTemplate.exchange(
                baseUrl + "/asset_type/all", HttpMethod.GET, null, typeReference);
        logger.info("response", response);
        Assert.assertEquals(HttpStatus.OK, response.getStatusCode());
    }


    @Test
    public void test2_getAssetTypebyId() throws Exception {

        String baseUrl = getBaseUrl(24L, 4l, null);
        ParameterizedTypeReference<RestTemplateResponseEnvelope<AssetTypeResponseDTO>> typeReference =
                new ParameterizedTypeReference<RestTemplateResponseEnvelope<AssetTypeResponseDTO>>() {
                };
        ResponseEntity<RestTemplateResponseEnvelope<AssetTypeResponseDTO>> response = restTemplate.exchange(
                baseUrl + "/asset_type/" + createdId + "", HttpMethod.GET, null, typeReference);
        logger.info("response", response.getBody().getData());
        Assert.assertEquals(response.getBody().getData().getId(), BigInteger.valueOf(16));
        Assert.assertEquals(HttpStatus.OK, response.getStatusCode());
    }


    @Test
    public void test3_removeAssetType() throws Exception {

        createdId=BigInteger.valueOf(36);
        String baseUrl = getBaseUrl(24L, 4l, null);
        UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(baseUrl + "/asset_type/delete/" + createdId);
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
            throw new UnsupportedOperationException("organization ID must not be null");
        }

    }


}