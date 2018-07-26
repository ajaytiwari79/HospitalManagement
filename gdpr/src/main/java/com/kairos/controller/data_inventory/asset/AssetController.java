package com.kairos.controller.data_inventory.asset;


import com.kairos.dto.data_inventory.AssetDTO;
import com.kairos.service.data_inventory.AssetService;
import com.kairos.utils.ResponseHandler;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.inject.Inject;
import javax.validation.Valid;
import java.math.BigInteger;

import static com.kairos.constants.ApiConstant.API_ORGANIZATION_URL;

@RestController
@RequestMapping(API_ORGANIZATION_URL)
@Api(API_ORGANIZATION_URL)
public class AssetController {


    @Inject
    private AssetService assetService;


    @ApiOperation(value = "create asset for organization with basic detail")
    @PostMapping("/asset")
    public ResponseEntity<Object> createAssetWithBasicDetail(@PathVariable Long countryId, @PathVariable Long organizationId, @Valid @RequestBody AssetDTO assetDto) {

        if (countryId == null) {
            return ResponseHandler.invalidResponse(HttpStatus.BAD_REQUEST, false, "Country id can't be Null");
        } else if (organizationId == null) {
            return ResponseHandler.invalidResponse(HttpStatus.BAD_REQUEST, false, "ManagingOrganization id can't be Null");
        }
        return ResponseHandler.generateResponse(HttpStatus.OK, true, assetService.createAsseWithBasictDetail(countryId, organizationId, assetDto));
    }


    @ApiOperation(value = "delete  asset by Id")
    @DeleteMapping("/asset/{assetId}")
    public ResponseEntity<Object> deleteAssetById(@PathVariable Long countryId, @PathVariable Long organizationId, @PathVariable BigInteger assetId) {

        if (countryId == null) {
            return ResponseHandler.invalidResponse(HttpStatus.BAD_REQUEST, false, "Country id can't be Null");
        } else if (organizationId == null) {
            return ResponseHandler.invalidResponse(HttpStatus.BAD_REQUEST, false, "ManagingOrganization id can't be Null");
        }
        return ResponseHandler.generateResponse(HttpStatus.OK, true, assetService.deleteAssetById(countryId, organizationId, assetId));
    }



    @ApiOperation(value = "update asset basic detail")
    @PutMapping("/asset/update/{assetId}")
    public ResponseEntity<Object> updateAssetData(@PathVariable Long countryId, @PathVariable Long organizationId,@PathVariable BigInteger assetId, @Valid @RequestBody AssetDTO assetDto) {

        if (countryId == null) {
            return ResponseHandler.invalidResponse(HttpStatus.BAD_REQUEST, false, "Country id can't be Null");
        } else if (organizationId == null) {
            return ResponseHandler.invalidResponse(HttpStatus.BAD_REQUEST, false, "ManagingOrganization id can't be Null");
        }
        return ResponseHandler.generateResponse(HttpStatus.OK, true, assetService.updateAssetData(countryId, organizationId,assetId, assetDto));
    }





    @ApiOperation(value = "Get Asset With meta data by Id")
    @GetMapping("/asset/{assetId}")
    public ResponseEntity<Object> getAssetWithMetaDatabyId(@PathVariable Long countryId, @PathVariable Long organizationId,@PathVariable BigInteger assetId) {

        if (countryId == null) {
            return ResponseHandler.invalidResponse(HttpStatus.BAD_REQUEST, false, "Country id can't be Null");
        } else if (organizationId == null) {
            return ResponseHandler.invalidResponse(HttpStatus.BAD_REQUEST, false, "ManagingOrganization id can't be Null");
        }
        return ResponseHandler.generateResponse(HttpStatus.OK, true, assetService.getAssetWithMetadataById(countryId, organizationId,assetId));
    }




    @ApiOperation(value = "Get All Asset With meta data ")
    @GetMapping("/asset")
    public ResponseEntity<Object> getAllAssetWithMetaData(@PathVariable Long countryId, @PathVariable Long organizationId,@PathVariable BigInteger assetId) {

        if (countryId == null) {
            return ResponseHandler.invalidResponse(HttpStatus.BAD_REQUEST, false, "Country id can't be Null");
        } else if (organizationId == null) {
            return ResponseHandler.invalidResponse(HttpStatus.BAD_REQUEST, false, "ManagingOrganization id can't be Null");
        }
        return ResponseHandler.generateResponse(HttpStatus.OK, true, assetService.getAllAssetWithMetadata(countryId, organizationId));
    }




    @ApiOperation(value = "get history of asset or changes done in Asset")
    @GetMapping("/asset/{assetId}/history")
    public ResponseEntity<Object> getHistoryOrDataAuditOfAsset( @PathVariable BigInteger assetId)  throws ClassNotFoundException {

        if (assetId == null) {
            return ResponseHandler.invalidResponse(HttpStatus.BAD_REQUEST, false, "Asset  id can't be Null");
        }
        return ResponseHandler.generateResponse(HttpStatus.OK, true, assetService.getAssetActivities( assetId));
    }


}
