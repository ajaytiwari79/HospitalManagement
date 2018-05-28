package com.kairos.controller.master_data_management.asset_management;


import com.kairos.dto.MasterAssetDto;
import com.kairos.service.master_data_management.asset_management.MasterAssetService;
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

import static com.kairos.constant.ApiConstant.API_MASTER_ASSET_URL;
import static com.kairos.constant.ApiConstant.COUNTRY_URL;

/*
 *
 *  created by bobby 11/5/2018
 * */


@RestController
@RequestMapping(API_MASTER_ASSET_URL)
@Api(API_MASTER_ASSET_URL)
@CrossOrigin
public class MasterAssetController {

    private static final Logger LOGGER = LoggerFactory.getLogger(MasterAssetController.class);

    @Inject
    private MasterAssetService masterAssetService;


    @ApiOperation(value = "add master asset")
    @RequestMapping(value = "/add_asset", method = RequestMethod.POST)
    public ResponseEntity<Object> addMasterAsset(@PathVariable Long countryId, @Validated @RequestBody MasterAssetDto masterAssetDto) {
        if (countryId == null) {
            return ResponseHandler.invalidResponse(HttpStatus.BAD_REQUEST, false, "country id is null");
        }
        return ResponseHandler.generateResponse(HttpStatus.OK, true, masterAssetService.addMasterAsset(countryId, masterAssetDto));
    }

    @ApiOperation(value = "get all master asset")
    @RequestMapping(value = "/all", method = RequestMethod.GET)
    public ResponseEntity<Object> getAllMasterAsset() {
        return ResponseHandler.generateResponse(HttpStatus.OK, true, masterAssetService.getAllMasterAsset());
    }

    @ApiOperation(value = "update master asset by id")
    @PutMapping("/update/{id}")
    public ResponseEntity<Object> updateMasterAsset(@PathVariable BigInteger id, @RequestBody MasterAssetDto asset) {

        if (id != null) {
            return ResponseHandler.generateResponse(HttpStatus.OK, true, masterAssetService.updateMasterAsset(id, asset));

        }
        return ResponseHandler.invalidResponse(HttpStatus.BAD_GATEWAY, false, "id cannot be null");
    }


    @ApiOperation(value = "delete master asset")
    @DeleteMapping("/delete/{id}")
    public ResponseEntity<Object> deleteMasterAsset(@PathVariable BigInteger id) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true, masterAssetService.deleteMasterAsset(id));
    }

    @ApiOperation(value = "get master asset by id")
    @GetMapping("/{id}")
    public ResponseEntity<Object> getMasterAsset(@PathVariable Long countryId, @PathVariable BigInteger id) {
        if (id == null) {
            return ResponseHandler.invalidResponse(HttpStatus.BAD_REQUEST, false, "id is null");
        } else if (countryId == null) {
            return ResponseHandler.invalidResponse(HttpStatus.BAD_REQUEST, false, "country id is null");

        } else
            return ResponseHandler.invalidResponse(HttpStatus.BAD_GATEWAY, false, "id cannot be null");
    }


}
