package com.kairos.controller.master_data.data_category_element;


import com.kairos.dto.gdpr.master_data.DataElementDTO;
import com.kairos.service.master_data.data_category_element.DataElementService;
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

import static com.kairos.constants.ApiConstant.API_ORGANIZATION_COUNTRY_URL;
import static com.kairos.constants.ApiConstant.UNIT_URL;


@RestController
@RequestMapping(API_ORGANIZATION_COUNTRY_URL)
@Api(API_ORGANIZATION_COUNTRY_URL)
public class DataElementController {


    @Inject
    private DataElementService dataElementService;


    @ApiOperation("create  data Element ")
    @PostMapping("/data_element/add")
    public ResponseEntity<Object> addDataElement(@PathVariable Long countryId, @Valid @RequestBody ValidateRequestBodyList<DataElementDTO> dataElements) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true, dataElementService.createDataElements(countryId, dataElements.getRequestBody()));

    }


    @ApiOperation("get data Element by id")
    @GetMapping("/data_element/{dataElementId}")
    public ResponseEntity<Object> getDataElement(@PathVariable Long countryId, @PathVariable BigInteger dataElementId) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true, dataElementService.getDataElementById(countryId, dataElementId));

    }

    @ApiOperation("get All data Element ")
    @GetMapping("/data_element/all")
    public ResponseEntity<Object> getAllDataElement(@PathVariable Long countryId) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true, dataElementService.getAllDataElements(countryId));

    }

    @ApiOperation("deleted  data element by id ")
    @DeleteMapping("/data_element/delete/{dataElementId}")
    public ResponseEntity<Object> deleteDataElement(@PathVariable Long countryId, @PathVariable BigInteger dataElementId) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true, dataElementService.deleteDataElement(countryId, dataElementId));

    }


    @ApiOperation("update  data Element ")
    @PutMapping("/data_element/update/{dataElementId}")
    public ResponseEntity<Object> updateDataElement(@PathVariable Long countryId, @PathVariable BigInteger dataElementId, @Valid @RequestBody DataElementDTO dataElementDTO) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true, dataElementService.updateDataElement(countryId, dataElementId, dataElementDTO));
    }

    @ApiOperation("get data Element of unit by id")
    @GetMapping(UNIT_URL + "/data_element/{id}")
    public ResponseEntity<Object> getDataElementOfUnitById(@PathVariable Long unitId, @PathVariable BigInteger id) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true, dataElementService.getDataElementByIdOnLeftHierarchySelection(unitId, id));

    }

    @ApiOperation("get All data Element of unit ")
    @GetMapping(UNIT_URL + "/data_element/all")
    public ResponseEntity<Object> getAllDataElementOfUnit(@PathVariable Long unitId) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true, dataElementService.getAllDataElementOnLeftHierarchySelection(unitId));

    }


}
