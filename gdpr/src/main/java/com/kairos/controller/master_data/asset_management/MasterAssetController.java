package com.kairos.controller.master_data.asset_management;


import com.kairos.gdpr.master_data.MasterAssetDTO;
import com.kairos.service.master_data.asset_management.MasterAssetService;
import com.kairos.utils.ResponseHandler;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.inject.Inject;
import java.math.BigInteger;

import static com.kairos.constants.ApiConstant.API_ORGANIZATION_URL;
import static com.kairos.constants.ApiConstant.UNIT_URL;


/*
 *
 *  created by bobby 11/5/2018
 * */


@RestController
@RequestMapping(API_ORGANIZATION_URL)
@Api(API_ORGANIZATION_URL)
public class MasterAssetController {

    private static final Logger LOGGER = LoggerFactory.getLogger(MasterAssetController.class);

    @Inject
    private MasterAssetService masterAssetService;


    @ApiOperation(value = "add master asset")
    @PostMapping( "/master_asset/add_asset")
    public ResponseEntity<Object> addMasterAsset(@PathVariable Long countryId, @PathVariable Long organizationId, @Validated @RequestBody MasterAssetDTO masterAssetDto) {
        if (countryId == null) {
            return ResponseHandler.invalidResponse(HttpStatus.BAD_REQUEST, false, "country id can't be null");
        }  else if (organizationId == null) {
            return ResponseHandler.invalidResponse(HttpStatus.BAD_REQUEST, false, "organization id can't be null");

        }
        return ResponseHandler.generateResponse(HttpStatus.OK, true, masterAssetService.addMasterAsset(countryId, organizationId, masterAssetDto));
    }

    @ApiOperation(value = "get all master asset")
    @GetMapping("/master_asset/all")
    public ResponseEntity<Object> getAllMasterAsset(@PathVariable Long countryId, @PathVariable Long organizationId) {
        if (countryId == null) {
            return ResponseHandler.invalidResponse(HttpStatus.BAD_REQUEST, false, "country id can't be null");
        }else if (organizationId == null) {
            return ResponseHandler.invalidResponse(HttpStatus.BAD_REQUEST, false, "organization id can't be null");
        }
        return ResponseHandler.generateResponse(HttpStatus.OK, true, masterAssetService.getAllMasterAsset(countryId, organizationId));
    }

    @ApiOperation(value = "update master asset by id")
    @PutMapping("/master_asset/update/{id}")
    public ResponseEntity<Object> updateMasterAsset(@PathVariable Long countryId, @PathVariable Long organizationId, @PathVariable BigInteger id, @Validated @RequestBody MasterAssetDTO assetDTO) {

        if (id == null) {
            return ResponseHandler.invalidResponse(HttpStatus.BAD_REQUEST, false, "id cannot be null");
        } if (countryId == null) {
            return ResponseHandler.invalidResponse(HttpStatus.BAD_REQUEST, false, "country id can't be null");
        }  if (organizationId == null) {
            return ResponseHandler.invalidResponse(HttpStatus.BAD_REQUEST, false, "organization id can't be null");
        }
        return ResponseHandler.generateResponse(HttpStatus.OK, true, masterAssetService.updateMasterAsset(countryId,organizationId,id,assetDTO));
    }


    @ApiOperation(value = "delete master asset")
    @DeleteMapping("/master_asset/delete/{id}")
    public ResponseEntity<Object> deleteMasterAsset(@PathVariable Long countryId, @PathVariable Long organizationId, @PathVariable BigInteger id) {
        if (id == null) {
            return ResponseHandler.invalidResponse(HttpStatus.BAD_REQUEST, false, "id cannot be null");
        } if (countryId == null) {
            return ResponseHandler.invalidResponse(HttpStatus.BAD_REQUEST, false, "country id can't be null");
        }  if (organizationId == null) {
            return ResponseHandler.invalidResponse(HttpStatus.BAD_REQUEST, false, "organization id can't be null");

        }
        return ResponseHandler.generateResponse(HttpStatus.OK, true, masterAssetService.deleteMasterAsset(countryId, organizationId, id));
    }

    @ApiOperation(value = "get master asset by id")
    @GetMapping("/master_asset/{id}")
    public ResponseEntity<Object> getMasterAsset(@PathVariable Long countryId,@PathVariable Long organizationId, @PathVariable BigInteger id) {
        if (id == null) {
            return ResponseHandler.invalidResponse(HttpStatus.BAD_REQUEST, false, "id cannot be null");
        } if (countryId == null) {
            return ResponseHandler.invalidResponse(HttpStatus.BAD_REQUEST, false, "country id can't be null");
        }  if (organizationId == null) {
            return ResponseHandler.invalidResponse(HttpStatus.BAD_REQUEST, false, "organization id can't be null");
        }
        return ResponseHandler.generateResponse(HttpStatus.OK, true, masterAssetService.getMasterAssetById(countryId,organizationId,id));

    }


    @ApiOperation(value = "get master asset of Unit by id")
    @GetMapping(UNIT_URL+"/master_asset/{id}")
    public ResponseEntity<Object> getMasterAssetOfUnitById(@PathVariable Long countryId,@PathVariable Long unitId, @PathVariable BigInteger id) {
        if (id == null) {
            return ResponseHandler.invalidResponse(HttpStatus.BAD_REQUEST, false, "id cannot be null");
        }
        if (countryId == null) {
            return ResponseHandler.invalidResponse(HttpStatus.BAD_REQUEST, false, "country id can't be null");
        }
        if (unitId == null) {
            return ResponseHandler.invalidResponse(HttpStatus.BAD_REQUEST, false, "unitId id can't be null");
        }
        return ResponseHandler.generateResponse(HttpStatus.OK, true, masterAssetService.getMasterAssetById(countryId,unitId,id));

    }



    @ApiOperation(value = "get all master asset of Unit")
    @GetMapping(UNIT_URL+"/master_asset/all")
    public ResponseEntity<Object> getAllMasterAssetOfUnit(@PathVariable Long countryId, @PathVariable Long unitId) {
        if (countryId == null) {
            return ResponseHandler.invalidResponse(HttpStatus.BAD_REQUEST, false, "country id can't be null");
        }else if (unitId == null) {
            return ResponseHandler.invalidResponse(HttpStatus.BAD_REQUEST, false, "unitId id can't be null");

        }
        return ResponseHandler.generateResponse(HttpStatus.OK, true, masterAssetService.getAllMasterAsset(countryId, unitId));
    }

}
