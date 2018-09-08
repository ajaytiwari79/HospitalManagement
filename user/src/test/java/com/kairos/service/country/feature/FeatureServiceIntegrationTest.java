package com.kairos.service.country.feature;

import com.kairos.UserServiceApplication;
import com.kairos.commons.client.RestTemplateResponseEnvelope;
import com.kairos.persistence.model.country.feature.Feature;
import com.kairos.persistence.model.country.feature.FeatureQueryResult;
import com.kairos.persistence.model.user.resources.Resource;
import com.kairos.persistence.model.user.resources.Vehicle;
import com.kairos.dto.user.country.feature.FeatureDTO;
import com.kairos.dto.user.country.feature.VehicleFeaturesDTO;
import org.junit.Assert;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.MethodSorters;
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

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by prerna on 4/12/17.
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = UserServiceApplication.class,webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class FeatureServiceIntegrationTest {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Value("${server.host.http.url}")
    private String url ;
    @Inject
    TestRestTemplate restTemplate;
    @Inject
    FeatureService featureService;

    static String nameOfFeauture = "Test Feature";
    static Long createdFeatureId;
    static Long orgId = 71L;
    static Long countryId = 53L;
    static Long unitId = 145L;
    static Long resourceId = 6353L;
    static Long vehicleId = 10599L;

    @Test
    public void test1_addCountryFeature() throws Exception {
        String baseUrl=getBaseUrl(orgId,countryId, null);
        FeatureDTO featureDTO = new FeatureDTO(nameOfFeauture, "Test description");
        HttpEntity<FeatureDTO> requestBodyData = new HttpEntity<>(featureDTO);

        ParameterizedTypeReference<RestTemplateResponseEnvelope<Feature>> resTypeReference =
                new ParameterizedTypeReference<RestTemplateResponseEnvelope<Feature>>() {
                };

        ResponseEntity<RestTemplateResponseEnvelope<Feature>> response = restTemplate.exchange(
                baseUrl+"/feature",
                HttpMethod.POST, requestBodyData, resTypeReference);

        Assert.assertTrue(HttpStatus.CREATED.equals(response.getStatusCode()) || HttpStatus.CONFLICT.equals(response.getStatusCode()));
        if(HttpStatus.CREATED.equals(response.getStatusCode())) {
            createdFeatureId = response.getBody().getData().getId();
        }
    }

    @Test
    public void test2_updateFeature() throws Exception {
        if(createdFeatureId == null){
            logger.info("Feature Id is null");
            Feature feature = featureService.getFeatureByName(countryId, nameOfFeauture);
            createdFeatureId = feature.getId();
        }
        String baseUrl=getBaseUrl(orgId,countryId, null);
        FeatureDTO featureDTO = new FeatureDTO(nameOfFeauture, "Updated Desription");
        HttpEntity<FeatureDTO> requestBodyData = new HttpEntity<>(featureDTO);

        ParameterizedTypeReference<RestTemplateResponseEnvelope<Feature>> resTypeReference =
                new ParameterizedTypeReference<RestTemplateResponseEnvelope<Feature>>() {
                };

        ResponseEntity<RestTemplateResponseEnvelope<Feature>> response = restTemplate.exchange(
                baseUrl+"/feature/"+createdFeatureId,
                HttpMethod.PUT, requestBodyData, resTypeReference);

        Assert.assertTrue(HttpStatus.OK.equals(response.getStatusCode()) ||
                HttpStatus.NOT_FOUND.equals(response.getStatusCode()) ||
                HttpStatus.CONFLICT.equals(response.getStatusCode()) );
    }

    @Test
    public void test3_getListOfFeatures() throws Exception {
        String baseUrl=getBaseUrl(orgId,countryId, null);
        ParameterizedTypeReference<RestTemplateResponseEnvelope<HashMap<String,Object>>> resTypeReference =
                new ParameterizedTypeReference<RestTemplateResponseEnvelope<HashMap<String,Object>>>() {
                };

        ResponseEntity<RestTemplateResponseEnvelope<HashMap<String,Object>>> response = restTemplate.exchange(
                baseUrl+"/feature",
                HttpMethod.GET, null, resTypeReference);

        Assert.assertTrue(HttpStatus.OK.equals(response.getStatusCode()) ||
                HttpStatus.NOT_FOUND.equals(response.getStatusCode()) );

        logger.info("response.getBody().getData() : "+response.getBody().getData());
    }

    @Test
    public void test4_deleteFeature() throws Exception {
        if(createdFeatureId == null){
            logger.info("Feature Id is null");
            Feature feature = featureService.getFeatureByName(countryId, nameOfFeauture);
            createdFeatureId = feature.getId();
        }
        String baseUrl=getBaseUrl(orgId,countryId, null);
        ParameterizedTypeReference<RestTemplateResponseEnvelope<Boolean>> resTypeReference =
                new ParameterizedTypeReference<RestTemplateResponseEnvelope<Boolean>>() {
                };

        ResponseEntity<RestTemplateResponseEnvelope<Boolean>> response = restTemplate.exchange(
                baseUrl+"/feature/"+createdFeatureId,
                HttpMethod.DELETE, null, resTypeReference);

        Assert.assertTrue(HttpStatus.OK.equals(response.getStatusCode()) ||
                HttpStatus.NOT_FOUND.equals(response.getStatusCode()) );
    }

    @Test
    public void test5_updateFeaturesOfVehicle() throws Exception {
        if(createdFeatureId == null){
            logger.info("Feature Id is null");
            Feature feature = featureService.getFeatureByName(countryId, nameOfFeauture);
            createdFeatureId = feature.getId();
        }
        String baseUrl=getBaseUrl(orgId,countryId, null);
        VehicleFeaturesDTO vehicleFeaturesDTO = new VehicleFeaturesDTO();
        List<Long> featureIds = new ArrayList<>();
        featureIds.add(createdFeatureId);
        vehicleFeaturesDTO.setFeatures(featureIds);

        HttpEntity<VehicleFeaturesDTO> requestBodyData = new HttpEntity<>(vehicleFeaturesDTO);

        ParameterizedTypeReference<RestTemplateResponseEnvelope<Vehicle>> resTypeReference =
                new ParameterizedTypeReference<RestTemplateResponseEnvelope<Vehicle>>() {
                };

        ResponseEntity<RestTemplateResponseEnvelope<Vehicle>> response = restTemplate.exchange(
                baseUrl+"/vehicle/"+vehicleId+"/feature",
                HttpMethod.PUT, requestBodyData, resTypeReference);

        Assert.assertTrue(HttpStatus.OK.equals(response.getStatusCode()) ||
                HttpStatus.NOT_FOUND.equals(response.getStatusCode()) );
    }

    @Test
    public void test6_updateFeaturesOfReource() throws Exception {
        if(createdFeatureId == null){
            logger.info("Feature Id is null");
            Feature feature = featureService.getFeatureByName(countryId, nameOfFeauture);

            createdFeatureId = feature.getId();
        }
        String baseUrl=getBaseUrl(orgId,null, unitId);
        VehicleFeaturesDTO vehicleFeaturesDTO = new VehicleFeaturesDTO();

        List<Long> featureIds = new ArrayList<>();
        featureIds.add(createdFeatureId);
        vehicleFeaturesDTO.setFeatures(featureIds);
        logger.info("vehicleFeaturesDTO : "+vehicleFeaturesDTO.getFeatures().get(0));

        HttpEntity<VehicleFeaturesDTO> requestBodyData = new HttpEntity<>(vehicleFeaturesDTO);

        ParameterizedTypeReference<RestTemplateResponseEnvelope<Resource>> resTypeReference =
                new ParameterizedTypeReference<RestTemplateResponseEnvelope<Resource>>() {
                };

        ResponseEntity<RestTemplateResponseEnvelope<Resource>> response = restTemplate.exchange(
                baseUrl+"/resource/"+resourceId+"/feature",
                HttpMethod.PUT, requestBodyData, resTypeReference);

        Assert.assertTrue(HttpStatus.OK.equals(response.getStatusCode()) ||
                HttpStatus.NOT_FOUND.equals(response.getStatusCode()) );
    }

    @Test
    public void test7_getListOfFeaturesOfResource() throws Exception {
        String baseUrl=getBaseUrl(orgId,null, unitId);
        ParameterizedTypeReference<RestTemplateResponseEnvelope<HashMap<String,List<FeatureQueryResult>>>> resTypeReference =
                new ParameterizedTypeReference<RestTemplateResponseEnvelope<HashMap<String,List<FeatureQueryResult>>>>() {
                };

        ResponseEntity<RestTemplateResponseEnvelope<HashMap<String,List<FeatureQueryResult>>>> response = restTemplate.exchange(
                baseUrl+"/resource/"+resourceId+"/feature",
                HttpMethod.GET, null, resTypeReference);

        Assert.assertTrue(HttpStatus.OK.equals(response.getStatusCode()) ||
                HttpStatus.NOT_FOUND.equals(response.getStatusCode()) );

        logger.info("response.getBody().getData() : "+response.getBody().getData());
    }

    public final String getBaseUrl(Long organizationId,Long countryId, Long unitId){
        if(organizationId!=null && countryId!=null ){
            String baseUrl=new StringBuilder(url+"/api/v1/organization/").append(organizationId)
                    .append("/country/").append(countryId).toString();
            return baseUrl;
        } else if(organizationId!=null && unitId!=null ){
            String baseUrl=new StringBuilder(url+"/api/v1/organization/").append(organizationId)
                    .append("/unit/").append(unitId).toString();
            return baseUrl;
        } else if(organizationId!=null ){
            String baseUrl=new StringBuilder(url+"/api/v1/organization/").append(organizationId).toString();
            return baseUrl;
        } else{
            throw new UnsupportedOperationException("organization ID must not be null");
        }

    }
}