package com.kairos.controller.data_inventory.asset;


import com.kairos.gdpr.master_data.AssetTypeDTO;
import com.kairos.service.data_inventory.asset.OrganizationAssetTypeService;
import com.kairos.utils.ResponseHandler;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
public class OrganizationAssetTypeController {


    private static final Logger LOGGER = LoggerFactory.getLogger(OrganizationAssetTypeController.class);

    @Inject
    private OrganizationAssetTypeService assetTypeService;


    @ApiOperation("add AssetType")
    @PostMapping("/asset_type/add")
    public ResponseEntity<Object> createAssetType(@PathVariable Long unitId, @Valid @RequestBody AssetTypeDTO assetTypes) {
        if (unitId == null) {
            return ResponseHandler.invalidResponse(HttpStatus.BAD_REQUEST, false, "organization id can't be null");
        }
        return ResponseHandler.generateResponse(HttpStatus.OK, true, assetTypeService.createAssetTypeAndAddSubAssetTypes(unitId, assetTypes));

    }


    @ApiOperation("get AssetType by id")
    @GetMapping("/asset_type/{id}")
    public ResponseEntity<Object> getAssetType(@PathVariable Long unitId, @PathVariable BigInteger id) {
        if (id == null) {
            return ResponseHandler.invalidResponse(HttpStatus.BAD_REQUEST, false, "id cannot be null");
        } else if (unitId == null) {
            return ResponseHandler.invalidResponse(HttpStatus.BAD_REQUEST, false, "organization id can't be null");
        }
        return ResponseHandler.generateResponse(HttpStatus.OK, true, assetTypeService.getAssetTypeById(unitId, id));

    }


    @ApiOperation("get all AssetType ")
    @GetMapping("/asset_type/all")
    public ResponseEntity<Object> getAllAssetType(@PathVariable Long unitId) {

        if (unitId == null) {
            return ResponseHandler.invalidResponse(HttpStatus.BAD_REQUEST, false, "organization id can't be null");
        }
        return ResponseHandler.generateResponse(HttpStatus.OK, true, assetTypeService.getAllAssetType(unitId));

    }


    @ApiOperation("delete Asset Type by Id")
    @DeleteMapping("/asset_type/{assetTypeId}")
    public ResponseEntity<Object> deleteAssetTypeById(@PathVariable Long unitId, @PathVariable BigInteger assetTypeId) {
        if (unitId == null) {
            return ResponseHandler.invalidResponse(HttpStatus.BAD_REQUEST, false, "organization id can't be null");
        }
        return ResponseHandler.generateResponse(HttpStatus.OK, true, assetTypeService.deleteAssetTypeById(unitId, assetTypeId));

    }


    @ApiOperation("delete Asset Sub type by Id")
    @DeleteMapping("/asset_type/{assetTypeId}/sub_asset_type/{subAssetTypeId}")
    public ResponseEntity<Object> deleteAssetSubTypeById(@PathVariable Long unitId, @PathVariable BigInteger assetTypeId, @PathVariable BigInteger subAssetTypeId) {
        if (unitId == null) {
            return ResponseHandler.invalidResponse(HttpStatus.BAD_REQUEST, false, "organization id can't be null");
        }
        return ResponseHandler.generateResponse(HttpStatus.OK, true, assetTypeService.deleteAssetSubTypeById(unitId, assetTypeId, subAssetTypeId));

    }


    @ApiOperation("update subAsset by id")
    @PutMapping("/asset_type/update/{id}")
    public ResponseEntity<Object> updateAssetType(@PathVariable Long unitId, @PathVariable BigInteger id, @Valid @RequestBody AssetTypeDTO assetType) {
        if (id == null) {
            return ResponseHandler.invalidResponse(HttpStatus.BAD_REQUEST, false, "id cannot be null");
        } else if (unitId == null) {
            return ResponseHandler.invalidResponse(HttpStatus.BAD_REQUEST, false, "organization id can't be null");

        }
        return ResponseHandler.generateResponse(HttpStatus.OK, true, assetTypeService.updateAssetTypeUpdateAndCreateNewSubAssetsAndAddToAssetType(unitId, id, assetType));

    }


}
