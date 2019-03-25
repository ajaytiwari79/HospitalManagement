package com.kairos.controller.master_data.asset_management;


import com.kairos.dto.gdpr.master_data.AssetTypeDTO;
import com.kairos.service.master_data.asset_management.AssetTypeService;
import com.kairos.utils.ResponseHandler;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.inject.Inject;
import javax.validation.Valid;

import static com.kairos.constants.ApiConstant.API_ORGANIZATION_COUNTRY_URL;


/*
 *
 *  created by bobby 17/5/2018
 * */


@RestController
@RequestMapping(API_ORGANIZATION_COUNTRY_URL)
@Api(API_ORGANIZATION_COUNTRY_URL)
class AssetTypeController {

    @Inject
    private AssetTypeService assetTypeService;


    @ApiOperation("add AssetType")
    @PostMapping("/asset_type")
    public ResponseEntity<Object> createAssetType(@PathVariable Long countryId, @Valid @RequestBody AssetTypeDTO assetTypes) {

        return ResponseHandler.generateResponse(HttpStatus.OK, true, assetTypeService.createAssetTypeAndAddSubAssetTypes(countryId, assetTypes));

    }


    @ApiOperation("get AssetType by id")
    @GetMapping("/asset_type/{assetTypeId}")
    public ResponseEntity<Object> getAssetType(@PathVariable Long countryId, @PathVariable Long assetTypeId) {

        return ResponseHandler.generateResponse(HttpStatus.OK, true, assetTypeService.getAssetTypeById(countryId, assetTypeId));

    }


    @ApiOperation("get all AssetType  with sub Asset type")
    @GetMapping("/asset_type")
    public ResponseEntity<Object> getAllAssetType(@PathVariable Long countryId) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true, assetTypeService.getAllAssetTypeWithSubAssetTypeAndRisk(countryId));

    }

    @ApiOperation("delete AssetType  by id")
    @DeleteMapping("/asset_type/{assetTypeId}")
    public ResponseEntity<Object> deleteAssetTypeById(@PathVariable Long countryId, @PathVariable Long assetTypeId) {

        return ResponseHandler.generateResponse(HttpStatus.OK, false, assetTypeService.deleteAssetType(countryId,assetTypeId));

    }

    @ApiOperation("update Asset type and Sub Asset ")
    @PutMapping("/asset_type/{assetTypeId}")
    public ResponseEntity<Object> updateAssetType(@PathVariable Long countryId, @PathVariable Long assetTypeId, @Valid @RequestBody AssetTypeDTO assetType) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true, assetTypeService.updateAssetTypeUpdateAndCreateNewSubAssetsAndAddToAssetType(countryId, assetTypeId, assetType));

    }

}
