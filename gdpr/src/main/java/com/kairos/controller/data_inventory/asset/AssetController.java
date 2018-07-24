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
            return ResponseHandler.invalidResponse(HttpStatus.BAD_REQUEST, false, "Organization id can't be Null");
        }
        return ResponseHandler.generateResponse(HttpStatus.OK, true, assetService.addBasicAssetDetail(countryId, organizationId, assetDto));
    }


    @ApiOperation(value = "get history of asset or changes done in Asset")
    @GetMapping("/asset/{assetId}/history")
    public ResponseEntity<Object> getHistoryOrDataAuditOfAsset(@PathVariable Long countryId, @PathVariable Long organizationId, @PathVariable BigInteger assetId) {
        if (countryId == null) {
            return ResponseHandler.invalidResponse(HttpStatus.BAD_REQUEST, false, "Country id can't be Null");
        }
        if (organizationId == null) {
            return ResponseHandler.invalidResponse(HttpStatus.BAD_REQUEST, false, "Organization id can't be Null");
        }
        if (assetId == null) {
            return ResponseHandler.invalidResponse(HttpStatus.BAD_REQUEST, false, "Asset  id can't be Null");
        }
        return ResponseHandler.generateResponse(HttpStatus.OK, true, assetService.getAssetActivities(countryId, organizationId, assetId));
    }


}
