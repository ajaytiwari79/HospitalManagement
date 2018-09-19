package com.kairos.controller.master_data.processing_activity_masterdata;

import com.kairos.dto.gdpr.MasterProcessingActivityRiskDTO;
import com.kairos.dto.gdpr.master_data.MasterProcessingActivityDTO;
import com.kairos.service.master_data.processing_activity_masterdata.MasterProcessingActivityService;
import com.kairos.utils.ResponseHandler;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import static com.kairos.constants.ApiConstant.API_ORGANIZATION_COUNTRY_URL;
import static com.kairos.constants.ApiConstant.UNIT_URL;

import javax.inject.Inject;
import javax.validation.Valid;
import java.math.BigInteger;

@RestController
@RequestMapping(API_ORGANIZATION_COUNTRY_URL)
@Api(API_ORGANIZATION_COUNTRY_URL)
public class MasterProcessingActivityController {

    private static final Logger LOGGER = LoggerFactory.getLogger(MasterProcessingActivityController.class);

    @Inject
    private MasterProcessingActivityService masterProcessingActivityService;


    @ApiOperation(value = "add MasterProcessingActivity asset")
    @PostMapping("/master_processing_activity/add")
    public ResponseEntity<Object> createMasterProcessingActivity(@PathVariable Long countryId, @RequestBody @Valid MasterProcessingActivityDTO processingActivityDto) {

        return ResponseHandler.generateResponse(HttpStatus.OK, true, masterProcessingActivityService.createMasterProcessingActivity(countryId, processingActivityDto));
    }


    @ApiOperation(value = "update MasterProcessingActivity")
    @PutMapping("/master_processing_activity/update/{id}")
    public ResponseEntity<Object> updateMasterProcessingActivity(@PathVariable Long countryId, @PathVariable BigInteger id, @Valid @RequestBody MasterProcessingActivityDTO processingActivityDto) {
     
        return ResponseHandler.generateResponse(HttpStatus.OK, true, masterProcessingActivityService.updateMasterProcessingActivityAndSubProcessingActivities(countryId, id, processingActivityDto));
    }

    @ApiOperation(value = "delete MasterProcessingActivity")
    @DeleteMapping("/master_processing_activity/delete/{id}")
    public ResponseEntity<Object> deleteMasterProcessingActivity(@PathVariable Long countryId, @PathVariable BigInteger id) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true, masterProcessingActivityService.deleteMasterProcessingActivity(countryId, id));
    }


    @ApiOperation(value = "delete MasterProcessingActivity")
    @DeleteMapping("/master_processing_activity/{id}/sub_processing_activity/{subProcessingActivityId}")
    public ResponseEntity<Object> deleteSubProcessingActivity(@PathVariable Long countryId, @PathVariable BigInteger id, @PathVariable BigInteger subProcessingActivityId) {
        if (id == null) {
            return ResponseHandler.invalidResponse(HttpStatus.BAD_REQUEST, false, "id cannot be null");
        }
        return ResponseHandler.generateResponse(HttpStatus.OK, true, masterProcessingActivityService.deleteSubProcessingActivity(countryId, id, subProcessingActivityId));
    }


    @ApiOperation(value = "get MasterProcessingActivity by id")
    @GetMapping("/master_processing_activity/{id}")
    public ResponseEntity<Object> getMasterProcessingActivity(@PathVariable Long countryId, @PathVariable BigInteger id) {

        return ResponseHandler.generateResponse(HttpStatus.OK, true, masterProcessingActivityService.getMasterProcessingActivityWithSubProcessing(countryId, id));
    }

    @ApiOperation(value = "get MasterProcessingActivity list with SubProcessing Activity")
    @GetMapping("/master_processing_activity/all")
    public ResponseEntity<Object> getMasterProcessingActivityListWithSubProcessingActivity(@PathVariable Long countryId) {

        return ResponseHandler.generateResponse(HttpStatus.OK, true, masterProcessingActivityService.getMasterProcessingActivityListWithSubProcessing(countryId));
    }


    @ApiOperation(value = "Link risk with Processing Activity And Sub Processing Activity")
    @PutMapping("/master_processing_activity/{processingActivityId}/risk")
    public ResponseEntity<Object> createRiskAndLinkWithProcessingActivityAndSubProcessingActivity(@PathVariable Long countryId, @PathVariable BigInteger processingActivityId, @Valid @RequestBody MasterProcessingActivityRiskDTO masterProcessingActivityRiskDTO) {

        return ResponseHandler.generateResponse(HttpStatus.OK, true, masterProcessingActivityService.createRiskAndLinkWithProcessingActivityAndSubProcessingActivity(countryId, processingActivityId, masterProcessingActivityRiskDTO));
    }


    @ApiOperation(value = "unlink risk from Processing Activity ")
    @DeleteMapping("/master_processing_activity/{processingActivityId}/risk/{riskId}")
    public ResponseEntity<Object> unlinkRiskFromProcessingActivityAndDeletedRisk(@PathVariable Long countryId, @PathVariable BigInteger processingActivityId, @PathVariable BigInteger riskId) {


        return ResponseHandler.generateResponse(HttpStatus.OK, true, masterProcessingActivityService.deleteRiskAndUnlinkFromProcessingActivityOrSubProcessingActivity(countryId, processingActivityId, riskId));
    }


    @ApiOperation(value = "unlink risk from Sub Processing Activity ")
    @DeleteMapping("/master_processing_activity/sub_Process/{subProcessingActivityId}/risk/{riskId}")
    public ResponseEntity<Object> unlinkRiskFromSubProcessingActivityAndDeleteRisk(@PathVariable Long countryId, @PathVariable BigInteger subProcessingActivityId, @PathVariable BigInteger riskId) {


        return ResponseHandler.generateResponse(HttpStatus.OK, true, masterProcessingActivityService.deleteRiskAndUnlinkFromProcessingActivityOrSubProcessingActivity(countryId, subProcessingActivityId, riskId));
    }


    @ApiOperation(value = "get All Processing Activity And linked risk ")
    @GetMapping("/master_processing_activity/risk")
    public ResponseEntity<Object> getMasterProcessingActivityAndLinkedRisks(@PathVariable Long countryId) {
        if (countryId == null) {
            return ResponseHandler.invalidResponse(HttpStatus.BAD_REQUEST, false, "country id can't be null");
        }
        return ResponseHandler.generateResponse(HttpStatus.OK, true, masterProcessingActivityService.getAllMasterProcessingActivityAndLinkedRisks(countryId));
    }


    @ApiOperation(value = "get All Sub Processing Activity of Processing Activity And  risks linked with sub processing activity ")
    @GetMapping("/master_processing_activity/{processingActivityId}/sub_Process/risk")
    public ResponseEntity<Object> getAllSubProcessingActivityWithLinkedRisks(@PathVariable Long countryId,@PathVariable(required = true) BigInteger processingActivityId) {
        if (countryId == null) {
            return ResponseHandler.invalidResponse(HttpStatus.BAD_REQUEST, false, "country id can't be null");
        }else if (processingActivityId==null)
        { return ResponseHandler.invalidResponse(HttpStatus.BAD_REQUEST, false, "Processing Activity id can't be null");
        }
        return ResponseHandler.generateResponse(HttpStatus.OK, true, masterProcessingActivityService.getAllSubProcessingActivityAndLinkedRisksByProcessingActivityId(countryId,processingActivityId));
    }


/*
    @ApiOperation(value = "get MasterProcessingActivity of unit by id")
    @GetMapping(UNIT_URL + "/master_processing_activity/{processingActivityId}")
    public ResponseEntity<Object> getMasterProcessingActivityOfUnitById(@PathVariable Long countryId, @PathVariable Long unitId, @PathVariable BigInteger processingActivityId) {
        if (processingActivityId == null) {
            return ResponseHandler.invalidResponse(HttpStatus.BAD_REQUEST, false, "Processing Activity id can't be null");
        }
        return ResponseHandler.generateResponse(HttpStatus.OK, true, masterProcessingActivityService.getMasterProcessingActivityWithSubProcessing(countryId, unitId, processingActivityId));
    }

    @ApiOperation(value = "get MasterProcessingActivity list with SubProcessing Activity of unit ")
    @GetMapping(UNIT_URL + "/master_processing_activity/all")
    public ResponseEntity<Object> getMasterProcessingActivityListWithSubProcessingActivityOfUnit(@PathVariable Long countryId, @PathVariable Long unitId) {
        if (countryId == null) {
            return ResponseHandler.invalidResponse(HttpStatus.BAD_REQUEST, false, "country id can't be null");
        } else if (unitId == null) {
            return ResponseHandler.invalidResponse(HttpStatus.BAD_REQUEST, false, "unitId can't be null");
        }
        return ResponseHandler.generateResponse(HttpStatus.OK, true, masterProcessingActivityService.getMasterProcessingActivityListWithSubProcessing(countryId, unitId));
    }*/

}
