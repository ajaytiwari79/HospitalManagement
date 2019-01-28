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

import static com.kairos.constants.ApiConstants.API_V1;
import static com.kairos.constants.ApiConstants.UNIT_URL;

/**
 * Created by prerna on 27/4/18.
 */
@RestController
@RequestMapping(API_V1)
@Api(API_V1)
public class StaffFilterController {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Inject
    private StaffFilterService filterService;

    @RequestMapping(value = UNIT_URL+"/filter", method = RequestMethod.POST)
    @ApiOperation("To add favourite filters")
    // @PreAuthorize("@customPermissionEvaluator.isAuthorized()")
    public ResponseEntity<Map<String, Object>> addFavouriteFilter(@RequestBody StaffFilterDTO staffFilterDTO,@PathVariable Long unitId) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true, filterService.addFavouriteFilter(unitId, staffFilterDTO));
    }

    @RequestMapping(value = UNIT_URL+"/filter/{filterId}", method = RequestMethod.PUT)
    @ApiOperation("To update favourite filters ")
    // @PreAuthorize("@customPermissionEvaluator.isAuthorized()")
    public ResponseEntity<Map<String, Object>> updateFavouriteFilter(@PathVariable Long filterId,@PathVariable Long unitId, @RequestBody StaffFilterDTO staffFilterDTO) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true, filterService.updateFavouriteFilter(filterId, unitId, staffFilterDTO));
    }

    @RequestMapping(value = UNIT_URL+"/all_filter/{moduleId}", method = RequestMethod.GET)
    @ApiOperation("To get all and favourite filters")
    // @PreAuthorize("@customPermissionEvaluator.isAuthorized()")
    public ResponseEntity<Map<String, Object>> getAllAndFavouriteFilters( @PathVariable long unitId, @PathVariable String moduleId) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true, filterService.getAllAndFavouriteFilters(moduleId,  unitId));
    }

    @RequestMapping(value = UNIT_URL+"/filter/{filterId}", method = RequestMethod.DELETE)
    @ApiOperation("To delete favourite filters ")
    // @PreAuthorize("@customPermissionEvaluator.isAuthorized()")
    public ResponseEntity<Map<String, Object>> deleteFavouriteFilter(@PathVariable Long filterId,@PathVariable Long unitId) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true, filterService.deleteFavouriteFilter(filterId, unitId));
    }

    @RequestMapping(value = UNIT_URL+"/staff_list_with_filter", method = RequestMethod.POST)
    @ApiOperation("Get All staff List available in Org")
    //@PreAuthorize("@customPermissionEvaluator.isAuthorized()")
    public ResponseEntity<Map<String, Object>> getAllStaffByUnitId(@PathVariable long unitId, @RequestBody StaffFilterDTO staffFilterDTO,@RequestParam String moduleId) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true, filterService.getAllStaffByUnitId(unitId, staffFilterDTO,moduleId));
    }

}
