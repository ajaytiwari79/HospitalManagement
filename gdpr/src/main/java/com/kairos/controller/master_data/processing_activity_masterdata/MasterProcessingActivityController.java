package com.kairos.controller.master_data.processing_activity_masterdata;

import com.kairos.dto.gdpr.MasterProcessingActivityRiskDTO;
import com.kairos.dto.gdpr.master_data.MasterProcessingActivityDTO;
import com.kairos.enums.gdpr.SuggestedDataStatus;
import com.kairos.service.master_data.processing_activity_masterdata.MasterProcessingActivityService;
import com.kairos.utils.ResponseHandler;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import static com.kairos.constants.ApiConstant.API_ORGANIZATION_COUNTRY_URL;

import javax.inject.Inject;
import javax.validation.Valid;
import java.math.BigInteger;
import java.util.Optional;
import java.util.Set;

@RestController
@RequestMapping(API_ORGANIZATION_COUNTRY_URL)
@Api(API_ORGANIZATION_COUNTRY_URL)
class MasterProcessingActivityController {

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
    public ResponseEntity<Object> updateMasterProcessingActivity(@PathVariable Long countryId, @PathVariable Long id, @Valid @RequestBody MasterProcessingActivityDTO processingActivityDto) {

        return ResponseHandler.generateResponse(HttpStatus.OK, true, masterProcessingActivityService.updateMasterProcessingActivityAndSubProcessingActivities(countryId, id, processingActivityDto));
    }

    @ApiOperation(value = "delete MasterProcessingActivity")
    @DeleteMapping("/master_processing_activity/delete/{id}")
    public ResponseEntity<Object> deleteMasterProcessingActivity(@PathVariable Long countryId, @PathVariable Long id) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true, masterProcessingActivityService.deleteMasterProcessingActivity(countryId, id));
    }


    @ApiOperation(value = "delete MasterProcessingActivity")
    @DeleteMapping("/master_processing_activity/{id}/sub_processing_activity/{subProcessingActivityId}")
    public ResponseEntity<Object> deleteSubProcessingActivity(@PathVariable Long countryId, @PathVariable Long id, @PathVariable Long subProcessingActivityId) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true, masterProcessingActivityService.deleteSubProcessingActivity(countryId, id, subProcessingActivityId));
    }


    @ApiOperation(value = "get MasterProcessingActivity by id")
    @GetMapping("/master_processing_activity/{id}")
    public ResponseEntity<Object> getMasterProcessingActivity(@PathVariable Long countryId, @PathVariable Long id) {
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


    @ApiOperation(value = "get All Processing Activity And linked risk and Sub Processing Activity")
    @GetMapping("/master_processing_activity/risk")
    public ResponseEntity<Object> getMasterProcessingActivityWithSubProcessingActivitiesAndLinkedRisks(@PathVariable Long countryId) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true, masterProcessingActivityService.getAllMasterProcessingActivityWithSubProcessingActivitiesAndRisks(countryId));
    }


    @ApiOperation(value = "Update Sugessted status of Processing Activity")
    @PutMapping("/master_processing_activity/{processingActivityId}/status")
    public ResponseEntity<Object> updateSuggestedStatusOfProcessingActivity(@PathVariable Long countryId, @PathVariable BigInteger processingActivityId, @RequestParam SuggestedDataStatus suggestedDataStatus) {
        if (!Optional.ofNullable(suggestedDataStatus).isPresent()) {
            return ResponseHandler.invalidResponse(HttpStatus.BAD_REQUEST, false, "Suggested Status in Empty");
        }
        return ResponseHandler.generateResponse(HttpStatus.OK, true, masterProcessingActivityService.updateSuggestedStatusOfMasterProcessingActivity(countryId, processingActivityId, suggestedDataStatus));
    }

    @ApiOperation(value = "Update Sugessted status of Processing Activity")
    @PutMapping("/master_processing_activity/{processingActivityId}/subProcess/status")
    public ResponseEntity<Object> updateSuggestedStatusOfSubProcessingActivity(@PathVariable Long countryId, @PathVariable BigInteger processingActivityId, @RequestBody Set<BigInteger> subProcessingActivityIds, @RequestParam SuggestedDataStatus suggestedDataStatus) {
        if (CollectionUtils.isEmpty(subProcessingActivityIds)) {
            return ResponseHandler.invalidResponse(HttpStatus.BAD_REQUEST, false, "Sub Processing Activity is Not Selected");
        } else if (!Optional.ofNullable(suggestedDataStatus).isPresent()) {
            return ResponseHandler.invalidResponse(HttpStatus.BAD_REQUEST, false, "Suggested Status in Empty");
        }
        return ResponseHandler.generateResponse(HttpStatus.OK, true, masterProcessingActivityService.updateSuggestedStatusOfSubProcessingActivities(countryId, processingActivityId, subProcessingActivityIds, suggestedDataStatus));
    }

}
