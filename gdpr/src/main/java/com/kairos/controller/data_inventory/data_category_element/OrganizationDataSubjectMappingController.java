package com.kairos.controller.data_inventory.data_category_element;


import com.kairos.dto.gdpr.data_inventory.OrganizationDataSubjectDTO;
import com.kairos.dto.gdpr.data_inventory.OrganizationDataSubjectBasicDTO;
import com.kairos.service.data_inventory.data_category_element.OrganizationDataSubjectMappingService;
import com.kairos.utils.ResponseHandler;
import com.kairos.utils.ValidateRequestBodyList;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.inject.Inject;
import javax.validation.Valid;

import java.math.BigInteger;

import static com.kairos.constants.ApiConstant.API_ORGANIZATION_UNIT_URL;


@RestController
@RequestMapping(API_ORGANIZATION_UNIT_URL)
@Api(API_ORGANIZATION_UNIT_URL)
public class OrganizationDataSubjectMappingController {


    @Inject
    private OrganizationDataSubjectMappingService organizationDataSubjectMappingService;


    @ApiOperation(value = "create data Subject with data category and data element")
    @PostMapping("dataSubject_mapping/add")
    public ResponseEntity<Object> createDataSubjectWithDataCategoryAndDataElement(@PathVariable Long unitId, @RequestBody @Valid ValidateRequestBodyList<OrganizationDataSubjectDTO> dataSubjectDTOs) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true, organizationDataSubjectMappingService.createDataSubjectWithDataCategoriesAndDataElements(unitId, dataSubjectDTOs.getRequestBody()));
    }


    @ApiOperation(value = "create data Subject with data category and data element")
    @DeleteMapping("dataSubject_mapping/delete/{dataSubjectId}")
    public ResponseEntity<Object> deleteDataSubjectMappingById(@PathVariable Long unitId, @PathVariable BigInteger dataSubjectId) {
    return ResponseHandler.generateResponse(HttpStatus.OK, true, organizationDataSubjectMappingService.deleteDataSubjectById(unitId, dataSubjectId));
    }



    @ApiOperation(value = "create data Subject with data category and data element")
    @GetMapping("dataSubject_mapping/all")
    public ResponseEntity<Object> getAllDataSubjectWithDataCategoryAndDataElementByUnitId(@PathVariable Long unitId) {
      return ResponseHandler.generateResponse(HttpStatus.OK, true, organizationDataSubjectMappingService.getAllDataSubjectByUnitId(unitId));
    }


    @ApiOperation(value = "create data Subject with data category and data element")
    @GetMapping("dataSubject_mapping/{dataSubjectId}")
    public ResponseEntity<Object> getDataSubjectWithDataCategoryAndDataElementByUnitId(@PathVariable Long unitId, @PathVariable BigInteger dataSubjectId) {
             return ResponseHandler.generateResponse(HttpStatus.OK, true, organizationDataSubjectMappingService.getDataSubjectByUnitId(unitId, dataSubjectId));
    }



    @ApiOperation(value = "create data Subject with data category and data element")
    @PutMapping("dataSubject_mapping/update/{dataSubjectId}")
    public ResponseEntity<Object> getAllDataSubjectWithDataCategoryAndDataElementByUnitId(@PathVariable Long unitId, @PathVariable BigInteger dataSubjectId, @RequestBody @Valid OrganizationDataSubjectBasicDTO dataSubjectMappingDTO) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true, organizationDataSubjectMappingService.updateDataSubjectMappingById(unitId,dataSubjectId,dataSubjectMappingDTO));
    }






}
