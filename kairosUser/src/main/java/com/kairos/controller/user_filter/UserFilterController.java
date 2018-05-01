package com.kairos.controller.user_filter;

import com.kairos.persistence.model.user.staff.StaffFilterDTO;
import com.kairos.service.user_filter.UserFilterService;
import com.kairos.util.response.ResponseHandler;
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
public class UserFilterController {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Inject
    private UserFilterService filterService;

    /*@RequestMapping(value = "/addStaffFavouriteFilters", method = RequestMethod.POST)
    @ApiOperation("verify staff has unit employment in unit or not ")
    // @PreAuthorize("@customPermissionEvaluator.isAuthorized()")
    public ResponseEntity<Map<String, Object>> addStaffFavouriteFilters(@RequestBody StaffFilterDTO staffFilterDTO, @PathVariable long organizationId) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true, filterService.addStaffFavouriteFilters(staffFilterDTO,organizationId));
    }
    @RequestMapping(value = "/updateStaffFavouriteFilters", method = RequestMethod.POST)
    @ApiOperation("verify staff has unit employment in unit or not ")
    // @PreAuthorize("@customPermissionEvaluator.isAuthorized()")
    public ResponseEntity<Map<String, Object>> updateStaffFavouriteFilters(@PathVariable long organizationId, @RequestBody StaffFilterDTO staffFilterDTO) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true, filterService.updateStaffFavouriteFilters(staffFilterDTO,organizationId));
    }
    @RequestMapping(value = "/removeStaffFavouriteFilters/{staffFavouriteFilterId}", method = RequestMethod.DELETE)
    @ApiOperation("verify staff has unit employment in unit or not ")
    // @PreAuthorize("@customPermissionEvaluator.isAuthorized()")
    public ResponseEntity<Map<String, Object>> removeStaffFavouriteFilters(@PathVariable Long staffFavouriteFilterId,@PathVariable long organizationId) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true, filterService.removeStaffFavouriteFilters(staffFavouriteFilterId,organizationId));
    }
    @RequestMapping(value = "/getStaffFavouriteFilters/{moduleId}", method = RequestMethod.GET)
    @ApiOperation("verify staff has unit employment in unit or not ")
    // @PreAuthorize("@customPermissionEvaluator.isAuthorized()")
    public ResponseEntity<Map<String, Object>> getStaffFavouriteFilters(@PathVariable long organizationId, @PathVariable String moduleId) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true, filterService.getStaffFavouriteFilters(moduleId, organizationId));
    }
    @RequestMapping(value = "/staff/filter", method = RequestMethod.GET)
    @ApiOperation("Get all applicable and favourite filters ")
    // @PreAuthorize("@customPermissionEvaluator.isAuthorized()")
    public ResponseEntity<Map<String, Object>> getAllFilters(@PathVariable long organizationId, @PathVariable String moduleId) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true, filterService.getStaffFavouriteFilters(moduleId, organizationId));
    }*/
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
    @RequestMapping(value = "/all_filter/{moduleId}", method = RequestMethod.GET)
    @ApiOperation("To get all and favourite filters")
    // @PreAuthorize("@customPermissionEvaluator.isAuthorized()")
    public ResponseEntity<Map<String, Object>> getAllAndFavouriteFilters(@PathVariable long organizationId, @PathVariable String moduleId) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true, filterService.getAllAndFavouriteFilters(moduleId, organizationId));
    }

    /*@RequestMapping(value = "/filter/{filterId}", method = RequestMethod.DELETE)
    @ApiOperation("verify staff has unit employment in unit or not ")
    // @PreAuthorize("@customPermissionEvaluator.isAuthorized()")
    public ResponseEntity<Map<String, Object>> removeStaffFavouriteFilters(@PathVariable Long staffFavouriteFilterId,@PathVariable long organizationId) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true, staffService.removeStaffFavouriteFilters(staffFavouriteFilterId,organizationId));
    }*/


}