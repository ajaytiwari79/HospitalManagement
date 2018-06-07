package com.kairos.controller.filter;


import com.kairos.dto.FilterSelectionDto;
import com.kairos.service.filter.FilterService;
import com.kairos.utils.ResponseHandler;
import io.swagger.annotations.Api;
import org.apache.commons.lang3.StringUtils;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import static com.kairos.constant.ApiConstant.API_FILTER;

import javax.inject.Inject;
import javax.validation.Valid;

@RestController
@RequestMapping(API_FILTER)
@Api(API_FILTER)
public class FilterController {


    @Inject
    private FilterService filterService;

    @GetMapping("/category/{moduleId}")
    public ResponseEntity<Object> getFilterData(@PathVariable Long countryId, @PathVariable String moduleId) {

        if (countryId != null) {
            return ResponseHandler.generateResponse(HttpStatus.OK, true, filterService.getFilterCategories(countryId, moduleId));
        } else if (StringUtils.isBlank(moduleId)) {
            return ResponseHandler.invalidResponse(HttpStatus.BAD_REQUEST, false, "module id is empty or null");

        }
        return ResponseHandler.invalidResponse(HttpStatus.BAD_GATEWAY, false, "countryId cannot be null");
    }

    @PostMapping("/data/{moduleId}")
    public ResponseEntity<Object> getMetaDataFilterResult(@PathVariable Long countryId, @PathVariable String moduleId, @Valid @RequestBody FilterSelectionDto filterSelectionDto) {

        if (countryId == null) {

            return ResponseHandler.invalidResponse(HttpStatus.BAD_GATEWAY, false, "countryId cannot be null");
        } else if (StringUtils.isBlank(moduleId)) {
            return ResponseHandler.invalidResponse(HttpStatus.BAD_REQUEST, true, "module id is empty or null");

        }
        return ResponseHandler.generateResponse(HttpStatus.OK, true, filterService.getFilterDataWithFilterSelection(countryId, moduleId, filterSelectionDto).getData());


    }

    /*@PostMapping("/filter_data")
    public ResponseEntity<Object> getMasterAssetDataWithFilter(@PathVariable Long countryId,@RequestParam String moduleId, @RequestBody FilterSelectionDto filterSelectionDto) {

        if (countryId != null) {
            return ResponseHandler.generateResponse(HttpStatus.OK, true, filterService. getMasterAssetDataWithFilter(countryId,moduleId,filterSelectionDto));
        }
        return ResponseHandler.invalidResponse(HttpStatus.BAD_GATEWAY, false, "countryId cannot be null");

    }*/

}
