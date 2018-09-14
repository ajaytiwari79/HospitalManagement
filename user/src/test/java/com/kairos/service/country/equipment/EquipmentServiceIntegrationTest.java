package com.kairos.service.country.equipment;

import com.kairos.UserServiceApplication;
import com.kairos.commons.client.RestTemplateResponseEnvelope;
import com.kairos.config.OrderTest;
import com.kairos.config.OrderTestRunner;
import com.kairos.persistence.model.country.equipment.Equipment;
import com.kairos.persistence.model.country.equipment.EquipmentCategory;
import com.kairos.persistence.model.country.equipment.EquipmentQueryResult;
import com.kairos.persistence.model.user.resources.Resource;
import com.kairos.dto.user.equipment.EquipmentCategoryDTO;
import com.kairos.dto.user.equipment.EquipmentDTO;
import com.kairos.dto.user.equipment.VehicleEquipmentDTO;
import com.kairos.service.exception.ExceptionService;
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

import javax.inject.Inject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by prerna on 12/12/17.
 */
@RunWith(OrderTestRunner.class)
@SpringBootTest(classes = UserServiceApplication.class,webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
public class EquipmentServiceIntegrationTest {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Value("${server.host.http.url}")
    private String url ;
    @Inject
    TestRestTemplate restTemplate;
    @Inject
    EquipmentService equipmentService;
    @Inject
    private ExceptionService exceptionService;
    static String nameOfEquipment = "Test Equipment";
    static Long createdEquipmentId;
    static Long orgId = 71L;
    static Long countryId = 53L;
    static Long unitId = 145L;
    static Long resourceId = 10644L;

    @Test
    @OrderTest(order = 1)
    public void getListOfEquipmentCategories() throws Exception {
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
    @OrderTest(order = 2)
    public void addCountryEquipment() throws Exception {
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
    @OrderTest(order = 3)
    public void updateEquipment() throws Exception {

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
    @OrderTest(order = 4)
    public void getListOfEquipments() throws Exception {
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
    @OrderTest(order = 5)
    public void deleteEquipment() throws Exception {
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


    @Test
    @OrderTest(order = 6)
    public void updateEquipmentsOfReource() throws Exception {
        if(createdEquipmentId == null){
            logger.info("Equipment Id is null");
            Equipment equipment = equipmentService.getEquipmentByName(countryId, nameOfEquipment);
            createdEquipmentId = equipment.getId();
        }
        String baseUrl=getBaseUrl(orgId,null, unitId);
        VehicleEquipmentDTO vehicleEquipmentDTO = new VehicleEquipmentDTO();

        List<Long> equipmentIds = new ArrayList<>();
        equipmentIds.add(createdEquipmentId);
        vehicleEquipmentDTO.setEquipments(equipmentIds);

        HttpEntity<VehicleEquipmentDTO> requestBodyData = new HttpEntity<>(vehicleEquipmentDTO);

        ParameterizedTypeReference<RestTemplateResponseEnvelope<Resource>> resTypeReference =
                new ParameterizedTypeReference<RestTemplateResponseEnvelope<Resource>>() {
                };

        ResponseEntity<RestTemplateResponseEnvelope<Resource>> response = restTemplate.exchange(
                baseUrl+"/resource/"+resourceId+"/equipment",
                HttpMethod.PUT, requestBodyData, resTypeReference);

        Assert.assertTrue(HttpStatus.OK.equals(response.getStatusCode()) ||
                HttpStatus.NOT_FOUND.equals(response.getStatusCode()) );
    }

    @Test
    @OrderTest(order = 7)
    public void getListOfEquipmentsOfResource() throws Exception {
        String baseUrl=getBaseUrl(orgId,null, unitId);
        ParameterizedTypeReference<RestTemplateResponseEnvelope<HashMap<String,List<EquipmentQueryResult>>>> resTypeReference =
                new ParameterizedTypeReference<RestTemplateResponseEnvelope<HashMap<String,List<EquipmentQueryResult>>>>() {
                };

        ResponseEntity<RestTemplateResponseEnvelope<HashMap<String,List<EquipmentQueryResult>>>> response = restTemplate.exchange(
                baseUrl+"/resource/"+resourceId+"/equipment",
                HttpMethod.GET, null, resTypeReference);

        Assert.assertTrue(HttpStatus.OK.equals(response.getStatusCode()) ||
                HttpStatus.NOT_FOUND.equals(response.getStatusCode()) );

        logger.info("response.getBody().getData() : "+response.getBody().getData());
    }

    @Test
    @OrderTest(order = 8)
    public void getListOfEquipmentsOfUnit() throws Exception {
        String baseUrl=getBaseUrl(orgId,null, unitId);
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
            exceptionService.unsupportedOperationException("message.organization.id.notnull");

        }
    return null;
    }
}