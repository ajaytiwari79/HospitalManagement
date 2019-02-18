package com.kairos.controller.master_data.asset_management;


import com.kairos.dto.gdpr.master_data.MasterAssetDTO;
import com.kairos.enums.gdpr.SuggestedDataStatus;
import com.kairos.service.master_data.asset_management.MasterAssetService;
import com.kairos.utils.ResponseHandler;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.inject.Inject;
import java.util.Optional;
import java.util.Set;

import static com.kairos.constants.ApiConstant.API_ORGANIZATION_COUNTRY_URL;


/*
 *
 *  created by bobby 11/5/2018
 * */


@RestController
@RequestMapping(API_ORGANIZATION_COUNTRY_URL)
@Api(API_ORGANIZATION_COUNTRY_URL)
class MasterAssetController {

    private static final Logger LOGGER = LoggerFactory.getLogger(MasterAssetController.class);

    @Inject
    private MasterAssetService masterAssetService;


    @ApiOperation(value = "add master asset")
    @PostMapping("/master_asset/add_asset")
    public ResponseEntity<Object> addMasterAsset(@PathVariable Long countryId, @Validated @RequestBody MasterAssetDTO masterAssetDto) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true, masterAssetService.addMasterAsset(countryId, masterAssetDto));
    }

    @ApiOperation(value = "get all master asset")
    @GetMapping("/master_asset/all")
    public ResponseEntity<Object> getAllMasterAsset(@PathVariable Long countryId) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true, masterAssetService.getAllMasterAsset(countryId));
    }

    @ApiOperation(value = "update master asset by id")
    @PutMapping("/master_asset/update/{id}")
    public ResponseEntity<Object> updateMasterAsset(@PathVariable Long countryId, @PathVariable Long id, @Validated @RequestBody MasterAssetDTO assetDTO) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true, masterAssetService.updateMasterAsset(countryId, id, assetDTO));
    }

    @ApiOperation(value = "delete master asset")
    @DeleteMapping("/master_asset/delete/{id}")
    public ResponseEntity<Object> deleteMasterAsset(@PathVariable Long countryId, @PathVariable Long id) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true, masterAssetService.deleteMasterAsset(countryId, id));
    }

    @ApiOperation(value = "get master asset by id")
    @GetMapping("/master_asset/{id}")
    public ResponseEntity<Object> getMasterAsset(@PathVariable Long countryId, @PathVariable Long id) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true, masterAssetService.getMasterAssetById(countryId, id));

    }

    @ApiOperation(value = "Update Suggest Status of Master Assets")
    @PutMapping("/master_asset/status")
    public ResponseEntity<Object> updateStatusOfMasterAssetByIds(@PathVariable Long countryId, @RequestBody Set<Long> assetIds, @RequestParam SuggestedDataStatus suggestedDataStatus) {
        if (CollectionUtils.isEmpty(assetIds)) {
            return ResponseHandler.invalidResponse(HttpStatus.BAD_REQUEST, false, "Hosting Provider is Not Selected");
        } else if (!Optional.ofNullable(suggestedDataStatus).isPresent()) {
            return ResponseHandler.invalidResponse(HttpStatus.BAD_REQUEST, false, "Suggested Status in Empty");
        }
        return ResponseHandler.generateResponse(HttpStatus.OK, true, masterAssetService.updateSuggestedStatusOfMasterAsset(countryId, assetIds, suggestedDataStatus));

    }


}
