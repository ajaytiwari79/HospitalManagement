package com.kairos.controller.master_data.asset_management;


import com.kairos.gdpr.master_data.AssetTypeDTO;
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


    @ApiOperation("get AssetType by name")
    @GetMapping("/asset_type/name")
    public ResponseEntity<Object> getAssetTypeByName(@PathVariable Long countryId, @RequestParam String name) {
        if (countryId == null) {
            return ResponseHandler.invalidResponse(HttpStatus.BAD_REQUEST, false, "country id can't be null");
        }
        return ResponseHandler.generateResponse(HttpStatus.OK, true, assetTypeService.getAssetTypeByName(countryId, name));

    }


    @ApiOperation("delete AssetType  by id")
    @DeleteMapping("/asset_type/delete/{id}")
    public ResponseEntity<Object> deleteAssetTypeById(@PathVariable Long countryId, @PathVariable BigInteger id) {

        return ResponseHandler.generateResponse(HttpStatus.OK, false, assetTypeService.deleteAssetType(countryId,id));

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


}
