package com.kairos.controller.asset;


import com.kairos.persistance.model.asset.Asset;
import com.kairos.service.asset.AssetService;
import com.kairos.utils.ResponseHandler;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.inject.Inject;

import java.util.Optional;

import static com.kairos.constant.ApiConstant.API_ASSET_URL;

@RestController
@RequestMapping(API_ASSET_URL)
@Api(API_ASSET_URL)
public class AssetController {


    @Inject
    private AssetService assetService;


    @ApiOperation(value = "add asset")
    @RequestMapping(value = "/add_asset", method = RequestMethod.POST)
    public ResponseEntity<Object> addAsset(Asset asset) {
        if (!Optional.ofNullable(asset).isPresent()) {
            return ResponseHandler.invalidResponse(HttpStatus.BAD_REQUEST, false, "Null or Asset Empty data");
        }

        return ResponseHandler.generateResponse(HttpStatus.OK, true, assetService.addAsset(asset));
    }

    @ApiOperation(value = "get all asset")
    @RequestMapping(value = "/getAll", method = RequestMethod.GET)
    public ResponseEntity<Object> getAllAsset() {
        return ResponseHandler.generateResponse(HttpStatus.OK, true, assetService.getAllAsset());
    }

    @ApiOperation(value = "update asset by id")
    @PutMapping("/update/{id}")
public ResponseEntity<Object> updateAsset(@PathVariable  Long id,@RequestBody  Asset asset)
    {
return ResponseHandler.generateResponse(HttpStatus.OK,true,assetService.updateAsset(id,asset));
    }

    @ApiOperation(value = "delete asset")
    @DeleteMapping("/delete/asset/{id}")
    public ResponseEntity<Object> deleteAssetById(@PathVariable  Long id)
    {
        return ResponseHandler.generateResponse(HttpStatus.OK,true,assetService.deleteAssetById(id));
    }





}
