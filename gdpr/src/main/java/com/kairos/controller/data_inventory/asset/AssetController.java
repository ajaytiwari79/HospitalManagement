package com.kairos.controller.data_inventory.asset;


import com.kairos.dto.gdpr.data_inventory.AssetDTO;
import com.kairos.service.data_inventory.asset.AssetService;
import com.kairos.utils.ResponseHandler;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.inject.Inject;
import javax.validation.Valid;
import java.util.Map;

import static com.kairos.constants.ApiConstant.API_ORGANIZATION_UNIT_URL;
import static com.kairos.constants.ApiConstant.COUNTRY_URL;
import static com.kairos.constants.AppConstant.IS_SUCCESS;


@RestController
@RequestMapping(API_ORGANIZATION_UNIT_URL)
@Api(API_ORGANIZATION_UNIT_URL)
class AssetController {


    @Inject
    private AssetService assetService;


    @ApiOperation(value = "create asset for organization with basic detail")
    @PostMapping("/asset")
    public ResponseEntity<Object> createAssetWithBasicDetail(@PathVariable Long unitId, @Valid @RequestBody AssetDTO asset) {
        asset.setSuggested(false);
        return ResponseHandler.generateResponse(HttpStatus.OK, true, assetService.saveAsset(unitId, asset));

    }


    @ApiOperation(value = "delete  asset by Id")
    @DeleteMapping("/asset/{assetId}")
    public ResponseEntity<Object> deleteAssetById(@PathVariable Long unitId, @PathVariable Long assetId) {
        Map<String, Object> result = assetService.deleteAssetById(unitId, assetId);
        if ((boolean) result.get(IS_SUCCESS)) {
            return ResponseHandler.generateResponse(HttpStatus.OK, true, result);
        }
        return ResponseHandler.generateResponse(HttpStatus.BAD_REQUEST, false, result);

    }

    @ApiOperation(value = "updated status of processing activity")
    @PutMapping("/asset/{assetId}/status")
    public ResponseEntity<Object> updateStatusOfAsset(@PathVariable Long unitId, @PathVariable Long assetId, @RequestParam(value = "active", required = true) boolean active) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true, assetService.updateStatusOfAsset(unitId, assetId, active));
    }


    @ApiOperation(value = "Get Asset With meta data by Id")
    @GetMapping("/asset/{assetId}")
    public ResponseEntity<Object> getAssetWithMetaDataById(@PathVariable Long unitId, @PathVariable Long assetId) {

        return ResponseHandler.generateResponse(HttpStatus.OK, true, assetService.getAssetWithRelatedDataAndRiskByUnitIdAndId(unitId, assetId));
    }


    @ApiOperation(value = "Get All Asset With meta data ")
    @GetMapping("/asset")
    public ResponseEntity<Object> getAllAssetWithMetaData(@PathVariable Long unitId) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true, assetService.getAllAssetByUnitId(unitId));
    }

    @ApiOperation(value = "get history of asset or changes done in Asset")
    @GetMapping("/asset/{assetId}/history")
    public ResponseEntity<Object> getHistoryOrDataAuditOfAsset(@PathVariable Long unitId,@PathVariable Long assetId) throws ClassNotFoundException{
        return ResponseHandler.generateResponse(HttpStatus.OK, true, assetService.getAssetActivitiesHistory(assetId));
    }


    /*@ApiOperation(value = "Unlink Processing Activity From asset ")
    @DeleteMapping("/asset/{assetId}/processing_activity/{processingActivityId}")
    public ResponseEntity<Object> unLinkProcessingActivityFromAsset(@PathVariable Long unitId, @PathVariable BigInteger assetId, @PathVariable BigInteger processingActivityId) {
        return ResponseHandler.generateResponse(HttpStatus.OK, false, assetService.unLinkProcessingActivityFromAsset(unitId, assetId, processingActivityId));
    }


    @ApiOperation(value = "Unlink Sub Processing Activity From asset ")
    @DeleteMapping("/asset/{assetId}/processing_activity/sub_processing_activity/{subProcessingActivityId}")
    public ResponseEntity<Object> unLinkSubProcessingActivityFromAsset(@PathVariable Long unitId, @PathVariable BigInteger assetId, @PathVariable BigInteger subProcessingActivityId) {
        return ResponseHandler.generateResponse(HttpStatus.OK, false, assetService.unLinkSubProcessingActivityFromAsset(unitId, assetId, subProcessingActivityId));

    }*/

    @ApiOperation(value = "get all active asset used in processing activity related tab")
    @GetMapping("/asset/related")
    public ResponseEntity<Object> getAllActiveAsset(@PathVariable Long unitId) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true, assetService.getAllActiveAsset(unitId));
    }

    /*@ApiOperation(value = "Get Previous Assessments Launched for Asset")
    @GetMapping("/asset/{assetId}/assesssment")
    public ResponseEntity<Object> getAllAssessmentLaunchedForAssetById(@PathVariable Long unitId, @PathVariable BigInteger assetId) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true, assetService.getAssessmentListByAssetId(unitId, assetId));
    }*/


    @ApiOperation(value = "Save Processing Activity And Suggest To country Admin")
    @PostMapping(COUNTRY_URL + "/asset/suggest")
    public ResponseEntity<Object> saveAssetAndSuggestToCountryAdmin(@PathVariable Long unitId, @PathVariable Long countryId, @Valid @RequestBody AssetDTO assetDTO) {
        assetDTO.setSuggested(true);
        return ResponseHandler.generateResponse(HttpStatus.OK, true, assetService.saveAssetAndSuggestToCountryAdmin(unitId, countryId, assetDTO));
    }

    @ApiOperation(value = "Get Asset Metadata")
    @GetMapping("/asset/meta_data")
    public ResponseEntity<Object> getAssetMetaData(@PathVariable Long unitId) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true, assetService.getAssetMetaData(unitId));
    }


}
