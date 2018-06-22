package com.kairos.controller.master_data_management.asset_management;


import com.kairos.persistance.model.master_data_management.asset_management.HostingType;
import com.kairos.service.master_data_management.asset_management.HostingTypeService;
import com.kairos.utils.ResponseHandler;
import com.kairos.utils.validate_list.ValidateListOfRequestBody;
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

import static com.kairos.constants.ApiConstant.API_HOSTING_TYPE_URL;
/*
 *
 *  created by bobby 17/5/2018
 * */



@RestController
@RequestMapping(API_HOSTING_TYPE_URL)
@Api(API_HOSTING_TYPE_URL)
public class HostingTypeController {

    private static final Logger LOGGER = LoggerFactory.getLogger(HostingTypeController.class);

    @Inject
    private HostingTypeService hostingTypeService;


    @ApiOperation("add HostingType")
    @PostMapping("/add")
    public ResponseEntity<Object> createHostingType(@PathVariable Long countryId, @Valid @RequestBody ValidateListOfRequestBody<HostingType> hostingTypes) {
        if (countryId == null) {
            return ResponseHandler.invalidResponse(HttpStatus.BAD_REQUEST, false, "id is null");
        }
        return ResponseHandler.generateResponse(HttpStatus.OK, true, hostingTypeService.createHostingType(countryId,hostingTypes.getRequestBody()));

    }


    @ApiOperation("get HostingType by id")
    @GetMapping("/{id}")
    public ResponseEntity<Object> getHostingType(@PathVariable Long countryId,@PathVariable BigInteger id) {
        if (id == null) {
            return ResponseHandler.invalidResponse(HttpStatus.BAD_REQUEST, false, "id is null");
        } else if (countryId == null) {
            return ResponseHandler.invalidResponse(HttpStatus.BAD_REQUEST, false, "country id is null");

        } else   return ResponseHandler.generateResponse(HttpStatus.OK, true, hostingTypeService.getHostingType(countryId,id));

    }


    @ApiOperation("get all HostingType ")
    @GetMapping("/all")
    public ResponseEntity<Object> getAllHostingType() {
        return ResponseHandler.generateResponse(HttpStatus.OK, true, hostingTypeService.getAllHostingType());

    }


    @ApiOperation("get HostingType by name")
    @GetMapping("/name")
    public ResponseEntity<Object> getHostingTypeByName(@PathVariable Long countryId,@RequestParam String name) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true, hostingTypeService.getHostingTypeByName(countryId,name));

    }


    @ApiOperation("delete HostingType  by id")
    @DeleteMapping("/delete/{id}")
    public ResponseEntity<Object> deleteHostingType(@PathVariable BigInteger id) {
        if (id == null) {
            return ResponseHandler.invalidResponse(HttpStatus.BAD_REQUEST, false, "id is null");
        }

        return ResponseHandler.generateResponse(HttpStatus.OK, true, hostingTypeService.deleteHostingType(id));

    }

    @ApiOperation("update HostingType by id")
    @PutMapping("/update/{id}")
    public ResponseEntity<Object> updateHostingType(@PathVariable BigInteger id, @Valid  @RequestBody HostingType hostingtype) {
        if (id == null) {
            return ResponseHandler.invalidResponse(HttpStatus.BAD_REQUEST, false, "id is null");
        }

        return ResponseHandler.generateResponse(HttpStatus.OK, true, hostingTypeService.updateHostingType(id, hostingtype));

    }


}
