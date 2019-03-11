package com.kairos.controller.data_inventory.assessment;


import com.kairos.dto.gdpr.assessment.AssessmentAnswerDTO;
import com.kairos.dto.response.ResponseDTO;
import com.kairos.enums.gdpr.AssessmentSchedulingFrequency;
import com.kairos.enums.gdpr.AssessmentStatus;
import com.kairos.dto.gdpr.assessment.AssessmentDTO;
import com.kairos.persistence.model.data_inventory.assessment.AssessmentAnswer;
import com.kairos.response.dto.common.AssessmentResponseDTO;
import com.kairos.response.dto.master_data.questionnaire_template.QuestionnaireSectionResponseDTO;
import com.kairos.service.data_inventory.assessment.AssessmentService;
import com.kairos.utils.ResponseHandler;
import com.kairos.utils.ValidateRequestBodyList;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.inject.Inject;
import javax.validation.Valid;

import java.util.List;
import java.util.Optional;

import static com.kairos.constants.ApiConstant.API_ORGANIZATION_UNIT_URL;


@RestController
@RequestMapping(API_ORGANIZATION_UNIT_URL)
@Api(API_ORGANIZATION_UNIT_URL)
class AssessmentController {


    @Inject
    private AssessmentService assessmentService;


    @ApiOperation(value = "launch assessment for Asset")
    @PostMapping( "/assessment/asset/{assetId}")
    public ResponseEntity<ResponseDTO<AssessmentDTO>> launchAssessmentForAsset(@PathVariable Long unitId, @PathVariable Long assetId, @RequestBody @Valid AssessmentDTO assessmentDTO) {
        return ResponseHandler.generateResponseDTO(HttpStatus.OK, true, assessmentService.launchAssessmentForAsset(unitId,  assetId, assessmentDTO));

    }


    @ApiOperation(value = "launch assessment for processing activity")
    @PostMapping( "/assessment/processing_activity/{processingActivityId}")
    public ResponseEntity<ResponseDTO<AssessmentDTO>> launchAssessmentForProcessingActivity(@PathVariable Long unitId, @PathVariable Long processingActivityId, @RequestBody @Valid AssessmentDTO assessmentDTO,@RequestParam boolean subProcessingActivity) {
        return ResponseHandler.generateResponseDTO(HttpStatus.OK, true, assessmentService.launchAssessmentForProcessingActivity(unitId,  processingActivityId, assessmentDTO,subProcessingActivity));

    }

    @ApiOperation(value = "get Assessment  By Id")
    @GetMapping( "/assessment/{assessmentId}")
    public ResponseEntity<ResponseDTO<List<QuestionnaireSectionResponseDTO>>> getAssetAssessmentById(@PathVariable Long unitId, @PathVariable Long assessmentId) {
        return ResponseHandler.generateResponseDTO(HttpStatus.OK, true, assessmentService.getAssessmentByUnitIdAndId( unitId, assessmentId));
    }

    @ApiOperation(value = "get All launched Assessment Assign Staff Member")
    @GetMapping("/assessment/staff")
    public ResponseEntity<Object> getAllLaunchedAssessmentAssignToStaff(@PathVariable Long unitId) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true, assessmentService.getAllLaunchedAssessmentOfCurrentLoginUser(unitId));
    }

    @ApiOperation(value = "get All Assessment of unit")
    @GetMapping("/assessment")
    public ResponseEntity<ResponseDTO<List<AssessmentResponseDTO>>> getAllAssessmentByUnitId(@PathVariable Long unitId) {
        return ResponseHandler.generateResponseDTO(HttpStatus.OK, true, assessmentService.getAllAssessmentByUnitId(unitId));
    }

    @ApiOperation(value = "delete Assessment by id")
    @DeleteMapping("/assessment/{assessmentId}")
    public ResponseEntity<ResponseDTO<Boolean>> deleteAssessment(@PathVariable Long unitId,@PathVariable Long assessmentId) {
        return ResponseHandler.generateResponseDTO(HttpStatus.OK, true, assessmentService.deleteAssessmentById(unitId,assessmentId));
    }


    @ApiOperation(value = "save answer of assessment question In progress state by  Assignee")
    @PutMapping("/assessment/{assessmentId}")
    public ResponseEntity<Object> saveAssessmentAnswerForAssetOrProcessingActivity(@PathVariable Long unitId, @PathVariable Long assessmentId, @Valid @RequestBody ValidateRequestBodyList<AssessmentAnswerDTO> assessmentAnswerValueObjects , @RequestParam(required = true) AssessmentStatus status) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true, assessmentService.saveAssessmentAnswerByUnitIdAndAssessmentId(unitId, assessmentId, assessmentAnswerValueObjects.getRequestBody(),status));
    }

    @ApiOperation(value = "get assessment scheduling frequency enum")
    @GetMapping("/assessment/scheduling")
    public ResponseEntity<ResponseDTO< AssessmentSchedulingFrequency[]>> getSchedulingFrequencyEnumList() {
        return ResponseHandler.generateResponseDTO(HttpStatus.OK, true, assessmentService.getSchedulingFrequency());
    }


    @ApiOperation(value = "Change Assessment status")
    @PutMapping("/assessment/{assessmentId}/status")
    public ResponseEntity<Object> changeAssessmentStatusKanbanView(@PathVariable Long unitId, @PathVariable Long assessmentId, @RequestParam(value = "assessmentStatus",required = true) AssessmentStatus assessmentStatus) {
        if (!Optional.ofNullable(assessmentStatus).isPresent())
        {
            return ResponseHandler.invalidResponse(HttpStatus.BAD_REQUEST, false, "please enter valid param");
        }
        return ResponseHandler.generateResponse(HttpStatus.OK, true, assessmentService.updateAssessmentStatus(unitId, assessmentId,assessmentStatus));
    }


}
