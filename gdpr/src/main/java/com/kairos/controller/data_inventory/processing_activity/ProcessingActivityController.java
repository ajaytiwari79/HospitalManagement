package com.kairos.controller.data_inventory.processing_activity;


import com.kairos.gdpr.data_inventory.ProcessingActivityDTO;
import com.kairos.service.data_inventory.processing_activity.ProcessingActivityService;
import com.kairos.utils.ResponseHandler;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.inject.Inject;
import javax.validation.Valid;

import java.math.BigInteger;

import static com.kairos.constants.ApiConstant.API_ORGANIZATION_URL_UNIT_URL;


@RestController
@RequestMapping(API_ORGANIZATION_URL_UNIT_URL)
@Api(API_ORGANIZATION_URL_UNIT_URL)
public class ProcessingActivityController {


    @Inject
    private ProcessingActivityService processingActivityService;


    @ApiOperation(value = "create Processing activity ")
    @PostMapping("/processing_activity/add")
    public ResponseEntity<Object> createProcessingActivity(@PathVariable Long unitId, @Valid @RequestBody ProcessingActivityDTO processingActivityDTO) {
        if (unitId == null) {
            return ResponseHandler.invalidResponse(HttpStatus.BAD_REQUEST, false, "ManagingOrganization id can't be Null");
        }
        return ResponseHandler.generateResponse(HttpStatus.OK, true, processingActivityService.createProcessingActivity(unitId, processingActivityDTO));
    }


    @ApiOperation(value = "delete  asset by Id")
    @DeleteMapping("/processing_activity/delete/{id}")
    public ResponseEntity<Object> deleteAssetById(@PathVariable Long unitId, @PathVariable BigInteger id) {

        if (unitId == null) {
            return ResponseHandler.invalidResponse(HttpStatus.BAD_REQUEST, false, "organization  id can't be Null");
        }
        return ResponseHandler.generateResponse(HttpStatus.OK, true, processingActivityService.deleteProcessingActivity(unitId, id));
    }


    @ApiOperation(value = "Get Processing Activity with meta data by Id")
    @GetMapping("/processing_activity/{id}")
    public ResponseEntity<Object> getProcessingActivityWithMetaDataById(@PathVariable Long unitId, @PathVariable BigInteger id) {

        if (unitId == null) {
            return ResponseHandler.invalidResponse(HttpStatus.BAD_REQUEST, false, "organization id can't be Null");
        }
        return ResponseHandler.generateResponse(HttpStatus.OK, true, processingActivityService.getProcessingActivityWithMetaDataById(unitId, id));
    }


    @ApiOperation(value = "Get All Processing activity With meta data ")
    @GetMapping("/processing_activity/all")
    public ResponseEntity<Object> getAllProcessingActivityWithMetaData(@PathVariable Long unitId) {

        if (unitId == null) {
            return ResponseHandler.invalidResponse(HttpStatus.BAD_REQUEST, false, "Organization id can't be Null");
        }
        return ResponseHandler.generateResponse(HttpStatus.OK, true, processingActivityService.getAllProcessingActivityWithMetaData(unitId));
    }


    @ApiOperation(value = "update Processing activity  detail")
    @PutMapping("/processing_activity/update/{id}")
    public ResponseEntity<Object> updateProcessingActivityDetail(@PathVariable Long unitId, @PathVariable BigInteger id, @Valid @RequestBody ProcessingActivityDTO processingActivityDTO) {

        if (unitId == null) {
            return ResponseHandler.invalidResponse(HttpStatus.BAD_REQUEST, false, "organization id can't be Null");
        }
        return ResponseHandler.generateResponse(HttpStatus.OK, true, processingActivityService.updateProcessingActivity(unitId, id, processingActivityDTO));
    }


}
