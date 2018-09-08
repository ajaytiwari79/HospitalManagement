package com.kairos.controller.filter;


import com.kairos.dto.gdpr.FilterSelectionDTO;
import com.kairos.service.filter.FilterService;
import com.kairos.utils.ResponseHandler;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.lang3.StringUtils;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import static com.kairos.constants.ApiConstant.API_ORGANIZATION_URL;


import javax.inject.Inject;
import javax.validation.Valid;

@RestController
@RequestMapping(API_ORGANIZATION_URL)
@Api(API_ORGANIZATION_URL)
public class FilterController {


    @Inject
    private FilterService filterService;


    /**
     * @param countryId
     * @param moduleId  is required to get filter group with contain Filter types on which filtering can apply
     * @return
     */

    @ApiOperation("get category or values of Properties on which filter apply by module id")
    @GetMapping("/filter/category/{moduleId}")
    public ResponseEntity<Object> getFilterData(@PathVariable Long countryId, @PathVariable Long organizationId, @PathVariable String moduleId) {

        if (countryId == null) {
            return ResponseHandler.invalidResponse(HttpStatus.BAD_REQUEST, false, "country id can't be null");
        }
        if (organizationId == null) {
            return ResponseHandler.invalidResponse(HttpStatus.BAD_REQUEST, false, "organization id can't be null");
        }
        if (StringUtils.isBlank(moduleId)) {
            return ResponseHandler.invalidResponse(HttpStatus.BAD_REQUEST, false, "module id is empty or null");
        }
        return ResponseHandler.generateResponse(HttpStatus.OK, true, filterService.getFilterCategories(countryId, organizationId, moduleId));
    }


    /**
     * @param countryId
     * @param moduleId           is require to get Filter types from filter group
     * @param filterSelectionDto contain List of id and and Filter type filtering data
     * @return Filter data on the basis of filter type selection and Ids
     */
    @ApiOperation("get  filter data by filter selection value")
    @PostMapping("/filter/data/{moduleId}")
    public ResponseEntity<Object> getMetaDataFilterResult(@PathVariable Long countryId, @PathVariable Long organizationId, @PathVariable String moduleId, @Valid @RequestBody FilterSelectionDTO filterSelectionDto) {

        if (countryId == null) {
            return ResponseHandler.invalidResponse(HttpStatus.BAD_REQUEST, false, "country id can't be null");
        }
        if (organizationId == null) {
            return ResponseHandler.invalidResponse(HttpStatus.BAD_REQUEST, false, "organization id can't be null");
        }
        if (StringUtils.isBlank(moduleId)) {
            return ResponseHandler.invalidResponse(HttpStatus.BAD_REQUEST, true, "module id is empty or null");
        }
        return ResponseHandler.generateResponse(HttpStatus.OK, true, filterService.getFilterDataWithFilterSelection(countryId, organizationId, moduleId, filterSelectionDto).getData());
    }


}
