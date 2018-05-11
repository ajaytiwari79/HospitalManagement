package com.kairos.controller.asset;


import com.kairos.persistance.model.asset.GlobalAsset;
import com.kairos.service.asset.GlobalAssetService;
import com.kairos.utils.ResponseHandler;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.inject.Inject;

import java.math.BigInteger;
import java.util.Optional;

import static com.kairos.constant.ApiConstant.API_ASSET_URL;

@RestController
@RequestMapping(API_ASSET_URL)
@Api(API_ASSET_URL)
@CrossOrigin
public class AssetController {


    @Inject
    private GlobalAssetService globalAssetService;


    @ApiOperation(value = "add global asset")
    @RequestMapping(value = "/global/add_asset", method = RequestMethod.POST)
    public ResponseEntity<Object> addAsset(@RequestBody GlobalAsset asset) {
          return ResponseHandler.generateResponse(HttpStatus.OK, true, globalAssetService.addAsset(asset));
    }

    @ApiOperation(value = "get all asset")
    @RequestMapping(value = "/global/all", method = RequestMethod.GET)
    public ResponseEntity<Object> getAllAsset() {
        return ResponseHandler.generateResponse(HttpStatus.OK, true, globalAssetService.getAllGlobalAsset());
    }

    @ApiOperation(value = "update asset by id")
    @PutMapping("/global/update/{id}")
    public ResponseEntity<Object> updateAsset(@PathVariable BigInteger id, @RequestBody GlobalAsset asset) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true, globalAssetService.updateAsset(id, asset));
    }

    @ApiOperation(value = "delete asset")
    @DeleteMapping("/global/delete/{id}")
    public ResponseEntity<Object> deleteAsset(@PathVariable BigInteger id) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true, globalAssetService.deleteAsset(id));
    }

    @ApiOperation(value = "get global asset by id")
    @GetMapping("/global/id/{id}")
    public ResponseEntity<Object> getGlobalAssetById(@PathVariable BigInteger id) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true, globalAssetService.getGlobalAssetById(id));
    }


}
