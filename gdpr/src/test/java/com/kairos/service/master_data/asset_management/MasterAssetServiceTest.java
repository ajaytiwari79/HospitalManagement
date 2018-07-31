package com.kairos.service.master_data.asset_management;

import com.kairos.KairosGdprApplication;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.web.util.UriComponentsBuilder;

import javax.inject.Inject;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = KairosGdprApplication.class, webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
public class MasterAssetServiceTest {


    Logger LOGGER=LoggerFactory.getLogger(MasterAssetServiceTest.class);

    private final Logger logger = LoggerFactory.getLogger(MasterAssetServiceTest.class);
    @Value("${server.host.http.url}")
    private String url;


    @Inject
    private TestRestTemplate restTemplate;



    @Test
    public void test_getAllMasterAsset() throws Exception {
        String baseUrl = getBaseUrl(24L, 4l,null);
        UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(baseUrl + "/master_asset/all");
        ResponseEntity<String> response = restTemplate.exchange(
                builder.toUriString(),
                HttpMethod.GET, null, String.class);
        logger.info("response", response);
        Assert.assertEquals(HttpStatus.OK, response.getStatusCode());
    }





    public final String getBaseUrl(Long organizationId, Long countryId,Long unitId) {
        if (organizationId != null && unitId != null && countryId !=null) {
            String baseUrl = new StringBuilder(url + "/api/v1/organization/").append(organizationId).append("/country/").append(countryId)
                    .append("/unit/").append(unitId).toString();
            ;
            return baseUrl;
        } else if (organizationId != null && countryId !=null) {
            String baseUrl = new StringBuilder(url + "/api/v1/organization/").append(organizationId).append("/country/").append(countryId).toString();
            return baseUrl;
        } else {
            throw new UnsupportedOperationException("ogranization ID must not be null");
        }

    }


}