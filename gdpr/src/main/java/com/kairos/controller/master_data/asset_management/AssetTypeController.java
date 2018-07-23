package com.kairos.controller.master_data.asset_management;


import com.kairos.dto.master_data.AssetTypeDTO;
import com.kairos.persistance.model.master_data.default_asset_setting.AssetType;
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
import java.util.Map;

import static com.kairos.constants.ApiConstant.API_ORGANIZATION_URL;
import static com.kairos.constants.ApiConstant.UNIT_URL;


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
    public ResponseEntity<Object> createAssetType(@PathVariable Long countryId, @PathVariable Long organizationId, @Valid @RequestBody AssetTypeDTO assetTypes) {
        if (countryId == null) {
            return ResponseHandler.invalidResponse(HttpStatus.BAD_REQUEST, false, "country id can't be null");
        } else if (organizationId == null) {
            return ResponseHandler.invalidResponse(HttpStatus.BAD_REQUEST, false, "organization id can't be null");
        }
        return ResponseHandler.generateResponse(HttpStatus.OK, true, assetTypeService.createAssetTypeAndAddSubAssetTypes(countryId, organizationId, assetTypes));

    }


    @ApiOperation("get AssetType by id")
    @GetMapping("/asset_type/{id}")
    public ResponseEntity<Object> getAssetType(@PathVariable Long countryId, @PathVariable Long organizationId, @PathVariable BigInteger id) {
        if (id == null) {
            return ResponseHandler.invalidResponse(HttpStatus.BAD_REQUEST, false, "id cannot be null");
        }
        if (countryId == null) {
            return ResponseHandler.invalidResponse(HttpStatus.BAD_REQUEST, false, "country id can't be null");
        }
        if (organizationId == null) {
            return ResponseHandler.invalidResponse(HttpStatus.BAD_REQUEST, false, "organization id can't be null");
        }
        return ResponseHandler.generateResponse(HttpStatus.OK, true, assetTypeService.getAssetTypeById(countryId, organizationId, id));

    }


    @ApiOperation("get all AssetType ")
    @GetMapping("/asset_type/all")
    public ResponseEntity<Object> getAllAssetType(@PathVariable Long countryId, @PathVariable Long organizationId) {
        if (countryId == null) {
            return ResponseHandler.invalidResponse(HttpStatus.BAD_REQUEST, false, "country id can't be null");
        }
        if (organizationId == null) {
            return ResponseHandler.invalidResponse(HttpStatus.BAD_REQUEST, false, "organization id can't be null");
        }
        return ResponseHandler.generateResponse(HttpStatus.OK, true, assetTypeService.getAllAssetType(countryId, organizationId));

    }



    @ApiOperation("get AssetType by name")
    @GetMapping("/asset_type/name")
    public ResponseEntity<Object> getAssetTypeByName(@PathVariable Long countryId, @PathVariable Long organizationId, @RequestParam String name) {
        if (countryId == null) {
            return ResponseHandler.invalidResponse(HttpStatus.BAD_REQUEST, false, "country id can't be null");
        } else if (organizationId == null) {
            return ResponseHandler.invalidResponse(HttpStatus.BAD_REQUEST, false, "organization id can't be null");

        }
        return ResponseHandler.generateResponse(HttpStatus.OK, true, assetTypeService.getAssetTypeByName(countryId, organizationId, name));

    }


    @ApiOperation("delete AssetType  by id")
    @DeleteMapping("/asset_type/delete/{id}")
    public ResponseEntity<Object> deleteAssetTypeById(@PathVariable Long countryId, @PathVariable Long organizationId, @PathVariable BigInteger id) {
        Map<String,Object> result=assetTypeService.deleteAssetType(countryId, organizationId, id);
        if ((Boolean) result.get("isSuccess")){
            return ResponseHandler.generateResponse(HttpStatus.OK, true, true);
        }
        return ResponseHandler.generateResponse(HttpStatus.OK,false,result);

    }

    @ApiOperation("update subAsset by id")
    @PutMapping("/asset_type/update/{id}")
    public ResponseEntity<Object> updateAssetType(@PathVariable Long countryId, @PathVariable Long organizationId, @PathVariable BigInteger id, @Valid @RequestBody AssetTypeDTO assetType) {
        if (id == null) {
            return ResponseHandler.invalidResponse(HttpStatus.BAD_REQUEST, false, "id cannot be null");
        }
        if (countryId == null) {
            return ResponseHandler.invalidResponse(HttpStatus.BAD_REQUEST, false, "country id can't be null");
        }
        if (organizationId == null) {
            return ResponseHandler.invalidResponse(HttpStatus.BAD_REQUEST, false, "organization id can't be null");

        }
        return ResponseHandler.generateResponse(HttpStatus.OK, true, assetTypeService.updateAssetTypeUpdateAndCreateNewSubAssetsAndAddToAssetType(countryId, organizationId, id, assetType));

    }

    @ApiOperation("get all AssetType ")
    @GetMapping(UNIT_URL+"/asset_type/all")
    public ResponseEntity<Object> getAllAssetTypeOfUnit(@PathVariable Long countryId, @PathVariable Long unitId) {
        if (countryId == null) {
            return ResponseHandler.invalidResponse(HttpStatus.BAD_REQUEST, false, "country id can't be null");
        }
        if (unitId == null) {
            return ResponseHandler.invalidResponse(HttpStatus.BAD_REQUEST, false, "organization id can't be null");
        }
        return ResponseHandler.generateResponse(HttpStatus.OK, true, assetTypeService.getAllAssetType(countryId, unitId));

    }


    @ApiOperation("get AssetType by id")
    @GetMapping(UNIT_URL+"/asset_type/{id}")
    public ResponseEntity<Object> getAssetTypeOfUnitById(@PathVariable Long countryId, @PathVariable Long unitId, @PathVariable BigInteger id) {
        if (id == null) {
            return ResponseHandler.invalidResponse(HttpStatus.BAD_REQUEST, false, "id cannot be null");
        }
        if (countryId == null) {
            return ResponseHandler.invalidResponse(HttpStatus.BAD_REQUEST, false, "country id can't be null");
        }
        if (unitId == null) {
            return ResponseHandler.invalidResponse(HttpStatus.BAD_REQUEST, false, "organization id can't be null");
        }
        return ResponseHandler.generateResponse(HttpStatus.OK, true, assetTypeService.getAssetTypeById(countryId, unitId, id));

    }

}
