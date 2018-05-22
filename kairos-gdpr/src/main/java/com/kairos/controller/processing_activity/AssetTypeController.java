package com.kairos.controller.processing_activity;


import com.kairos.service.processing_activity.AssetTypeService;
import com.kairos.utils.ResponseHandler;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.inject.Inject;
import java.math.BigInteger;

import static com.kairos.constant.ApiConstant.API_ASSET_TYPE_URL;

@RestController
@RequestMapping(API_ASSET_TYPE_URL)
@Api(API_ASSET_TYPE_URL)
public class AssetTypeController {


    @Inject
    private AssetTypeService assetTypeService;


    @ApiOperation("add AssetType")
    @PostMapping("/add")
    public ResponseEntity<Object> createAssetType(@RequestParam String assetType) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true, assetTypeService.createAssetType(assetType));

    }


    @ApiOperation("get AssetType by id")
    @GetMapping("/{id}")
    public ResponseEntity<Object> getAssetTypeById(@PathVariable BigInteger id) {
        if (id == null) {
            return ResponseHandler.invalidResponse(HttpStatus.BAD_REQUEST, false, "id is null");
        }
        return ResponseHandler.generateResponse(HttpStatus.OK, true, assetTypeService.getAssetTypeById(id));

    }


    @ApiOperation("get all AssetType ")
    @GetMapping("/all")
    public ResponseEntity<Object> getAllAssetType() {
        return ResponseHandler.generateResponse(HttpStatus.OK, true, assetTypeService.getAllAssetType());

    }


    @ApiOperation("delete AssetType  by id")
    @DeleteMapping("/delete/{id}")
    public ResponseEntity<Object> deleteAssetTypeById(@PathVariable BigInteger id) {
        if (id == null) {
            return ResponseHandler.invalidResponse(HttpStatus.BAD_REQUEST, false, "id is null");
        }

        return ResponseHandler.generateResponse(HttpStatus.OK, true, assetTypeService.deleteAssetTypeById(id));

    }

    @ApiOperation("update AssetType by id")
    @PutMapping("/update/{id}")
    public ResponseEntity<Object> updateAssetType(@PathVariable BigInteger id, @RequestParam String assetType) {

        if (id == null) {
            return ResponseHandler.invalidResponse(HttpStatus.BAD_REQUEST, false, "id is null");
        }
        return ResponseHandler.generateResponse(HttpStatus.OK, true, assetTypeService.updateAssetType(id, assetType));

    }


}
