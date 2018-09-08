package com.kairos.controller.master_data.asset_management;


import com.kairos.dto.gdpr.master_data.AssetTypeDTO;
import com.kairos.service.master_data.asset_management.AssetTypeService;
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

import static com.kairos.constants.ApiConstant.API_ORGANIZATION_URL;


/*
 *
 *  created by bobby 17/5/2018
 * */


@RestController
@RequestMapping(API_ORGANIZATION_URL)
@Api(API_ORGANIZATION_URL)
public class AssetTypeController {

    private static final Logger LOGGER = LoggerFactory.getLogger(AssetTypeController.class);

    @Inject
    private AssetTypeService assetTypeService;


    @ApiOperation("add AssetType")
    @PostMapping("/asset_type/add")
    public ResponseEntity<Object> createAssetType(@PathVariable Long countryId, @Valid @RequestBody AssetTypeDTO assetTypes) {
        if (countryId == null) {
            return ResponseHandler.invalidResponse(HttpStatus.BAD_REQUEST, false, "country id can't be null");
        }
        return ResponseHandler.generateResponse(HttpStatus.OK, true, assetTypeService.createAssetTypeAndAddSubAssetTypes(countryId, assetTypes));

    }


    @ApiOperation("get AssetType by id")
    @GetMapping("/asset_type/{id}")
    public ResponseEntity<Object> getAssetType(@PathVariable Long countryId, @PathVariable BigInteger id) {
        if (id == null) {
            return ResponseHandler.invalidResponse(HttpStatus.BAD_REQUEST, false, "id cannot be null");
        } else if (countryId == null) {
            return ResponseHandler.invalidResponse(HttpStatus.BAD_REQUEST, false, "country id can't be null");
        }

        return ResponseHandler.generateResponse(HttpStatus.OK, true, assetTypeService.getAssetTypeById(countryId, id));

    }


    @ApiOperation("get all AssetType ")
    @GetMapping("/asset_type/all")
    public ResponseEntity<Object> getAllAssetType(@PathVariable Long countryId) {
        if (countryId == null) {
            return ResponseHandler.invalidResponse(HttpStatus.BAD_REQUEST, false, "country id can't be null");
        }
        return ResponseHandler.generateResponse(HttpStatus.OK, true, assetTypeService.getAllAssetType(countryId));

    }


    @ApiOperation("delete AssetType  by id")
    @DeleteMapping("/asset_type/{assetId}")
    public ResponseEntity<Object> deleteAssetTypeById(@PathVariable Long countryId, @PathVariable BigInteger assetId) {

        return ResponseHandler.generateResponse(HttpStatus.OK, false, assetTypeService.deleteAssetType(countryId,assetId));

    }

    @ApiOperation("update subAsset by id")
    @PutMapping("/asset_type/update/{id}")
    public ResponseEntity<Object> updateAssetType(@PathVariable Long countryId, @PathVariable BigInteger id, @Valid @RequestBody AssetTypeDTO assetType) {
        if (id == null) {
            return ResponseHandler.invalidResponse(HttpStatus.BAD_REQUEST, false, "id cannot be null");
        }
        if (countryId == null) {
            return ResponseHandler.invalidResponse(HttpStatus.BAD_REQUEST, false, "country id can't be null");
        }
        return ResponseHandler.generateResponse(HttpStatus.OK, true, assetTypeService.updateAssetTypeUpdateAndCreateNewSubAssetsAndAddToAssetType(countryId, id, assetType));

    }

    @ApiOperation("unlinke Risk From Asset Type and delete risk")
    @DeleteMapping("/asset_type/{assetTypeId}/risk/{riskId}")
    public ResponseEntity<Object> unlinkRiskFromAssetType(@PathVariable Long countryId, @PathVariable BigInteger assetTypeId,@PathVariable BigInteger riskId ) {
        if (assetTypeId == null) {
            return ResponseHandler.invalidResponse(HttpStatus.BAD_REQUEST, false, "Asset Type Id can't be null");
        }
        if (countryId == null) {
            return ResponseHandler.invalidResponse(HttpStatus.BAD_REQUEST, false, "country id can't be null");
        }
        return ResponseHandler.generateResponse(HttpStatus.OK, true, assetTypeService.unlinkRiskFromAssetTypeOrSubAssetTypeAndDeletedRisk(countryId, assetTypeId, riskId));

    }


    @ApiOperation("unlinke Risk From Sub Asset Type and delete risk")
    @DeleteMapping("/asset_type/sub_asset_type/{subAssetTypeId}/risk/{riskId}")
    public ResponseEntity<Object> unlinkRiskFromSubAssetType(@PathVariable Long countryId, @PathVariable BigInteger subAssetTypeId,@PathVariable BigInteger riskId ) {
        if (subAssetTypeId == null) {
            return ResponseHandler.invalidResponse(HttpStatus.BAD_REQUEST, false, "Sub Asset Type Id can't be null");
        }
        if (countryId == null) {
            return ResponseHandler.invalidResponse(HttpStatus.BAD_REQUEST, false, "country id can't be null");
        }
        return ResponseHandler.generateResponse(HttpStatus.OK, true, assetTypeService.unlinkRiskFromAssetTypeOrSubAssetTypeAndDeletedRisk(countryId, subAssetTypeId, riskId));

    }


}
