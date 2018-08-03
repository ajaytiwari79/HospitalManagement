package com.kairos.controller.data_inventory.asset;


import com.kairos.dto.data_inventory.AssetDTO;
import com.kairos.persistance.model.data_inventory.asset.Asset;
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

import static com.kairos.constants.ApiConstant.API_ORGANIZATION_URL_UNIT_URL;

@RestController
@RequestMapping(API_ORGANIZATION_URL_UNIT_URL)
@Api(API_ORGANIZATION_URL_UNIT_URL)
public class AssetController {


    @Inject
    private AssetService assetService;


    @ApiOperation(value = "create asset for organization with basic detail")
    @PostMapping("/asset")
    public ResponseEntity<Object> createAssetWithBasicDetail( @PathVariable Long unitId, @Valid @RequestBody Asset asset) {
        if (unitId == null) {
            return ResponseHandler.invalidResponse(HttpStatus.BAD_REQUEST, false, "ManagingOrganization id can't be Null");
        }
        return ResponseHandler.generateResponse(HttpStatus.OK, true, assetService.createAsseWithBasictDetail( unitId, asset));
    }


    @ApiOperation(value = "delete  asset by Id")
    @DeleteMapping("/asset/{assetId}")
    public ResponseEntity<Object> deleteAssetById( @PathVariable Long unitId, @PathVariable BigInteger assetId) {

        if (unitId == null) {
            return ResponseHandler.invalidResponse(HttpStatus.BAD_REQUEST, false, "ManagingOrganization id can't be Null");
        }
        return ResponseHandler.generateResponse(HttpStatus.OK, true, assetService.deleteAssetById( unitId, assetId));
    }



    @ApiOperation(value = "update asset basic detail")
    @PutMapping("/asset/update/{assetId}")
    public ResponseEntity<Object> updateAssetData( @PathVariable Long unitId,@PathVariable BigInteger assetId, @Valid @RequestBody Asset asset) {

       if (unitId == null) {
            return ResponseHandler.invalidResponse(HttpStatus.BAD_REQUEST, false, "ManagingOrganization id can't be Null");
        }
        return ResponseHandler.generateResponse(HttpStatus.OK, true, assetService.updateAssetData( unitId,assetId, asset));
    }





    @ApiOperation(value = "Get Asset With meta data by Id")
    @GetMapping("/asset/{assetId}")
    public ResponseEntity<Object> getAssetWithMetaDatabyId( @PathVariable Long unitId,@PathVariable BigInteger assetId) {

        if (unitId == null) {
            return ResponseHandler.invalidResponse(HttpStatus.BAD_REQUEST, false, "ManagingOrganization id can't be Null");
        }
        return ResponseHandler.generateResponse(HttpStatus.OK, true, assetService.getAssetWithMetadataById( unitId,assetId));
    }




    @ApiOperation(value = "Get All Asset With meta data ")
    @GetMapping("/asset")
    public ResponseEntity<Object> getAllAssetWithMetaData( @PathVariable Long unitId,@PathVariable BigInteger assetId) {

        if (unitId == null) {
            return ResponseHandler.invalidResponse(HttpStatus.BAD_REQUEST, false, "ManagingOrganization id can't be Null");
        }
        return ResponseHandler.generateResponse(HttpStatus.OK, true, assetService.getAllAssetWithMetadata( unitId));
    }




    @ApiOperation(value = "get history of asset or changes done in Asset")
    @GetMapping("/asset/{assetId}/history")
    public ResponseEntity<Object> getHistoryOrDataAuditOfAsset( @PathVariable BigInteger assetId)  throws ClassNotFoundException {

        if (assetId == null) {
            return ResponseHandler.invalidResponse(HttpStatus.BAD_REQUEST, false, "Asset  id can't be Null");
        }
        return ResponseHandler.generateResponse(HttpStatus.OK, true, assetService.getAssetActivities( assetId));
    }


}
