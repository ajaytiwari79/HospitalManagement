package com.kairos.controller.data_inventory.processing_activity;


import com.kairos.dto.gdpr.data_inventory.ProcessingActivityDTO;
//import com.kairos.persistence.model.data_inventory.processing_activity.ProcessingActivityRelatedDataSubject;
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
    public ResponseEntity<Object> createProcessingActivity(@PathVariable Long organizationId, @Valid @RequestBody ProcessingActivityDTO processingActivityDTO) {
        processingActivityDTO.setSuggested(false);
        return ResponseHandler.generateResponse(HttpStatus.OK, true, processingActivityService.createProcessingActivity(organizationId, processingActivityDTO));
    }


    @ApiOperation(value = "delete  Processing Activity by Id")
    @DeleteMapping("/processing_activity/delete/{processingActivityId}")
    public ResponseEntity<Object> deleteProcessingActivityById(@PathVariable Long organizationId, @PathVariable Long processingActivityId) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true, processingActivityService.deleteProcessingActivity(organizationId, processingActivityId));
    }

    @ApiOperation(value = "delete  Sub Processing Activity ")
    @DeleteMapping("/processing_activity/{processingActivityId}/subProcess/{subProcessId}")
    public ResponseEntity<Object> deleteSubProcessingActivityById(@PathVariable Long organizationId, @PathVariable Long processingActivityId, @PathVariable Long subProcessingActivityId) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true, processingActivityService.deleteSubProcessingActivity(organizationId, processingActivityId, subProcessingActivityId));
    }

    @ApiOperation(value = "Get All Processing activity With meta data ")
    @GetMapping("/processing_activity")
    public ResponseEntity<Object> getAllProcessingActivityWithMetaData(@PathVariable Long organizationId) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true, processingActivityService.getAllProcessingActivityWithMetaData(organizationId));
    }


    @ApiOperation(value = "update Processing activity  detail")
    @PutMapping("/processing_activity/update/{id}")
    public ResponseEntity<Object> updateProcessingActivityDetail(@PathVariable Long organizationId, @PathVariable Long id, @Valid @RequestBody ProcessingActivityDTO processingActivityDTO) {

        if (organizationId == null) {
            return ResponseHandler.invalidResponse(HttpStatus.BAD_REQUEST, false, "organization id can't be Null");
        }
        return ResponseHandler.generateResponse(HttpStatus.OK, true, processingActivityService.updateProcessingActivity(organizationId, id, processingActivityDTO));
    }

    @ApiOperation(value = "get history of asset or changes done in Asset")
    @GetMapping("/processing_activity/{processingActivityId}/history")
    public ResponseEntity<Object> getHistoryOrDataAuditOfAsset(@PathVariable Long processingActivityId) throws ClassNotFoundException{

        if (processingActivityId == null) {
            return ResponseHandler.invalidResponse(HttpStatus.BAD_REQUEST, false, "processing Activity id can't be Null");
        }
        return ResponseHandler.generateResponse(HttpStatus.OK, true, processingActivityService.getProcessingActivityActivitiesHistory(processingActivityId));
    }

    @ApiOperation(value = "get Processing Activity And Sub Process with Basic Response For related tab in  Asset")
    @GetMapping("/processing_activity/related")
    public ResponseEntity<Object> getAllRelatedProcessingActivitiesAndSubProcessingActivities(@PathVariable Long organizationId) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true, processingActivityService.getAllProcessingActivityWithBasicDetailForAsset(organizationId));
    }


    @ApiOperation(value = "updated status of processing activity")
    @PutMapping("/processing_activity/{processingActivityId}/status")
    public ResponseEntity<Object> updateStatusOfProcessingActivity(@PathVariable Long organizationId, @PathVariable Long processingActivityId, @RequestParam boolean active) {

        if (organizationId == null) {
            return ResponseHandler.invalidResponse(HttpStatus.BAD_REQUEST, false, "Organization id can't be Null");
        }
        return ResponseHandler.generateResponse(HttpStatus.OK, true, processingActivityService.changeStatusOfProcessingActivity(organizationId, processingActivityId, active));
    }

//TODO
    /*@ApiOperation(value = "Map Data Subject ,Data Category and data element to  Processing activity ")
    @PutMapping("/processing_activity/{processingActivityId}/data_subject")
    public ResponseEntity<Object> mapDataSubjectToProcessingActivity(@PathVariable Long organizationId, @PathVariable BigInteger processingActivityId, @Valid @RequestBody ValidateRequestBodyList<ProcessingActivityRelatedDataSubject> dataSubjectList) {
        if (organizationId == null) {
            return ResponseHandler.invalidResponse(HttpStatus.BAD_REQUEST, false, "Organization id can't be Null");
        }
        return ResponseHandler.generateResponse(HttpStatus.OK, true, processingActivityService.mapDataSubjectDataCategoryAndDataElementToProcessingActivity(organizationId, processingActivityId, dataSubjectList.getRequestBody()));
    }*/


    @ApiOperation(value = "get all Mapped Data Subject ,Data Category and data element of Processing Activity")
    @GetMapping("/processing_activity/{processingActivityId}/data_subject")
    public ResponseEntity<Object> getDataSubjectDataCategoryAndDataElementsMappedWithProcessingActivity(@PathVariable Long organizationId, @PathVariable Long processingActivityId) {
        if (organizationId == null) {
            return ResponseHandler.invalidResponse(HttpStatus.BAD_REQUEST, false, "Organization id can't be Null");
        }
        return ResponseHandler.generateResponse(HttpStatus.OK, true, processingActivityService.getDataSubjectDataCategoryAndDataElementsMappedWithProcessingActivity(organizationId, processingActivityId));
    }

    //TODO
    /*@ApiOperation(value = "Link Asset to processing activity")
    @PutMapping("/processing_activity/{processingActivityId}/asset")
    public ResponseEntity<Object> linkAssetToProcessingActivity(@PathVariable Long organizationId, @PathVariable BigInteger processingActivityId, @RequestParam(value = "assetId") BigInteger assetId) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true, processingActivityService.mapAssetWithProcessingActivity(organizationId, processingActivityId, assetId));
    }

    @ApiOperation(value = "get all Asset linked with Processing Activity")
    @GetMapping("/processing_activity/{processingActivityId}/asset")
    public ResponseEntity<Object> getAllMappedAssetWithProcessingActivityById(@PathVariable Long organizationId, @PathVariable BigInteger processingActivityId) {
        if (organizationId == null) {
            return ResponseHandler.invalidResponse(HttpStatus.BAD_REQUEST, false, "Organization id can't be Null");
        }
        return ResponseHandler.generateResponse(HttpStatus.OK, true, processingActivityService.getAllAssetLinkedWithProcessingActivity(organizationId, processingActivityId));
    }


    @ApiOperation(value = "Remove  Asset from Processing Activity ")
    @DeleteMapping("/processing_activity/{processingActivityId}/asset/{assetId}")
    public ResponseEntity<Object> removeLinkedAssetFromProcessingActivity(@PathVariable Long organizationId, @PathVariable BigInteger processingActivityId, @PathVariable BigInteger assetId) {
        if (organizationId == null) {
            return ResponseHandler.invalidResponse(HttpStatus.BAD_REQUEST, false, "Organization id can't be Null");
        } else if (assetId == null) {
            return ResponseHandler.invalidResponse(HttpStatus.BAD_REQUEST, false, "Asset  id can't be Null");
        }
        return ResponseHandler.generateResponse(HttpStatus.OK, true, processingActivityService.removeLinkedAssetFromProcessingActivity(organizationId, processingActivityId, assetId));
    }

    @ApiOperation(value = "Remove Data Subject from processing activity ")
    @DeleteMapping("/processing_activity/{processingActivityId}/data_subject/{dataSubjectId}")
    public ResponseEntity<Object> removeRelatedDataSubjectFromProcessingActivity(@PathVariable Long organizationId, @PathVariable BigInteger processingActivityId, @PathVariable BigInteger dataSubjectId) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true, processingActivityService.removeLinkedDataSubjectFromProcessingActivity(organizationId, processingActivityId, dataSubjectId));
    }


    @ApiOperation(value = "Create And Link RISK to processing activity And Sub Processing Activity")
    @PutMapping("/processing_activity/{processingActivityId}/risk")
    public ResponseEntity<Object> createAndLinkRiskWithProcessingActivityAndSubProcessingActivity(@PathVariable Long organizationId, @PathVariable BigInteger processingActivityId, @Valid @RequestBody ProcessingActivityRiskDTO processingActivityRiskDTO) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true, processingActivityService.createRiskAndLinkWithProcessingActivities(organizationId, processingActivityId, processingActivityRiskDTO));
    }*/

    @ApiOperation(value = "Get All Processing Activities with Risks")
    @GetMapping("/processing_activity/risk")
    public ResponseEntity<Object> getAllProcessingActivityAndSubProcessingActivitiesWithRisk(@PathVariable Long organizationId) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true, processingActivityService.getAllProcessingActivityAndSubProcessingActivitiesWithRisk(organizationId));
    }


   /* @ApiOperation(value = "unlink  risk form Processing activity and Sub Processing Activity")
    @DeleteMapping("/processing_activity/{processingActivityId}/risk/{riskId}")
    public ResponseEntity<Object> unLinkRiskfromProcessingOrSubProcessingActivity(@PathVariable Long organizationId, @PathVariable BigInteger processingActivityId, @PathVariable BigInteger riskId) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true, processingActivityService.unLinkRiskFromProcessingOrSubProcessingActivityAndSafeDeleteRisk(organizationId, processingActivityId, riskId));
    }*/

    @ApiOperation(value = "Get Previous Assessments Launched for Processing Activity")
    @GetMapping("/processing_activity/{processingActivityId}/assesssment")
    public ResponseEntity<Object> getAllAssessmentLaunchedForProcessingActivityById(@PathVariable Long organizationId, @PathVariable Long processingActivityId) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true, assessmentService.getAssessmentListByProcessingActivityId(organizationId, processingActivityId));
    }

    @ApiOperation(value = "Save Processing Activity And Suggest To country Admin")
    @PostMapping(COUNTRY_URL + "/processing_activity/suggest")
    public ResponseEntity<Object> saveProcessingActivityAndSuggestToCountryAdmin(@PathVariable Long organizationId, @PathVariable Long countryId, @Valid @RequestBody ProcessingActivityDTO processingActivityDTO) {
        processingActivityDTO.setSuggested(true);
        return ResponseHandler.generateResponse(HttpStatus.OK, true, processingActivityService.saveProcessingActivityAndSuggestToCountryAdmin(organizationId, countryId, processingActivityDTO));
    }

    @ApiOperation(value = "Get  Processing activity Metadata")
    @GetMapping("/processing_activity/meta_data")
    public ResponseEntity<Object> getProcessingActivityMetaData(@PathVariable Long organizationId) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true, processingActivityService.getProcessingActivityMetaData(organizationId));
    }


}
