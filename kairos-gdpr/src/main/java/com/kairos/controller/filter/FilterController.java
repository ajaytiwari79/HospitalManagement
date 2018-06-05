package com.kairos.controller.filter;


import com.kairos.dto.FilterSelectionDto;
import com.kairos.service.filter.FilterService;
import com.kairos.service.master_data_management.asset_management.MasterAssetFilterService;
import com.kairos.utils.ResponseHandler;
import io.swagger.annotations.Api;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import static com.kairos.constant.ApiConstant.API_FILTER;

import javax.inject.Inject;

@RestController
@RequestMapping(API_FILTER)
@Api(API_FILTER)
public class FilterController {



    @Inject
    private FilterService filterService;

    @GetMapping("/category/{moduleId}")
    public ResponseEntity<Object> getMetaDataFilter(@PathVariable Long countryId, @PathVariable String moduleId) {

        if (countryId != null) {
            return ResponseHandler.generateResponse(HttpStatus.OK, true, filterService.metaDatafilters(moduleId,countryId));
        }
        return ResponseHandler.invalidResponse(HttpStatus.BAD_GATEWAY, false, "countryId cannot be null");

    }



    /*@PostMapping("/filter_data")
    public ResponseEntity<Object> getMasterAssetDataWithFilter(@PathVariable Long countryId,@RequestParam String moduleId, @RequestBody FilterSelectionDto filterSelectionDto) {

        if (countryId != null) {
            return ResponseHandler.generateResponse(HttpStatus.OK, true, filterService. getMasterAssetDataWithFilter(countryId,moduleId,filterSelectionDto));
        }
        return ResponseHandler.invalidResponse(HttpStatus.BAD_GATEWAY, false, "countryId cannot be null");

    }*/

}
