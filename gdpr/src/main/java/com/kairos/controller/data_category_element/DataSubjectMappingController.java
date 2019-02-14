package com.kairos.controller.data_category_element;


import com.kairos.dto.gdpr.master_data.DataSubjectDTO;
import com.kairos.dto.gdpr.master_data.MasterDataSubjectDTO;
import com.kairos.service.data_subject_management.DataSubjectMappingService;
import com.kairos.utils.ResponseHandler;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.inject.Inject;
import javax.validation.Valid;

import static com.kairos.constants.ApiConstant.API_ORGANIZATION_URL;
import static com.kairos.constants.ApiConstant.UNIT_URL;
import static com.kairos.constants.ApiConstant.COUNTRY_URL;



@RestController
@RequestMapping(API_ORGANIZATION_URL)
@Api(API_ORGANIZATION_URL)
class DataSubjectMappingController {


    @Inject
    private DataSubjectMappingService dataSubjectMappingService;


    @ApiOperation("create  data Subject mapping ")
    @PostMapping(COUNTRY_URL+"/data_subject")
    public ResponseEntity<Object> addDataSubjectAndMapping(@PathVariable Long countryId, @Valid @RequestBody MasterDataSubjectDTO dataSubjectMappingDto) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true, dataSubjectMappingService.addDataSubjectAndMapping(countryId, dataSubjectMappingDto));
    }

    @ApiOperation("delete data Subject mapping by id ")
    @DeleteMapping(COUNTRY_URL+"/data_subject/{dataSubjectId}")
    public ResponseEntity<Object> deleteDataSubjectAndMappingById(@PathVariable Long countryId, @PathVariable Long dataSubjectId) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true, dataSubjectMappingService.deleteDataSubjectById( dataSubjectId));
    }

    @ApiOperation("get data Subject mapping with data Category and data elements by id ")
    @GetMapping(COUNTRY_URL+"/data_subject/{dataSubjectId}")
    public ResponseEntity<Object> getDataSubjectWithDataCategoryAndElementsById(@PathVariable Long countryId, @PathVariable Long dataSubjectId) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true, dataSubjectMappingService.getDataSubjectAndMappingWithDataByCountryIdAndId(countryId, dataSubjectId));
    }

    @ApiOperation("get all data Subject  ")
    @GetMapping(COUNTRY_URL+"/data_subject")
    public ResponseEntity<Object> getAllDataSubjectWithDataCategoryAndElements(@PathVariable Long countryId) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true, dataSubjectMappingService.getAllDataSubjectWithDataCategoryByCountryId(countryId, true));
    }

    @ApiOperation("update data Subject ")
    @PutMapping(COUNTRY_URL+"/data_subject/{dataSubjectId}")
    public ResponseEntity<Object> updateDataSubjectAndMapping(@PathVariable Long countryId, @PathVariable Long dataSubjectId, @Valid @RequestBody MasterDataSubjectDTO dataSubjectMappingDto) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true, dataSubjectMappingService.updateDataSubjectAndMapping(countryId, dataSubjectId, dataSubjectMappingDto));
    }





    // todo working on it


    @ApiOperation("organization, save  data Subject ")
    @PostMapping(UNIT_URL+"/data_subject")
    public ResponseEntity<Object> saveOrganizationDataSubject(@PathVariable Long unitId, @Valid @RequestBody DataSubjectDTO dataSubjectDTO) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true, dataSubjectMappingService.saveOrganizationDataSubject(unitId, dataSubjectDTO));
    }

    @ApiOperation("organization, delete data Subject by id ")
    @DeleteMapping(UNIT_URL+"/data_subject/{dataSubjectId}")
    public ResponseEntity<Object> deleteOrganizationDataSubjectById(@PathVariable Long unitId, @PathVariable Long dataSubjectId) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true, dataSubjectMappingService.deleteDataSubjectById(dataSubjectId));
    }

    @ApiOperation("Organization get data Subject with data Category and data elements by id ")
    @GetMapping(UNIT_URL+"/data_subject/{dataSubjectId}")
    public ResponseEntity<Object> getOrganizationDataSubjectWithDataCategoryAndElementsById(@PathVariable Long unitId, @PathVariable Long dataSubjectId) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true, dataSubjectMappingService.getDataSubjectWithDataCategoryAndElementByUnitIdAndId(unitId, dataSubjectId));
    }

    @ApiOperation("organization ,get all data Subject  ")
    @GetMapping(UNIT_URL+"/data_subject")
    public ResponseEntity<Object> getOrganizationAllDataSubjectWithDataCategoryAndElements(@PathVariable Long unitId) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true, dataSubjectMappingService.findOrganizationAllDataSubjectWithDataCategoryAndDataElements(unitId));
    }

    @ApiOperation("organization, update data Subject ")
    @PutMapping(UNIT_URL+"/data_subject/{dataSubjectId}")
    public ResponseEntity<Object> updateOrganizationDataSubjectAndMapping(@PathVariable Long unitId, @PathVariable Long dataSubjectId, @Valid @RequestBody DataSubjectDTO dataSubjectDTO) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true, dataSubjectMappingService.updateOrganizationDataSubject(unitId, dataSubjectId, dataSubjectDTO));
    }








  /*  @ApiOperation("get data Subject mapping with data Category and data elements of unit by id ")
    @GetMapping(UNIT_URL + "/dataSubject_mapping/{dataSubjectId}")
    public ResponseEntity<Object> getDataSubjectWithDataCategoryAndElementOfUnitById(@PathVariable Long countryId, @PathVariable Long unitId, @PathVariable BigInteger dataSubjectId) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true, dataSubjectMappingService.getDataSubjectAndMappingWithDataOfUnitOnLeftHierarchySelectionById(unitId, dataSubjectId));
    }

    @ApiOperation("get all data Subject mapping of Unit ")
    @GetMapping(UNIT_URL + "/dataSubject_mapping/all")
    public ResponseEntity<Object> getAllDataSubjectWithDataCategoryAndElementOfUnit(@PathVariable Long countryId, @PathVariable Long unitId) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true, dataSubjectMappingService.getAllDataSubjectAndMappingWithDataOfUnitOnLeftHierarchySelection(unitId));
    }*/

}
