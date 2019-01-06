package com.kairos.controller.staff;

import com.kairos.persistence.model.staff.StaffFilterDTO;
import com.kairos.service.staff.StaffFilterService;
import com.kairos.utils.response.ResponseHandler;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.inject.Inject;
import java.util.Map;

import static com.kairos.constants.ApiConstants.API_ORGANIZATION_URL;
import static com.kairos.constants.ApiConstants.UNIT_URL;

/**
 * Created by prerna on 27/4/18.
 */
@RestController
@RequestMapping(API_ORGANIZATION_URL)
@Api(API_ORGANIZATION_URL)
public class StaffFilterController {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Inject
    private StaffFilterService filterService;

    @RequestMapping(value = "/filter", method = RequestMethod.POST)
    @ApiOperation("To add favourite filters")
    // @PreAuthorize("@customPermissionEvaluator.isAuthorized()")
    public ResponseEntity<Map<String, Object>> addFavouriteFilter(@RequestBody StaffFilterDTO staffFilterDTO,@PathVariable Long organizationId) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true, filterService.addFavouriteFilter(organizationId, staffFilterDTO));
    }

    @RequestMapping(value = "/filter/{filterId}", method = RequestMethod.PUT)
    @ApiOperation("To update favourite filters ")
    // @PreAuthorize("@customPermissionEvaluator.isAuthorized()")
    public ResponseEntity<Map<String, Object>> updateFavouriteFilter(@PathVariable Long filterId,@PathVariable Long organizationId, @RequestBody StaffFilterDTO staffFilterDTO) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true, filterService.updateFavouriteFilter(filterId, organizationId, staffFilterDTO));
    }

    @RequestMapping(value = UNIT_URL+"/all_filter/{moduleId}", method = RequestMethod.GET)
    @ApiOperation("To get all and favourite filters")
    // @PreAuthorize("@customPermissionEvaluator.isAuthorized()")
    public ResponseEntity<Map<String, Object>> getAllAndFavouriteFilters(@PathVariable long organizationId, @PathVariable long unitId, @PathVariable String moduleId) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true, filterService.getAllAndFavouriteFilters(moduleId,  unitId));
    }

    @RequestMapping(value = "/filter/{filterId}", method = RequestMethod.DELETE)
    @ApiOperation("To delete favourite filters ")
    // @PreAuthorize("@customPermissionEvaluator.isAuthorized()")
    public ResponseEntity<Map<String, Object>> deleteFavouriteFilter(@PathVariable Long filterId,@PathVariable Long organizationId) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true, filterService.deleteFavouriteFilter(filterId, organizationId));
    }

    @RequestMapping(value = UNIT_URL+"/staff_list_with_filter", method = RequestMethod.POST)
    @ApiOperation("Get All staff List available in Org")
    //@PreAuthorize("@customPermissionEvaluator.isAuthorized()")
    public ResponseEntity<Map<String, Object>> getAllStaffByUnitId(@PathVariable long unitId, @RequestBody StaffFilterDTO staffFilterDTO,@RequestParam String moduleId) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true, filterService.getAllStaffByUnitId(unitId, staffFilterDTO,moduleId));
    }

}
