package com.kairos.controller.data_inventory.processing_activity;


import com.kairos.dto.gdpr.data_inventory.ProcessingActivityDTO;
import com.kairos.service.data_inventory.assessment.AssessmentService;
import com.kairos.service.data_inventory.processing_activity.ProcessingActivityService;
import com.kairos.utils.ResponseHandler;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.inject.Inject;
import javax.validation.Valid;

import static com.kairos.constants.ApiConstant.API_ORGANIZATION_UNIT_URL;
import static com.kairos.constants.ApiConstant.COUNTRY_URL;


@RestController
@RequestMapping(API_ORGANIZATION_UNIT_URL)
@Api(API_ORGANIZATION_UNIT_URL)
class ProcessingActivityController {


    @Inject
    private ProcessingActivityService processingActivityService;

    @Inject
    private AssessmentService assessmentService;


    @ApiOperation(value = "create Processing activity ")
    @PostMapping("/processing_activity")
    public ResponseEntity<Object> createProcessingActivity(@PathVariable Long unitId, @Valid @RequestBody ProcessingActivityDTO processingActivityDTO) {
        processingActivityDTO.setSuggested(false);
        return ResponseHandler.generateResponse(HttpStatus.OK, true, processingActivityService.createProcessingActivity(unitId, processingActivityDTO));
    }


    @ApiOperation(value = "delete  Processing Activity by Id")
    @DeleteMapping("/processing_activity/delete/{processingActivityId}")
    public ResponseEntity<Object> deleteProcessingActivityById(@PathVariable Long unitId, @PathVariable Long processingActivityId) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true, processingActivityService.deleteProcessingActivity(unitId, processingActivityId));
    }

    @ApiOperation(value = "delete  Sub Processing Activity ")
    @DeleteMapping("/processing_activity/{processingActivityId}/subProcess/{subProcessId}")
    public ResponseEntity<Object> deleteSubProcessingActivityById(@PathVariable Long unitId, @PathVariable Long processingActivityId, @PathVariable Long subProcessingActivityId) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true, processingActivityService.deleteSubProcessingActivity(unitId, processingActivityId, subProcessingActivityId));
    }

    @ApiOperation(value = "Get All Processing activity With meta data ")
    @GetMapping("/processing_activity")
    public ResponseEntity<Object> getAllProcessingActivityWithMetaData(@PathVariable Long unitId) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true, processingActivityService.getAllProcessingActivityWithMetaData(unitId));
    }


    @ApiOperation(value = "update Processing activity  detail")
    @PutMapping("/processing_activity/update/{id}")
    public ResponseEntity<Object> updateProcessingActivityDetail(@PathVariable Long unitId, @PathVariable Long id, @Valid @RequestBody ProcessingActivityDTO processingActivityDTO) {

        return ResponseHandler.generateResponse(HttpStatus.OK, true, processingActivityService.updateProcessingActivity(unitId, id, processingActivityDTO));
    }

    @ApiOperation(value = "get history of asset or changes done in Asset")
    @GetMapping("/processing_activity/{processingActivityId}/history")
    public ResponseEntity<Object> getHistoryOrDataAuditOfAsset(@PathVariable Long processingActivityId) throws ClassNotFoundException{

        return ResponseHandler.generateResponse(HttpStatus.OK, true, processingActivityService.getProcessingActivityActivitiesHistory(processingActivityId));
    }

    @ApiOperation(value = "get Processing Activity And Sub Process with Basic Response For related tab in  Asset")
    @GetMapping("/processing_activity/related")
    public ResponseEntity<Object> getAllRelatedProcessingActivitiesAndSubProcessingActivities(@PathVariable Long unitId) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true, processingActivityService.getAllProcessingActivityWithBasicDetailForAsset(unitId));
    }


    @ApiOperation(value = "updated status of processing activity")
    @PutMapping("/processing_activity/{processingActivityId}/status")
    public ResponseEntity<Object> updateStatusOfProcessingActivity(@PathVariable Long unitId, @PathVariable Long processingActivityId, @RequestParam boolean active) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true, processingActivityService.changeStatusOfProcessingActivity(unitId, processingActivityId, active));
    }

    @ApiOperation(value = "get all Mapped Data Subject ,Data Category and data element of Processing Activity")
    @GetMapping("/processing_activity/{processingActivityId}/data_subject")
    public ResponseEntity<Object> getDataSubjectDataCategoryAndDataElementsMappedWithProcessingActivity(@PathVariable Long unitId, @PathVariable Long processingActivityId) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true, processingActivityService.getDataSubjectDataCategoryAndDataElementsMappedWithProcessingActivity(unitId, processingActivityId));
    }

    @ApiOperation(value = "Get All Processing Activities with Risks")
    @GetMapping("/processing_activity/risk")
    public ResponseEntity<Object> getAllProcessingActivityAndSubProcessingActivitiesWithRisk(@PathVariable Long unitId) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true, processingActivityService.getAllProcessingActivityAndSubProcessingActivitiesWithRisk(unitId));
    }


    @ApiOperation(value = "Get Previous Assessments Launched for Processing Activity")
    @GetMapping("/processing_activity/{processingActivityId}/assesssment")
    public ResponseEntity<Object> getAllAssessmentLaunchedForProcessingActivityById(@PathVariable Long unitId, @PathVariable Long processingActivityId) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true, assessmentService.getAssessmentListByProcessingActivityId(unitId, processingActivityId));
    }

    @ApiOperation(value = "Save Processing Activity And Suggest To country Admin")
    @PostMapping(COUNTRY_URL + "/processing_activity/suggest")
    public ResponseEntity<Object> saveProcessingActivityAndSuggestToCountryAdmin(@PathVariable Long unitId, @PathVariable Long countryId, @Valid @RequestBody ProcessingActivityDTO processingActivityDTO) {
        processingActivityDTO.setSuggested(true);
        return ResponseHandler.generateResponse(HttpStatus.OK, true, processingActivityService.saveProcessingActivityAndSuggestToCountryAdmin(unitId, countryId, processingActivityDTO));
    }

    @ApiOperation(value = "Get  Processing activity Metadata")
    @GetMapping("/processing_activity/meta_data")
    public ResponseEntity<Object> getProcessingActivityMetaData(@PathVariable Long unitId) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true, processingActivityService.getProcessingActivityMetaData(unitId));
    }


}
