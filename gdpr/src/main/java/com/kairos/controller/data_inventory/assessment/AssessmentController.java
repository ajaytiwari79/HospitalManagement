package com.kairos.controller.data_inventory.assessment;


import com.kairos.enums.AssessmentStatus;
import com.kairos.dto.gdpr.data_inventory.AssessmentDTO;
import com.kairos.persistence.model.data_inventory.assessment.AssessmentAnswerValueObject;
import com.kairos.service.data_inventory.assessment.AssessmentService;
import com.kairos.utils.ResponseHandler;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.inject.Inject;
import javax.validation.Valid;

import java.math.BigInteger;
import java.util.Optional;

import static com.kairos.constants.ApiConstant.API_ORGANIZATION_UNIT_URL;
import static com.kairos.constants.ApiConstant.COUNTRY_URL;


@RestController
@RequestMapping(API_ORGANIZATION_UNIT_URL)
@Api(API_ORGANIZATION_UNIT_URL)
public class AssessmentController {


    @Inject
    private AssessmentService assessmentService;


    @ApiOperation(value = "Add assessment to Asset")
    @PostMapping(COUNTRY_URL + "/assessment/asset/{assetId}")
    public ResponseEntity<Object> addAssessmentToAsset(@PathVariable Long unitId, @PathVariable Long countryId, @PathVariable BigInteger assetId, @RequestBody @Valid AssessmentDTO assessmentDTO) {
        if (unitId == null) {
            return ResponseHandler.invalidResponse(HttpStatus.BAD_REQUEST, false, "Organization id can't be null");
        }
        return ResponseHandler.generateResponse(HttpStatus.OK, true, assessmentService.saveAssessmentForAsset(unitId, countryId, assetId, assessmentDTO));

    }


    @ApiOperation(value = "Add assessment to Processing Activity")
    @PostMapping(COUNTRY_URL + "/assessment/processing_activity/{processingActivityId}")
    public ResponseEntity<Object> addAssessmentToProcessingActivity(@PathVariable Long unitId, @PathVariable Long countryId, @PathVariable BigInteger processingActivityId, @RequestBody @Valid AssessmentDTO assessmentDTO) {
        if (unitId == null) {
            return ResponseHandler.invalidResponse(HttpStatus.BAD_REQUEST, false, "Organization id can't be null");
        }
        return ResponseHandler.generateResponse(HttpStatus.OK, true, assessmentService.saveAssessmentForProcessingActivity(unitId, countryId, processingActivityId, assessmentDTO));

    }


    @ApiOperation(value = "get Assessment  By Id")
    @GetMapping(COUNTRY_URL + "/assessment/{assessmentId}")
    public ResponseEntity<Object> getAssetAssessmentById(@PathVariable Long countryId, @PathVariable Long unitId, @PathVariable BigInteger assessmentId) {

        if (unitId == null) {
            return ResponseHandler.invalidResponse(HttpStatus.BAD_REQUEST, false, "Organization id can't be Null");
        }
        return ResponseHandler.generateResponse(HttpStatus.OK, true, assessmentService.getAssessmentById(countryId, unitId, assessmentId));
    }


    @ApiOperation(value = "get All launched Assessment Assign to respondent and are in New and InProgress state")
    @GetMapping("/assessment")
    public ResponseEntity<Object> getAllLaunchedAssessment(@PathVariable Long unitId) {

        if (unitId == null) {
            return ResponseHandler.invalidResponse(HttpStatus.BAD_REQUEST, false, "Organization id can't be Null");
        }
        return ResponseHandler.generateResponse(HttpStatus.OK, true, assessmentService.getAllLaunchedAssessmentOfAssignee(unitId));
    }

    @ApiOperation(value = "Update Answer of assessment question In progress state by  Assignee")
    @PutMapping("/assessment/{assessmentId}")
    public ResponseEntity<Object> saveAssessmentAnswerForAssetOrProcessingActivity(@PathVariable Long unitId, @PathVariable BigInteger assessmentId, @Valid @RequestBody AssessmentAnswerValueObject assessmentAnswerValueObject) {
        if (unitId == null) {
            return ResponseHandler.invalidResponse(HttpStatus.BAD_REQUEST, false, "Organization id can't be Null");
        }
        return ResponseHandler.generateResponse(HttpStatus.OK, true, assessmentService.addAssessmentAnswerForAssetOrProcessingActivityToAssessment(unitId, assessmentId, assessmentAnswerValueObject));
    }


    @ApiOperation(value = "Change Assessment status")
    @PutMapping("/assessment/{assessmentId}/status")
    public ResponseEntity<Object> changeAssessmentStatusKanbanView(@PathVariable Long unitId, @PathVariable BigInteger assessmentId, @RequestParam(value = "assessmentStatus",required = true) AssessmentStatus assessmentStatus) {
        if (!Optional.ofNullable(assessmentStatus).isPresent())
        {
            return ResponseHandler.invalidResponse(HttpStatus.BAD_REQUEST, false, "Assessment Status "+assessmentStatus+" is invalid");
        }
        if (unitId == null) {
            return ResponseHandler.invalidResponse(HttpStatus.BAD_REQUEST, false, "Organization id can't be Null");
        }
        return ResponseHandler.generateResponse(HttpStatus.OK, true, assessmentService.updateAssessmentStatus(unitId, assessmentId,assessmentStatus));
    }



}
