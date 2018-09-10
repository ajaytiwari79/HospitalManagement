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

import static com.kairos.constants.ApiConstant.API_ORGANIZATION_URL;
import static com.kairos.constants.ApiConstant.UNIT_URL;

import javax.inject.Inject;
import javax.validation.Valid;
import java.math.BigInteger;

@RestController
@RequestMapping(API_ORGANIZATION_URL)
@Api(API_ORGANIZATION_URL)
public class MasterProcessingActivityController {

    private static final Logger LOGGER = LoggerFactory.getLogger(MasterProcessingActivityController.class);

    @Inject
    private MasterProcessingActivityService masterProcessingActivityService;


    @ApiOperation(value = "add MasterProcessingActivity asset")
    @PostMapping("/master_processing_activity/add")
    public ResponseEntity<Object> createMasterProcessingActivity(@PathVariable Long countryId, @PathVariable Long organizationId, @RequestBody @Valid MasterProcessingActivityDTO processingActivityDto) {
        if (countryId == null) {
            return ResponseHandler.invalidResponse(HttpStatus.BAD_REQUEST, false, "country id can't be null");
        } else if (organizationId == null) {
            return ResponseHandler.invalidResponse(HttpStatus.BAD_REQUEST, false, "organization id can't be null");
        }
        return ResponseHandler.generateResponse(HttpStatus.OK, true, masterProcessingActivityService.createMasterProcessingActivity(countryId, organizationId, processingActivityDto));
    }


    @ApiOperation(value = "update MasterProcessingActivity")
    @PutMapping("/master_processing_activity/update/{id}")
    public ResponseEntity<Object> updateMasterProcessingActivity(@PathVariable Long countryId, @PathVariable Long organizationId, @PathVariable BigInteger id, @Valid @RequestBody MasterProcessingActivityDTO processingActivityDto) {
        if (id == null) {
            return ResponseHandler.invalidResponse(HttpStatus.BAD_REQUEST, false, "id cannot be null");
        }
        if (countryId == null) {
            return ResponseHandler.invalidResponse(HttpStatus.BAD_REQUEST, false, "country id can't be null");
        }
        if (organizationId == null) {
            return ResponseHandler.invalidResponse(HttpStatus.BAD_REQUEST, false, "organization id can't be null");
        }
        return ResponseHandler.generateResponse(HttpStatus.OK, true, masterProcessingActivityService.updateMasterProcessingActivityAndSubProcessingActivities(countryId, organizationId, id, processingActivityDto));
    }

    @ApiOperation(value = "delete MasterProcessingActivity")
    @DeleteMapping("/master_processing_activity/delete/{id}")
    public ResponseEntity<Object> deleteMasterProcessingActivity(@PathVariable Long countryId, @PathVariable Long organizationId, @PathVariable BigInteger id) {
        if (id == null) {
            return ResponseHandler.invalidResponse(HttpStatus.BAD_REQUEST, false, "id cannot be null");
        }
        if (countryId == null) {
            return ResponseHandler.invalidResponse(HttpStatus.BAD_REQUEST, false, "country id can't be null");
        }
        if (organizationId == null) {
            return ResponseHandler.invalidResponse(HttpStatus.BAD_REQUEST, false, "organization id can't be null");
        }
        return ResponseHandler.generateResponse(HttpStatus.OK, true, masterProcessingActivityService.deleteMasterProcessingActivity(countryId, organizationId, id));
    }


    @ApiOperation(value = "delete MasterProcessingActivity")
    @DeleteMapping("/master_processing_activity/{id}/sub_processing_activity/{subProcessingActivityId}")
    public ResponseEntity<Object> deleteSubProcessingActivity(@PathVariable Long countryId, @PathVariable Long organizationId, @PathVariable BigInteger id, @PathVariable BigInteger subProcessingActivityId) {
        if (id == null) {
            return ResponseHandler.invalidResponse(HttpStatus.BAD_REQUEST, false, "id cannot be null");
        }
        return ResponseHandler.generateResponse(HttpStatus.OK, true, masterProcessingActivityService.deleteSubProcessingActivity(countryId, organizationId, id, subProcessingActivityId));
    }


    @ApiOperation(value = "get MasterProcessingActivity by id")
    @GetMapping("/master_processing_activity/{id}")
    public ResponseEntity<Object> getMasterProcessingActivity(@PathVariable Long countryId, @PathVariable Long organizationId, @PathVariable BigInteger id) {
        if (id == null) {
            return ResponseHandler.invalidResponse(HttpStatus.BAD_REQUEST, false, "id cannot be null");
        }
        if (countryId == null) {
            return ResponseHandler.invalidResponse(HttpStatus.BAD_REQUEST, false, "country id can't be null");
        }
        if (organizationId == null) {
            return ResponseHandler.invalidResponse(HttpStatus.BAD_REQUEST, false, "organization id can't be null");
        }
        return ResponseHandler.generateResponse(HttpStatus.OK, true, masterProcessingActivityService.getMasterProcessingActivityWithSubProcessing(countryId, organizationId, id));
    }

    @ApiOperation(value = "get MasterProcessingActivity list with SubProcessing Activity")
    @GetMapping("/master_processing_activity/all")
    public ResponseEntity<Object> getMasterProcessingActivityListWithSubProcessingActivity(@PathVariable Long countryId, @PathVariable Long organizationId) {
        if (countryId == null) {
            return ResponseHandler.invalidResponse(HttpStatus.BAD_REQUEST, false, "country id can't be null");
        } else if (organizationId == null) {
            return ResponseHandler.invalidResponse(HttpStatus.BAD_REQUEST, false, "organization id can't be null");
        }
        return ResponseHandler.generateResponse(HttpStatus.OK, true, masterProcessingActivityService.getMasterProcessingActivityListWithSubProcessing(countryId, organizationId));
    }


    @ApiOperation(value = "Linke risk with Processing Activity And Sub Processing Activity")
    @PutMapping("/master_processing_activity/{processingActivityId}/risk")
    public ResponseEntity<Object> createRiskAndLinkWithProcessingActivityAndSubProcessingActivity(@PathVariable Long countryId, @PathVariable Long organizationId, @PathVariable BigInteger processingActivityId, @Valid @RequestBody MasterProcessingActivityRiskDTO masterProcessingActivityRiskDTO) {

        if (countryId == null) {
            return ResponseHandler.invalidResponse(HttpStatus.BAD_REQUEST, false, "country id can't be null");
        }
       else if (organizationId == null) {
            return ResponseHandler.invalidResponse(HttpStatus.BAD_REQUEST, false, "organization id can't be null");
        }
        return ResponseHandler.generateResponse(HttpStatus.OK, true, masterProcessingActivityService.createRiskAndLinkWithProcessingActivityAndSubProcessingActivity(countryId, organizationId, processingActivityId, masterProcessingActivityRiskDTO));
    }


    @ApiOperation(value = "unlink risk from Processing Activity ")
    @DeleteMapping("/master_processing_activity/{processingActivityId}/risk/{riskId}")
    public ResponseEntity<Object> unlinkRiskFromProcessingActivityAndDeletedRisk(@PathVariable Long countryId, @PathVariable Long organizationId, @PathVariable BigInteger processingActivityId,@PathVariable BigInteger riskId) {

        if (countryId == null) {
            return ResponseHandler.invalidResponse(HttpStatus.BAD_REQUEST, false, "country id can't be null");
        }
        if (organizationId == null) {
            return ResponseHandler.invalidResponse(HttpStatus.BAD_REQUEST, false, "organization id can't be null");
        }
        return ResponseHandler.generateResponse(HttpStatus.OK, true, masterProcessingActivityService.deleteRiskAndUnlinkFromProcessingActivityOrSubProcessingActivity(countryId, organizationId, processingActivityId,riskId));
    }


    @ApiOperation(value = "unlink risk from Sub Processing Activity ")
    @DeleteMapping("/master_processing_activity/sub_Process/{subProcessingActivityId}/risk/{riskId}")
    public ResponseEntity<Object> unlinkRiskFromSubProcessingActivityAndDeleteRisk(@PathVariable Long countryId, @PathVariable Long organizationId, @PathVariable BigInteger subProcessingActivityId,@PathVariable BigInteger riskId) {

        if (countryId == null) {
            return ResponseHandler.invalidResponse(HttpStatus.BAD_REQUEST, false, "country id can't be null");
        }
        if (organizationId == null) {
            return ResponseHandler.invalidResponse(HttpStatus.BAD_REQUEST, false, "organization id can't be null");
        }
        return ResponseHandler.generateResponse(HttpStatus.OK, true, masterProcessingActivityService.deleteRiskAndUnlinkFromProcessingActivityOrSubProcessingActivity(countryId, organizationId, subProcessingActivityId,riskId));
    }


    @ApiOperation(value = "get MasterProcessingActivity of unit by id")
    @GetMapping(UNIT_URL + "/master_processing_activity/{id}")
    public ResponseEntity<Object> getMasterProcessingActivityOfUnitById(@PathVariable Long countryId, @PathVariable Long unitId, @PathVariable BigInteger id) {
        if (id == null) {
            return ResponseHandler.invalidResponse(HttpStatus.BAD_REQUEST, false, "id cannot be null");
        }
        if (countryId == null) {
            return ResponseHandler.invalidResponse(HttpStatus.BAD_REQUEST, false, "country id can't be null");
        }
        if (unitId == null) {
            return ResponseHandler.invalidResponse(HttpStatus.BAD_REQUEST, false, "unitId  can't be null");
        }
        return ResponseHandler.generateResponse(HttpStatus.OK, true, masterProcessingActivityService.getMasterProcessingActivityWithSubProcessing(countryId, unitId, id));
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
    }

}
