package com.kairos.controller.master_data.data_category_element;


import com.kairos.dto.gdpr.master_data.DataSubjectMappingDTO;
import com.kairos.service.master_data.data_category_element.DataSubjectMappingService;
import com.kairos.utils.ResponseHandler;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.inject.Inject;
import javax.validation.Valid;
import java.math.BigInteger;

import static com.kairos.constants.ApiConstant.API_ORGANIZATION_COUNTRY_URL;
import static com.kairos.constants.ApiConstant.UNIT_URL;


@RestController
@RequestMapping(API_ORGANIZATION_COUNTRY_URL)
@Api(API_ORGANIZATION_COUNTRY_URL)
public class DataSubjectMappingController {


    @Inject
    private DataSubjectMappingService dataSubjectMappingService;


    @ApiOperation("create  data Subject mapping ")
    @PostMapping("/dataSubject_mapping/add")
    public ResponseEntity<Object> addDataSubjectAndMapping(@PathVariable Long countryId, @Valid @RequestBody DataSubjectMappingDTO dataSubjectMappingDto) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true, dataSubjectMappingService.addDataSubjectAndMapping(countryId, dataSubjectMappingDto));
    }

    @ApiOperation("delete data Subject mapping by id ")
    @DeleteMapping("/dataSubject_mapping/delete/{id}")
    public ResponseEntity<Object> deleteDataSubjectAndMappingById(@PathVariable Long countryId, @PathVariable BigInteger id) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true, dataSubjectMappingService.deleteDataSubjectAndMapping(countryId, id));
    }

    @ApiOperation("get data Subject mapping with data Category and data elements by id ")
    @GetMapping("/dataSubject_mapping/{id}")
    public ResponseEntity<Object> getDataSubjectWithDataCategoryAndElementsById(@PathVariable Long countryId, @PathVariable BigInteger id) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true, dataSubjectMappingService.getDataSubjectAndMappingWithData(countryId, id));
    }

    @ApiOperation("get all data Subject mapping ")
    @GetMapping("/dataSubject_mapping/all")
    public ResponseEntity<Object> getAllDataSubjectWithDataCategoryAndElements(@PathVariable Long countryId) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true, dataSubjectMappingService.getAllDataSubjectAndMappingWithData(countryId));
    }

    @ApiOperation("update data Subject mapping ")
    @PutMapping("/dataSubject_mapping/update/{id}")
    public ResponseEntity<Object> updateDataSubjectAndMapping(@PathVariable Long countryId, @PathVariable BigInteger id, @Valid @RequestBody DataSubjectMappingDTO dataSubjectMappingDto) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true, dataSubjectMappingService.updateDataSubjectAndMapping(countryId, id, dataSubjectMappingDto));
    }

    @ApiOperation("get data Subject mapping with data Category and data elements of unit by id ")
    @GetMapping(UNIT_URL + "/dataSubject_mapping/{dataSubjectId}")
    public ResponseEntity<Object> getDataSubjectWithDataCategoryAndElementOfUnitById(@PathVariable Long countryId, @PathVariable Long unitId, @PathVariable BigInteger dataSubjectId) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true, dataSubjectMappingService.getDataSubjectAndMappingWithDataOfUnitOnLeftHierarchySelectionById(unitId, dataSubjectId));
    }

    @ApiOperation("get all data Subject mapping of Unit ")
    @GetMapping(UNIT_URL + "/dataSubject_mapping/all")
    public ResponseEntity<Object> getAllDataSubjectWithDataCategoryAndElementOfUnit(@PathVariable Long countryId, @PathVariable Long unitId) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true, dataSubjectMappingService.getAllDataSubjectAndMappingWithDataOfUnitOnLeftHierarchySelection(unitId));
    }

}
