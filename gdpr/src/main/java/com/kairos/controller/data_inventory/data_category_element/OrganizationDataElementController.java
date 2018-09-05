package com.kairos.controller.data_inventory.data_category_element;


import com.kairos.service.data_inventory.data_category_element.OrganizationDataElementService;
import com.kairos.utils.ResponseHandler;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.inject.Inject;

import java.math.BigInteger;

import static com.kairos.constants.ApiConstant.API_ORGANIZATION_URL_UNIT_URL;


@RestController
@RequestMapping(API_ORGANIZATION_URL_UNIT_URL)
@Api(API_ORGANIZATION_URL_UNIT_URL)
public class OrganizationDataElementController {


    @Inject
    private OrganizationDataElementService organizationDataElementService;


    @ApiOperation(value = "Delete Data Element")
    @DeleteMapping("/data_category/{dataCategoryId}/data_element/{elementId}")
    public ResponseEntity<Object> deleteDataElement(@PathVariable Long unitId, @PathVariable BigInteger dataCategoryId, @PathVariable BigInteger elementId) {

        if (unitId == null) {
            return ResponseHandler.invalidResponse(HttpStatus.BAD_REQUEST, false, "Organization Id can't be null");
        }
        return ResponseHandler.generateResponse(HttpStatus.OK, true, organizationDataElementService.deleteDataElementById(unitId, dataCategoryId, elementId));
    }


    @ApiOperation(value = "get all Data Element")
    @GetMapping("/data_element/all")
    public ResponseEntity<Object> getAllDataElementByUnitId(@PathVariable Long unitId) {

        if (unitId == null) {
            return ResponseHandler.invalidResponse(HttpStatus.BAD_REQUEST, false, "Organization Id can't be null");
        }
        return ResponseHandler.generateResponse(HttpStatus.OK, true, organizationDataElementService.getAllDataElementByUnitId(unitId));
    }


    @ApiOperation(value = "get all Data Element")
    @GetMapping("/data_element/{id}")
    public ResponseEntity<Object> getDataElementByUnitIdAndId(@PathVariable Long unitId, @PathVariable BigInteger id) {

        if (unitId == null) {
            return ResponseHandler.invalidResponse(HttpStatus.BAD_REQUEST, false, "Organization Id can't be null");
        }
        return ResponseHandler.generateResponse(HttpStatus.OK, true, organizationDataElementService.getDataElementByUnitIdAndId(unitId, id));
    }


}
