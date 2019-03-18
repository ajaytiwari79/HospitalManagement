package com.kairos.controller.data_category_element;


import com.kairos.dto.gdpr.master_data.DataSubjectDTO;
import com.kairos.dto.gdpr.master_data.MasterDataSubjectDTO;
import com.kairos.service.data_subject_management.DataSubjectService;
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
class DataSubjectController {


    @Inject
    private DataSubjectService dataSubjectService;


    @ApiOperation("create  data Subject mapping ")
    @PostMapping(COUNTRY_URL+"/data_subject")
    public ResponseEntity<Object> addDataSubjectAndMapping(@PathVariable Long countryId, @Valid @RequestBody MasterDataSubjectDTO dataSubjectDto) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true, dataSubjectService.addDataSubjectAndMapping(countryId, dataSubjectDto));
    }

    @ApiOperation("delete data Subject mapping by id ")
    @DeleteMapping(COUNTRY_URL+"/data_subject/{dataSubjectId}")
    public ResponseEntity<Object> deleteDataSubjectAndMappingById(@PathVariable Long countryId, @PathVariable Long dataSubjectId) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true, dataSubjectService.deleteDataSubjectById( dataSubjectId));
    }

    @ApiOperation("get data Subject mapping with data Category and data elements by id ")
    @GetMapping(COUNTRY_URL+"/data_subject/{dataSubjectId}")
    public ResponseEntity<Object> getDataSubjectWithDataCategoryAndElementsById(@PathVariable Long countryId, @PathVariable Long dataSubjectId) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true, dataSubjectService.getDataSubjectAndMappingWithDataByCountryIdAndId(countryId, dataSubjectId));
    }

    @ApiOperation("get all data Subject  ")
    @GetMapping(COUNTRY_URL+"/data_subject")
    public ResponseEntity<Object> getAllDataSubjectWithDataCategoryAndElements(@PathVariable Long countryId) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true, dataSubjectService.getAllDataSubjectWithDataCategoryByCountryId(countryId, true));
    }

    @ApiOperation("update data Subject ")
    @PutMapping(COUNTRY_URL+"/data_subject/{dataSubjectId}")
    public ResponseEntity<Object> updateDataSubjectAndMapping(@PathVariable Long countryId, @PathVariable Long dataSubjectId, @Valid @RequestBody MasterDataSubjectDTO dataSubjectDto) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true, dataSubjectService.updateDataSubjectAndMapping(countryId, dataSubjectId, dataSubjectDto));
    }





    // todo working on it


    @ApiOperation("organization, save  data Subject ")
    @PostMapping(UNIT_URL+"/data_subject")
    public ResponseEntity<Object> saveOrganizationDataSubject(@PathVariable Long unitId, @Valid @RequestBody DataSubjectDTO dataSubjectDTO) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true, dataSubjectService.saveOrganizationDataSubject(unitId, dataSubjectDTO));
    }

    @ApiOperation("organization, delete data Subject by id ")
    @DeleteMapping(UNIT_URL+"/data_subject/{dataSubjectId}")
    public ResponseEntity<Object> deleteOrganizationDataSubjectById(@PathVariable Long unitId, @PathVariable Long dataSubjectId) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true, dataSubjectService.deleteDataSubjectById(dataSubjectId));
    }

    @ApiOperation("Organization get data Subject with data Category and data elements by id ")
    @GetMapping(UNIT_URL+"/data_subject/{dataSubjectId}")
    public ResponseEntity<Object> getOrganizationDataSubjectWithDataCategoryAndElementsById(@PathVariable Long unitId, @PathVariable Long dataSubjectId) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true, dataSubjectService.getDataSubjectWithDataCategoryAndElementByUnitIdAndId(unitId, dataSubjectId));
    }

    @ApiOperation("organization ,get all data Subject  ")
    @GetMapping(UNIT_URL+"/data_subject")
    public ResponseEntity<Object> getOrganizationAllDataSubjectWithDataCategoryAndElements(@PathVariable Long unitId) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true, dataSubjectService.findOrganizationAllDataSubjectWithDataCategoryAndDataElements(unitId));
    }

    @ApiOperation("organization, update data Subject ")
    @PutMapping(UNIT_URL+"/data_subject/{dataSubjectId}")
    public ResponseEntity<Object> updateOrganizationDataSubjectAndMapping(@PathVariable Long unitId, @PathVariable Long dataSubjectId, @Valid @RequestBody DataSubjectDTO dataSubjectDTO) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true, dataSubjectService.updateOrganizationDataSubject(unitId, dataSubjectId, dataSubjectDTO));
    }

}
