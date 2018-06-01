package com.kairos.controller.master_data_management.processing_activity_masterdata;


import com.kairos.dto.MasterProcessingActivityDto;
import com.kairos.service.master_data_management.processing_activity_masterdata.MasterProcessingActivityService;
import com.kairos.utils.ResponseHandler;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import static com.kairos.constant.ApiConstant.API_MASTER_PROCESSING_ACTIVITY;

import javax.inject.Inject;
import javax.validation.Valid;
import java.math.BigInteger;

@RestController
@RequestMapping(API_MASTER_PROCESSING_ACTIVITY)
@Api(API_MASTER_PROCESSING_ACTIVITY)
public class MasterProcessingActivityController {

    private static final Logger LOGGER = LoggerFactory.getLogger(MasterProcessingActivityController.class);

    @Inject
    private MasterProcessingActivityService masterProcessingActivityService;


    @ApiOperation(value = "add MasterProcessingActivity asset")
    @RequestMapping(value = "/add", method = RequestMethod.POST)
    public ResponseEntity<Object> createMasterProcessingActivity(@PathVariable Long countryId, @RequestBody @Valid MasterProcessingActivityDto processingActivityDto) {
        if (countryId == null) {
            return ResponseHandler.invalidResponse(HttpStatus.BAD_REQUEST, false, "country id is null");
        }
        return ResponseHandler.generateResponse(HttpStatus.OK, true, masterProcessingActivityService.createMasterProcessingActivity(countryId, processingActivityDto));
    }
/*
    @ApiOperation(value = "get all MasterProcessingActivity")
    @RequestMapping(value = "/all", method = RequestMethod.GET)
    public ResponseEntity<Object> getAllAsset() {
        return ResponseHandler.generateResponse(HttpStatus.OK, true, masterProcessingActivityService.getAllmasterProcessingActivity());
    }*/

    @ApiOperation(value = "update MasterProcessingActivity")
    @PutMapping("/update/{id}")
    public ResponseEntity<Object> updateMasterProcessingActivity(@PathVariable Long countryId, @PathVariable BigInteger id, @Valid @RequestBody  MasterProcessingActivityDto processingActivityDto) {
        if (id == null) {
            return ResponseHandler.invalidResponse(HttpStatus.BAD_REQUEST, false, "id is null");
        }
        return ResponseHandler.generateResponse(HttpStatus.OK, true, masterProcessingActivityService.updateMasterProcessingActivity(countryId, id, processingActivityDto));
    }

    @ApiOperation(value = "delete MasterProcessingActivity")
    @DeleteMapping("/delete/{id}")
    public ResponseEntity<Object> deleteMasterProcessingActivity(@PathVariable BigInteger id) {
        if (id == null) {
            return ResponseHandler.invalidResponse(HttpStatus.BAD_REQUEST, false, "id is null");
        }
        return ResponseHandler.generateResponse(HttpStatus.OK, true, masterProcessingActivityService.deleteMasterProcessingActivity(id));
    }

    @ApiOperation(value = "get MasterProcessingActivity by id")
    @GetMapping("/{id}")
    public ResponseEntity<Object> getMasterProcessingActivity(@PathVariable Long countryId, @PathVariable BigInteger id) {
        if (id == null) {
            return ResponseHandler.invalidResponse(HttpStatus.BAD_REQUEST, false, "id is null");
        } else if (countryId == null) {
            return ResponseHandler.invalidResponse(HttpStatus.BAD_REQUEST, false, "country id is null");

        } else
            return ResponseHandler.generateResponse(HttpStatus.OK, true, masterProcessingActivityService.getMasterProcessingActivityWithSubProcessing(countryId, id));
    }

    @ApiOperation(value = "get MasterProcessingActivity list with Subprocessing Activity")
    @GetMapping("/all")
    public ResponseEntity<Object> getMasterProcessingActivityListWithSubProcessingActivity(@PathVariable Long countryId) {
        if (countryId == null) {
            return ResponseHandler.invalidResponse(HttpStatus.BAD_REQUEST, false, "country id is null");
        } else

            return ResponseHandler.generateResponse(HttpStatus.OK, true, masterProcessingActivityService.getMasterProcessingActivityListWithSubProcessing(countryId));
    }
}
