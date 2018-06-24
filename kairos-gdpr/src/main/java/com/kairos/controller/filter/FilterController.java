package com.kairos.controller.filter;


import com.kairos.dto.FilterSelectionDTO;
import com.kairos.service.filter.FilterService;
import com.kairos.utils.ResponseHandler;
import io.swagger.annotations.Api;
import org.apache.commons.lang3.StringUtils;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import static com.kairos.constants.ApiConstant.API_FILTER;

import javax.inject.Inject;
import javax.validation.Valid;

@RestController
@RequestMapping(API_FILTER)
@Api(API_FILTER)
public class FilterController {


    @Inject
    private FilterService filterService;


    /**
     *
     * @param countryId
     * @param moduleId is required to get filter group with contain Filter types on which filtering can apply
     * @return
     */
    @GetMapping("/category/{moduleId}")
    public ResponseEntity<Object> getFilterData(@PathVariable Long countryId,@PathVariable Long organizationId, @PathVariable String moduleId) {

        if (countryId != null) {
            return ResponseHandler.generateResponse(HttpStatus.OK, true, filterService.getFilterCategories(countryId,organizationId, moduleId));
        } else if (StringUtils.isBlank(moduleId)) {
            return ResponseHandler.invalidResponse(HttpStatus.BAD_REQUEST, false, "module id is empty or null");

        }
        return ResponseHandler.invalidResponse(HttpStatus.BAD_GATEWAY, false, "countryId cannot be null");
    }


    /**
     *
     * @param countryId
     * @param moduleId is require to get Filter types from filter group
     * @param filterSelectionDto contain List of id and and Filter type filtering data
     * @return Filter data on the basis of filter type selection and Ids
     */
    @PostMapping("/data/{moduleId}")
    public ResponseEntity<Object> getMetaDataFilterResult(@PathVariable Long countryId,@PathVariable Long organizationId, @PathVariable String moduleId, @Valid @RequestBody FilterSelectionDTO filterSelectionDto) {

        if (countryId == null) {

            return ResponseHandler.invalidResponse(HttpStatus.BAD_GATEWAY, false, "countryId cannot be null");
        } else if (StringUtils.isBlank(moduleId)) {
            return ResponseHandler.invalidResponse(HttpStatus.BAD_REQUEST, true, "module id is empty or null");

        }
        return ResponseHandler.generateResponse(HttpStatus.OK, true, filterService.getFilterDataWithFilterSelection(countryId,organizationId,moduleId, filterSelectionDto).getData());


    }


}
