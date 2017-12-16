package com.kairos.service.country.equipment;

import com.kairos.UserServiceApplication;
import com.kairos.client.dto.RestTemplateResponseEnvelope;
import com.kairos.persistence.model.user.country.equipment.Equipment;
import com.kairos.persistence.model.user.country.equipment.EquipmentCategory;
import com.kairos.response.dto.web.equipment.EquipmentCategoryDTO;
import com.kairos.response.dto.web.equipment.EquipmentDTO;
import com.kairos.service.country.feature.FeatureService;
import org.junit.Assert;
import org.junit.FixMethodOrder;
import org.junit.Ignore;
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

import java.util.HashMap;
import java.util.List;

import static org.junit.Assert.*;

/**
 * Created by prerna on 12/12/17.
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = UserServiceApplication.class,webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class EquipmentServiceIntegrationTest {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Value("${server.host.http.url}")
    private String url ;
    @Inject
    TestRestTemplate restTemplate;
    @Inject
    EquipmentService equipmentService;

    static String nameOfEquipment = "Test Equipment";
    static Long createdEquipmentId;
    static Long orgId = 71L;
    static Long countryId = 53L;

    @Test
    public void test1_getListOfEquipmentCategories() throws Exception {
        String baseUrl=getBaseUrl(orgId,countryId, null);
        ParameterizedTypeReference<RestTemplateResponseEnvelope<List<EquipmentCategory>>> resTypeReference =
                new ParameterizedTypeReference<RestTemplateResponseEnvelope<List<EquipmentCategory>>>() {
                };

        ResponseEntity<RestTemplateResponseEnvelope<List<EquipmentCategory>>> response = restTemplate.exchange(
                baseUrl+"/equipment_category",
                HttpMethod.GET, null, resTypeReference);

        Assert.assertTrue(HttpStatus.OK.equals(response.getStatusCode()) ||
                HttpStatus.NOT_FOUND.equals(response.getStatusCode()) );

        logger.info("response.getBody().getData() : "+response.getBody().getData());
    }

    @Test
    public void test2_addCountryEquipment() throws Exception {
        String baseUrl=getBaseUrl(orgId,countryId, null);
        EquipmentCategory equipmentCategory = equipmentService.getEquipmentCategoryByName("Small");
        logger.info("Equipment name : "+equipmentCategory.getId());
        EquipmentCategoryDTO equipmentCategoryDTO = new EquipmentCategoryDTO(equipmentCategory.getId());
        EquipmentDTO equipmentDTO = new EquipmentDTO(nameOfEquipment, "Test description", equipmentCategoryDTO);
        HttpEntity<EquipmentDTO> requestBodyData = new HttpEntity<>(equipmentDTO);

        ParameterizedTypeReference<RestTemplateResponseEnvelope<Equipment>> resTypeReference =
                new ParameterizedTypeReference<RestTemplateResponseEnvelope<Equipment>>() {
                };

        ResponseEntity<RestTemplateResponseEnvelope<Equipment>> response = restTemplate.exchange(
                baseUrl+"/equipment",
                HttpMethod.POST, requestBodyData, resTypeReference);

        Assert.assertTrue(HttpStatus.CREATED.equals(response.getStatusCode()) || HttpStatus.CONFLICT.equals(response.getStatusCode()));
        if(HttpStatus.CREATED.equals(response.getStatusCode())) {
            createdEquipmentId = response.getBody().getData().getId();
        }

    }

    @Test
    public void test3_updateEquipment() throws Exception {

        if(createdEquipmentId == null){
            logger.info("Equipment Id is null");
            Equipment equipment = equipmentService.getEquipmentByName(countryId, nameOfEquipment);
            createdEquipmentId = equipment.getId();
        }
        logger.info("createdEquipmentId : "+createdEquipmentId);
        String baseUrl=getBaseUrl(orgId,countryId, null);

        EquipmentCategory equipmentCategory = equipmentService.getEquipmentCategoryByName("Medium");
        EquipmentCategoryDTO equipmentCategoryDTO = new EquipmentCategoryDTO(equipmentCategory.getId());
        EquipmentDTO equipmentDTO = new EquipmentDTO(nameOfEquipment, "Test description updated", equipmentCategoryDTO);


        HttpEntity<EquipmentDTO> requestBodyData = new HttpEntity<>(equipmentDTO);

        ParameterizedTypeReference<RestTemplateResponseEnvelope<Equipment>> resTypeReference =
                new ParameterizedTypeReference<RestTemplateResponseEnvelope<Equipment>>() {
                };

        ResponseEntity<RestTemplateResponseEnvelope<Equipment>> response = restTemplate.exchange(
                baseUrl+"/equipment/"+createdEquipmentId,
                HttpMethod.PUT, requestBodyData, resTypeReference);

        Assert.assertTrue(HttpStatus.OK.equals(response.getStatusCode()) ||
                HttpStatus.NOT_FOUND.equals(response.getStatusCode()) ||
                HttpStatus.CONFLICT.equals(response.getStatusCode()) );
    }



    @Test
    public void test4_getListOfEquipments() throws Exception {
        String baseUrl=getBaseUrl(orgId,countryId, null);
        ParameterizedTypeReference<RestTemplateResponseEnvelope<HashMap<String,Object>>> resTypeReference =
                new ParameterizedTypeReference<RestTemplateResponseEnvelope<HashMap<String,Object>>>() {
                };

        ResponseEntity<RestTemplateResponseEnvelope<HashMap<String,Object>>> response = restTemplate.exchange(
                baseUrl+"/equipment",
                HttpMethod.GET, null, resTypeReference);

        Assert.assertTrue(HttpStatus.OK.equals(response.getStatusCode()) ||
                HttpStatus.NOT_FOUND.equals(response.getStatusCode()) );

        logger.info("response.getBody().getData() : "+response.getBody().getData());
    }

    @Test
    public void test5_deleteEquipment() throws Exception {
        if(createdEquipmentId == null){
            logger.info("Equipment Id is null");
            Equipment equipment = equipmentService.getEquipmentByName(countryId, nameOfEquipment);
            createdEquipmentId = equipment.getId();
        }
        logger.info("createdEquipmentId : "+createdEquipmentId);
        String baseUrl=getBaseUrl(orgId,countryId, null);
        ParameterizedTypeReference<RestTemplateResponseEnvelope<Boolean>> resTypeReference =
                new ParameterizedTypeReference<RestTemplateResponseEnvelope<Boolean>>() {
                };

        ResponseEntity<RestTemplateResponseEnvelope<Boolean>> response = restTemplate.exchange(
                baseUrl+"/equipment/"+createdEquipmentId,
                HttpMethod.DELETE, null, resTypeReference);

        Assert.assertTrue(HttpStatus.OK.equals(response.getStatusCode()) ||
                HttpStatus.NOT_FOUND.equals(response.getStatusCode()) );
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