package com.kairos.controller.master_data_management.asset_management;


import com.kairos.dto.FilterSelectionDto;
import com.kairos.service.master_data_management.asset_management.MasterAssetFilterService;
import com.kairos.utils.ResponseHandler;
import io.swagger.annotations.Api;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.inject.Inject;

import static com.kairos.constant.ApiConstant.API_MASTER_ASSET_FILTER;


@RestController
@RequestMapping(API_MASTER_ASSET_FILTER)
@Api(API_MASTER_ASSET_FILTER)
public class MasterAssetFilterController {


    @Inject
    private MasterAssetFilterService masterAssetFilterService;

    @GetMapping("/category")
    public ResponseEntity<Object> getMasterAssetFilter(@PathVariable Long countryId,@RequestParam String moduleId) {

        if (countryId != null) {
            return ResponseHandler.generateResponse(HttpStatus.OK, true, masterAssetFilterService.masterAssetfilterQueryResult(countryId,moduleId,true));
        }
        return ResponseHandler.invalidResponse(HttpStatus.BAD_GATEWAY, false, "countryId cannot be null");

    }



    @PostMapping("/filter_data")
    public ResponseEntity<Object> getMasterAssetDataWithFilter(@PathVariable Long countryId,@RequestParam String moduleId, @RequestBody FilterSelectionDto filterSelectionDto) {

        if (countryId != null) {
            return ResponseHandler.generateResponse(HttpStatus.OK, true, masterAssetFilterService. getMasterAssetDataWithFilter(countryId,moduleId,filterSelectionDto));
        }
        return ResponseHandler.invalidResponse(HttpStatus.BAD_GATEWAY, false, "countryId cannot be null");

    }

}
