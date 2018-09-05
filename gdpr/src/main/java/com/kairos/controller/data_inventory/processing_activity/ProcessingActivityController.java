package com.kairos.controller.data_inventory.processing_activity;


import com.kairos.gdpr.data_inventory.ProcessingActivityDTO;
import com.kairos.persistance.model.data_inventory.processing_activity.ProcessingActivityRelatedDataSubject;
import com.kairos.service.data_inventory.processing_activity.ProcessingActivityService;
import com.kairos.utils.ResponseHandler;
import com.kairos.utils.ValidateRequestBodyList;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.inject.Inject;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;

import java.math.BigInteger;
import java.util.List;
import java.util.Map;

import static com.kairos.constants.ApiConstant.COUNTRY_URL;
import static com.kairos.constants.AppConstant.IS_SUCCESS;
import static com.kairos.constants.ApiConstant.API_ORGANIZATION_URL_UNIT_URL;


@RestController
@RequestMapping(API_ORGANIZATION_URL_UNIT_URL)
@Api(API_ORGANIZATION_URL_UNIT_URL)
public class ProcessingActivityController {


    @Inject
    private ProcessingActivityService processingActivityService;


    @ApiOperation(value = "create Processing activity ")
    @PostMapping("/processing_activity")
    public ResponseEntity<Object> createProcessingActivity(@PathVariable Long unitId, @Valid @RequestBody ProcessingActivityDTO processingActivityDTO) {
        if (unitId == null) {
            return ResponseHandler.invalidResponse(HttpStatus.BAD_REQUEST, false, "ManagingOrganization id can't be Null");
        }
        return ResponseHandler.generateResponse(HttpStatus.OK, true, processingActivityService.createProcessingActivity(unitId, processingActivityDTO));
    }


    @ApiOperation(value = "delete  Processing Activity by Id")
    @DeleteMapping("/processing_activity/delete/{processingActivityId}")
    public ResponseEntity<Object> deleteProcessingActivityById(@PathVariable Long unitId, @PathVariable BigInteger processingActivityId) {

        if (unitId == null) {
            return ResponseHandler.generateResponse(HttpStatus.BAD_REQUEST, false, "Organization Id can't be null");
        }
        return ResponseHandler.generateResponse(HttpStatus.OK, true, processingActivityService.deleteProcessingActivity(unitId, processingActivityId));
    }

    @ApiOperation(value = "delete  Sub Processing Activity ")
    @DeleteMapping("/processing_activity/{processingActivityId}/subProcess/{subProcessId}")
    public ResponseEntity<Object> deleteSubProcessingActivityById(@PathVariable Long unitId, @PathVariable BigInteger processingActivityId, @PathVariable BigInteger subProcessId) {

        if (unitId == null) {
            return ResponseHandler.generateResponse(HttpStatus.BAD_REQUEST, false, "Organization Id can't be null");
        } else if (processingActivityId == null) {
            return ResponseHandler.generateResponse(HttpStatus.BAD_REQUEST, false, "Processing Activity Id can't be null");
        }
        return ResponseHandler.generateResponse(HttpStatus.OK, true, processingActivityService.deleteSubProcessingActivity(unitId, processingActivityId, subProcessId));
    }

    @ApiOperation(value = "Get  Sub Processing Activities of Processing Activity ")
    @GetMapping("/processing_activity/{id}")
    public ResponseEntity<Object> getProcessingActivityWithMetaDataById(@PathVariable Long unitId, @PathVariable BigInteger id) {

        if (unitId == null) {
            return ResponseHandler.invalidResponse(HttpStatus.BAD_REQUEST, false, "organization id can't be Null");
        }
        return ResponseHandler.generateResponse(HttpStatus.OK, true, processingActivityService.getProcessingActivityWithWithSubProcessingActivitiesById(unitId, id));
    }


    @ApiOperation(value = "Get All Processing activity With meta data ")
    @GetMapping("/processing_activity")
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

    @ApiOperation(value = "get history of asset or changes done in Asset")
    @GetMapping("/processing_activity/{processingActivityId}/history")
    public ResponseEntity<Object> getHistoryOrDataAuditOfAsset(@PathVariable BigInteger processingActivityId, @RequestParam(defaultValue = "5") int size, @RequestParam(defaultValue = "0") int skip) {

        if (processingActivityId == null) {
            return ResponseHandler.invalidResponse(HttpStatus.BAD_REQUEST, false, "processing Activity id can't be Null");
        }
        return ResponseHandler.generateResponse(HttpStatus.OK, true, processingActivityService.getProcessingActivityActivitiesHistory(processingActivityId, size, skip));
    }


    @ApiOperation(value = "get history of asset or changes done in Asset")
    @GetMapping("/processing_activity/related")
    public ResponseEntity<Object> getAllRelatedProcessingActivitiesAndSubProcessingActivities(@PathVariable Long unitId) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true, processingActivityService.getAllProcessingActivityBasicDetailsAndWithSubProcess(unitId));
    }


    @ApiOperation(value = "updated status of processing activity")
    @PutMapping("/processing_activity/{processingActivityId}/status")
    public ResponseEntity<Object> updateStatusOfProcessingActivity(@PathVariable Long unitId, @PathVariable BigInteger processingActivityId, @RequestParam boolean active) {

        if (unitId == null) {
            return ResponseHandler.invalidResponse(HttpStatus.BAD_REQUEST, false, "Organization id can't be Null");
        }
        return ResponseHandler.generateResponse(HttpStatus.OK, true, processingActivityService.changeStatusOfProcessingActivity(unitId, processingActivityId, active));
    }


    @ApiOperation(value = "Map Data Subject ,Data Category and data element to  Processing activity ")
    @PutMapping("/processing_activity/{processingActivityId}/data_subject")
    public ResponseEntity<Object> mapDataSubjectToProcessingActivity(@PathVariable Long unitId, @PathVariable BigInteger processingActivityId, @Valid @RequestBody ValidateRequestBodyList<ProcessingActivityRelatedDataSubject> dataSubjectList) {
        if (unitId == null) {
            return ResponseHandler.invalidResponse(HttpStatus.BAD_REQUEST, false, "Organization id can't be Null");
        }
        return ResponseHandler.generateResponse(HttpStatus.OK, true, processingActivityService.mapDataSubjectDataCategoryAndDataElementToProcessingActivity(unitId, processingActivityId, dataSubjectList.getRequestBody()));
    }


    @ApiOperation(value = "get all Mapped Data Subject ,Data Category and data element of Processing Activity")
    @GetMapping("/processing_activity/{processingActivityId}/data_subject")
    public ResponseEntity<Object> getDataSubjectDataCategoryAndDataElementsMappedWithProcessingActivity(@PathVariable Long unitId, @PathVariable BigInteger processingActivityId) {
        if (unitId == null) {
            return ResponseHandler.invalidResponse(HttpStatus.BAD_REQUEST, false, "Organization id can't be Null");
        }
        return ResponseHandler.generateResponse(HttpStatus.OK, true, processingActivityService.getDataSubjectDataCategoryAndDataElementsMappedWithProcessingActivity(unitId, processingActivityId));
    }


    @ApiOperation(value = "Link Asset to processing activity")
    @PutMapping("/processing_activity/{processingActivityId}/asset/{assetId}")
    public ResponseEntity<Object> linkAssetToProcessingActivity(@PathVariable Long unitId, @PathVariable BigInteger processingActivityId, @PathVariable BigInteger assetId) {
        if (unitId == null) {
            return ResponseHandler.invalidResponse(HttpStatus.BAD_REQUEST, false, "Organization id can't be Null");
        }
        return ResponseHandler.generateResponse(HttpStatus.OK, true, processingActivityService.mapAssetWithProcessingActivity(unitId, processingActivityId, assetId));
    }

    @ApiOperation(value = "get all Asset linked with Perocessing Activity")
    @GetMapping("/processing_activity/{processingActivityId}/asset")
    public ResponseEntity<Object> getAllMappedAssetWithProcessingActivityById(@PathVariable Long unitId, @PathVariable BigInteger processingActivityId) {
        if (unitId == null) {
            return ResponseHandler.invalidResponse(HttpStatus.BAD_REQUEST, false, "Organization id can't be Null");
        }
        return ResponseHandler.generateResponse(HttpStatus.OK, true, processingActivityService.getAllAssetLinkedWithProcessingActivity(unitId, processingActivityId));
    }


    @ApiOperation(value = "Remove  Asset from Processing Activity ")
    @DeleteMapping("/processing_activity/{processingActivityId}/asset/{assetId}")
    public ResponseEntity<Object> removeLinkedAssetFromProcessingActivity(@PathVariable Long unitId, @PathVariable BigInteger processingActivityId, @PathVariable BigInteger assetId) {
        if (unitId == null) {
            return ResponseHandler.invalidResponse(HttpStatus.BAD_REQUEST, false, "Organization id can't be Null");
        } else if (assetId == null) {
            return ResponseHandler.invalidResponse(HttpStatus.BAD_REQUEST, false, "Asset  id can't be Null");
        }
        return ResponseHandler.generateResponse(HttpStatus.OK, true, processingActivityService.removeLinkedAssetFromProcessingActivity(unitId, processingActivityId, assetId));
    }

    @ApiOperation(value = "Remove Data Subject from processing activity ")
    @DeleteMapping("/processing_activity/{processingActivityId}/data_subject/{dataSubjectId}")
    public ResponseEntity<Object> removeRelatedDataSubjectFromProcessingActivity(@PathVariable Long unitId, @PathVariable BigInteger processingActivityId, @PathVariable BigInteger dataSubjectId) {

        if (unitId == null) {
            return ResponseHandler.invalidResponse(HttpStatus.BAD_REQUEST, false, "Organization id can't be Null");
        } else if (processingActivityId == null) {
            return ResponseHandler.invalidResponse(HttpStatus.BAD_REQUEST, false, "processing Activity id can't be Null");
        }
        return ResponseHandler.generateResponse(HttpStatus.OK, true, processingActivityService.removeLinkedDataSubjectFromProcessingActivity(unitId, processingActivityId, dataSubjectId));
    }


}
