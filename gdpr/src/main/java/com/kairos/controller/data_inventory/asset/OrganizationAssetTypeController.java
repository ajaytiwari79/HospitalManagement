package com.kairos.controller.data_inventory.asset;


import com.kairos.dto.gdpr.data_inventory.AssetTypeOrganizationLevelDTO;
import com.kairos.dto.gdpr.master_data.AssetTypeDTO;
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
    private OrganizationAssetTypeService organizationAssetTypeService;


    @ApiOperation("add AssetType")
    @PostMapping("/asset_type/add")
    public ResponseEntity<Object> createAssetType(@PathVariable Long unitId, @Valid @RequestBody AssetTypeOrganizationLevelDTO assetTypeDTO) {
        if (unitId == null) {
            return ResponseHandler.invalidResponse(HttpStatus.BAD_REQUEST, false, "organization id can't be null");
        }
        return ResponseHandler.generateResponse(HttpStatus.OK, true, organizationAssetTypeService.createAssetTypeAndAddSubAssetTypes(unitId, assetTypeDTO));

    }


    @ApiOperation("get AssetType by id")
    @GetMapping("/asset_type/{id}")
    public ResponseEntity<Object> getAssetType(@PathVariable Long unitId, @PathVariable BigInteger id) {
        if (id == null) {
            return ResponseHandler.invalidResponse(HttpStatus.BAD_REQUEST, false, "id cannot be null");
        } else if (unitId == null) {
            return ResponseHandler.invalidResponse(HttpStatus.BAD_REQUEST, false, "organization id can't be null");
        }
        return ResponseHandler.generateResponse(HttpStatus.OK, true, organizationAssetTypeService.getAssetTypeById(unitId, id));

    }


    @ApiOperation("get all AssetType with risk")
    @GetMapping("/asset_type/all")
    public ResponseEntity<Object> getAllAssetType(@PathVariable Long unitId) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true, organizationAssetTypeService.getAllAssetType(unitId));

    }

    @ApiOperation("get all Sub Asset Type of Asset type  with risks ")
    @GetMapping("/asset_type/{assetTypeId}/sub_asset_type")
    public ResponseEntity<Object> getAllSubAssetTypeOfAssetType(@PathVariable Long unitId, @PathVariable BigInteger assetTypeId) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true, organizationAssetTypeService.getSubAssetTypesOfAssetTypeWithRisks(unitId, assetTypeId));
    }


    @ApiOperation("delete Asset Type by Id")
    @DeleteMapping("/asset_type/{assetTypeId}")
    public ResponseEntity<Object> deleteAssetTypeById(@PathVariable Long unitId, @PathVariable BigInteger assetTypeId) {
        if (unitId == null) {
            return ResponseHandler.invalidResponse(HttpStatus.BAD_REQUEST, false, "organization id can't be null");
        }
        return ResponseHandler.generateResponse(HttpStatus.OK, true, organizationAssetTypeService.deleteAssetTypeById(unitId, assetTypeId));

    }


    @ApiOperation("delete Asset Sub type by Id")
    @DeleteMapping("/asset_type/{assetTypeId}/sub_asset_type/{subAssetTypeId}")
    public ResponseEntity<Object> deleteAssetSubTypeById(@PathVariable Long unitId, @PathVariable BigInteger assetTypeId, @PathVariable BigInteger subAssetTypeId) {
        if (unitId == null) {
            return ResponseHandler.invalidResponse(HttpStatus.BAD_REQUEST, false, "organization id can't be null");
        }
        return ResponseHandler.generateResponse(HttpStatus.OK, true, organizationAssetTypeService.deleteAssetSubTypeById(unitId, assetTypeId, subAssetTypeId));

    }

    @ApiOperation("unlinke Risk From Asset Type and delete risk")
    @DeleteMapping("/asset_type/{assetTypeId}/risk/{riskId}")
    public ResponseEntity<Object> unlinkRiskFromAssetType(@PathVariable Long unitId, @PathVariable BigInteger assetTypeId, @PathVariable BigInteger riskId) {
        if (assetTypeId == null) {
            return ResponseHandler.invalidResponse(HttpStatus.BAD_REQUEST, false, "Asset Type Id can't be null");
        }
        return ResponseHandler.generateResponse(HttpStatus.OK, true, organizationAssetTypeService.unlinkRiskFromAssetTypeOrSubAssetTypeAndDeletedRisk(unitId, assetTypeId, riskId));

    }


    @ApiOperation("unlinke Risk From Sub Asset Type and delete risk")
    @DeleteMapping("/asset_type/sub_asset_type/{subAssetTypeId}/risk/{riskId}")
    public ResponseEntity<Object> unlinkRiskFromSubAssetType(@PathVariable Long unitId, @PathVariable BigInteger subAssetTypeId, @PathVariable BigInteger riskId) {
        if (subAssetTypeId == null) {
            return ResponseHandler.invalidResponse(HttpStatus.BAD_REQUEST, false, "Sub Asset Type Id can't be null");
        }
        return ResponseHandler.generateResponse(HttpStatus.OK, true, organizationAssetTypeService.unlinkRiskFromAssetTypeOrSubAssetTypeAndDeletedRisk(unitId, subAssetTypeId, riskId));

    }


    @ApiOperation("update subAsset by id")
    @PutMapping("/asset_type/update/{id}")
    public ResponseEntity<Object> updateAssetType(@PathVariable Long unitId, @PathVariable BigInteger id, @Valid @RequestBody AssetTypeOrganizationLevelDTO assetTypeDTO) {
        if (id == null) {
            return ResponseHandler.invalidResponse(HttpStatus.BAD_REQUEST, false, "id cannot be null");
        } else if (unitId == null) {
            return ResponseHandler.invalidResponse(HttpStatus.BAD_REQUEST, false, "organization id can't be null");

        }
        return ResponseHandler.generateResponse(HttpStatus.OK, true, organizationAssetTypeService.updateAssetTypeAndSubAssetsAndAddRisks(unitId, id, assetTypeDTO));

    }


}
