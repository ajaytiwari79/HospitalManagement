package com.kairos.controller.organization;

import com.kairos.service.organization.UnionService;
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

/**
 * Created by vipul on 13/2/18.
 */
@RestController

@RequestMapping(API_ORGANIZATION_URL)
@Api(API_ORGANIZATION_URL)

public class UnionController {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Inject
    private UnionService unionService;

    @RequestMapping(value = "/parent_unions/{countryId}", method = RequestMethod.GET)
    @ApiOperation("Get All Unions")
    // @PreAuthorize("@customPermissionEvaluator.isAuthorized()")
    public ResponseEntity<Map<String, Object>> getAllUnionOfCountry(@PathVariable long countryId) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true,
                unionService.getAllUnionOfCountry(countryId));
    }

  // TODO NEED TO IMPLEMENT IN FUTURE
//    @RequestMapping(value = UNIT_URL + "/unions", method = RequestMethod.GET)
//    @ApiOperation("Get All Unions by organization ")
//    // @PreAuthorize("@customPermissionEvaluator.isAuthorized()")
//    public ResponseEntity<Map<String, Object>> getAllApplicableUnionsForOrganization(@PathVariable Long unitId) {
//        return ResponseHandler.generateResponse(HttpStatus.OK, true,
//                unionService.getAllApplicableUnionsForOrganization(unitId));
//    }
//    @RequestMapping(value = UNIT_URL + "/unions/{unionId}", method = RequestMethod.GET)
//    @ApiOperation("Get All Unions by organization ")
//    // @PreAuthorize("@customPermissionEvaluator.isAuthorized()")
//    public ResponseEntity<Map<String, Object>> addUnionInOrganization(@PathVariable Long unitId,@PathVariable Long unionId,@RequestParam("joined") boolean joined) {
//        return ResponseHandler.generateResponse(HttpStatus.OK, true,
//                unionService.addUnionInOrganization(unitId,unionId,joined));
//    }
}
