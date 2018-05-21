package com.kairos.controller.master_data_management.asset_management;


import com.kairos.dto.MasterAssetDto;
import com.kairos.service.master_data_management.asset_management.MasterAssetService;
import com.kairos.utils.ResponseHandler;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.inject.Inject;
import java.math.BigInteger;

import static com.kairos.constant.ApiConstant.API_MASTER_ASSET_URL;

@RestController
@RequestMapping(API_MASTER_ASSET_URL)
@Api(API_MASTER_ASSET_URL)
@CrossOrigin
public class MasterAssetController {


    @Inject
    private MasterAssetService masterAssetService;


    @ApiOperation(value = "add master asset")
    @RequestMapping(value = "/add_asset", method = RequestMethod.POST)
    public ResponseEntity<Object> addMasterAsset(@Validated @RequestBody MasterAssetDto masterAssetDto) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true, masterAssetService.addMasterAsset(masterAssetDto));
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
    @GetMapping("/id/{id}")
    public ResponseEntity<Object> getMasterAsset(@PathVariable BigInteger id) {
        if (id != null) {
            return ResponseHandler.generateResponse(HttpStatus.OK, true, masterAssetService.getMasterAssetById(id));
        }
        return ResponseHandler.invalidResponse(HttpStatus.BAD_GATEWAY, false, "id cannot be null");
    }


}
