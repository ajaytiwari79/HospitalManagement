package com.kairos.controller.master_data_management.processing_activity_masterdata;


import com.kairos.persistance.model.master_data_management.processing_activity_masterdata.ResponsibilityType;
import com.kairos.service.master_data_management.processing_activity_masterdata.ResponsibilityTypeService;
import com.kairos.utils.ResponseHandler;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.inject.Inject;
import javax.validation.Valid;
import java.math.BigInteger;
import java.util.List;

import static com.kairos.constant.ApiConstant.API_RESPONSIBILITY_TYPE;
/*
 *
 *  created by bobby 20/5/2018
 * */


@RestController
@RequestMapping(API_RESPONSIBILITY_TYPE)
@Api(API_RESPONSIBILITY_TYPE)
public class ResponsibilityTypeController {

    private static final Logger LOGGER = LoggerFactory.getLogger(ResponsibilityTypeController.class);

    @Inject
    private ResponsibilityTypeService responsibilityTypeService;


    @ApiOperation("add ResponsibilityType  ")
    @PostMapping("/add")
    public ResponseEntity<Object> createResponsibilityType(@PathVariable Long countryId, @RequestBody List<ResponsibilityType> responsibilityTypes) {
        if (countryId == null) {
            return ResponseHandler.invalidResponse(HttpStatus.BAD_REQUEST, false, "country id is null");
        }
        return ResponseHandler.generateResponse(HttpStatus.OK, true, responsibilityTypeService.createResponsibilityType(countryId, responsibilityTypes));

    }


    @ApiOperation("get ResponsibilityType  by id")
    @GetMapping("/{id}")
    public ResponseEntity<Object> getResponsibilityType(@PathVariable Long countryId, @PathVariable BigInteger id) {
        if (id == null) {
            return ResponseHandler.invalidResponse(HttpStatus.BAD_REQUEST, false, "id is null");
        } else if (countryId == null) {
            return ResponseHandler.invalidResponse(HttpStatus.BAD_REQUEST, false, "country id is null");

        } else
            return ResponseHandler.generateResponse(HttpStatus.OK, true, responsibilityTypeService.getResponsibilityType(countryId, id));

    }


    @ApiOperation("get all ResponsibilityType ")
    @GetMapping("/all")
    public ResponseEntity<Object> getAllResponsibilityType() {
        return ResponseHandler.generateResponse(HttpStatus.OK, true, responsibilityTypeService.getAllResponsibilityType());

    }

    @ApiOperation("get ResponsibilityType by name")
    @GetMapping("/name")
    public ResponseEntity<Object> getResponsibilityTypeByName(@PathVariable Long countryId, @RequestParam String name) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true, responsibilityTypeService.getResponsibilityTypeByName(countryId, name));

    }


    @ApiOperation("delete ResponsibilityType  by id")
    @DeleteMapping("/delete/{id}")
    public ResponseEntity<Object> deleteResponsibilityType(@PathVariable BigInteger id) {
        if (id == null) {
            return ResponseHandler.invalidResponse(HttpStatus.BAD_REQUEST, false, "id is null");
        }
        return ResponseHandler.generateResponse(HttpStatus.OK, true, responsibilityTypeService.deleteResponsibilityType(id));

    }

    @ApiOperation("update ResponsibilityType  by id")
    @PutMapping("/update/{id}")
    public ResponseEntity<Object> updateResponsibilityType(@PathVariable BigInteger id,@Valid @RequestBody ResponsibilityType responsibilityType) {
        if (id == null) {
            return ResponseHandler.invalidResponse(HttpStatus.BAD_REQUEST, false, "id is null");
        }
        return ResponseHandler.generateResponse(HttpStatus.OK, true, responsibilityTypeService.updateResponsibilityType(id, responsibilityType));

    }


}
