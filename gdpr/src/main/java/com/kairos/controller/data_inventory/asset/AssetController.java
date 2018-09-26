package com.kairos.controller.data_inventory.asset;


import com.kairos.dto.gdpr.data_inventory.AssetDTO;
import com.kairos.dto.gdpr.data_inventory.AssetRelateProcessingActivityDTO;
import com.kairos.service.data_inventory.asset.AssetService;
import com.kairos.utils.ResponseHandler;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.inject.Inject;
import javax.validation.Valid;
import java.math.BigInteger;
import java.util.Map;

import static com.kairos.constants.ApiConstant.API_ORGANIZATION_UNIT_URL;
import static com.kairos.constants.AppConstant.IS_SUCCESS;


@RestController
@RequestMapping(API_ORGANIZATION_UNIT_URL)
@Api(API_ORGANIZATION_UNIT_URL)
public class AssetController {


    @Inject
    private AssetService assetService;


    @ApiOperation(value = "create asset for organization with basic detail")
    @PostMapping("/asset")
    public ResponseEntity<Object> createAssetWithBasicDetail(@PathVariable Long unitId, @Valid @RequestBody AssetDTO asset) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true, assetService.createAssetWithBasicDetail(unitId, asset));
    }


    @ApiOperation(value = "delete  asset by Id")
    @DeleteMapping("/asset/{assetId}")
    public ResponseEntity<Object> deleteAssetById(@PathVariable Long unitId, @PathVariable BigInteger assetId) {
        Map<String, Object> result = assetService.deleteAssetById(unitId, assetId);
        if ((boolean) result.get(IS_SUCCESS)) {
            return ResponseHandler.generateResponse(HttpStatus.OK, true, result);
        }
        return ResponseHandler.generateResponse(HttpStatus.BAD_REQUEST, false, result);

    }

    @ApiOperation(value = "updated status of processing activity")
    @PutMapping("/asset/{assetId}/status")
    public ResponseEntity<Object> updateStatusOfAsset(@PathVariable Long unitId, @PathVariable BigInteger assetId, @RequestParam(value = "active", required = true) boolean active) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true, assetService.updateStatusOfAsset(unitId, assetId, active));
    }


    @ApiOperation(value = "update asset basic detail")
    @PutMapping("/asset/update/{assetId}")
    public ResponseEntity<Object> updateAssetData(@PathVariable Long unitId, @PathVariable BigInteger assetId, @Valid @RequestBody AssetDTO asset) {

        return ResponseHandler.generateResponse(HttpStatus.OK, true, assetService.updateAssetData(unitId, assetId, asset));
    }


    @ApiOperation(value = "Get Asset With meta data by Id")
    @GetMapping("/asset/{assetId}")
    public ResponseEntity<Object> getAssetWithMetaDataById(@PathVariable Long unitId, @PathVariable BigInteger assetId) {

        return ResponseHandler.generateResponse(HttpStatus.OK, true, assetService.getAssetWithMetadataById(unitId, assetId));
    }


    @ApiOperation(value = "Get All Asset With meta data ")
    @GetMapping("/asset")
    public ResponseEntity<Object> getAllAssetWithMetaData(@PathVariable Long unitId) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true, assetService.getAllAssetWithMetadata(unitId));
    }


    @ApiOperation(value = "get history of asset or changes done in Asset")
    @GetMapping("/asset/{assetId}/history")
    public ResponseEntity<Object> getHistoryOrDataAuditOfAsset(@PathVariable BigInteger assetId) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true, assetService.getAssetActivitiesHistory(assetId));
    }


    @ApiOperation(value = "Add processing Activity to Asset ")
    @PutMapping("/asset/{assetId}/processing_activity")
    public ResponseEntity<Object> relateProcessingActivitiesAndSubProcessingActivitiesToAsset(@PathVariable Long unitId, @PathVariable BigInteger assetId, @Valid @RequestBody AssetRelateProcessingActivityDTO relateProcessingActivityDTO) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true, assetService.addProcessingActivitiesAndSubProcessingActivitiesToAsset(unitId, assetId, relateProcessingActivityDTO));
    }

    @ApiOperation(value = "get Processing activity and Sub processing Activity  related with asset")
    @GetMapping("/asset/{assetId}/processing_activity")
    public ResponseEntity<Object> getRelatedSubProcessingActivityAndSubProcessingActivity(@PathVariable Long unitId, @PathVariable BigInteger assetId) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true, assetService.getAllRelatedProcessingActivityAndSubProcessingActivities(unitId, assetId));
    }


    @ApiOperation(value = "Unlink Processing Activity From asset ")
    @DeleteMapping("/asset/{assetId}/processing_activity/{processingActivityId}")
    public ResponseEntity<Object> unLinkProcessingActivityFromAsset(@PathVariable Long unitId, @PathVariable BigInteger assetId, @PathVariable BigInteger processingActivityId) {
        return ResponseHandler.generateResponse(HttpStatus.OK, false, assetService.unLinkProcessingActivityFromAsset(unitId, assetId, processingActivityId));
    }


    @ApiOperation(value = "Unlink Sub Processing Activity From asset ")
    @DeleteMapping("/asset/{assetId}/processing_activity/sub_processing_activity/{subProcessingActivityId}")
    public ResponseEntity<Object> unLinkSubProcessingActivityFromAsset(@PathVariable Long unitId, @PathVariable BigInteger assetId, @PathVariable BigInteger subProcessingActivityId) {
        return ResponseHandler.generateResponse(HttpStatus.OK, false, assetService.unLinkSubProcessingActivityFromAsset(unitId, assetId, subProcessingActivityId));

    }

    @ApiOperation(value = "get all active asset used in processing activity related tab")
    @GetMapping("/asset/related")
    public ResponseEntity<Object> getAllActiveAsset(@PathVariable Long unitId) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true, assetService.getAllActiveAsset(unitId));
    }

}
