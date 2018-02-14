package com.kairos.controller.organization;

import com.kairos.service.organization.UnionService;
import com.kairos.util.response.ResponseHandler;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.inject.Inject;
import java.util.Map;

import static com.kairos.constants.ApiConstants.API_ORGANIZATION_URL;
import static com.kairos.constants.ApiConstants.UNIT_URL;

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

    @RequestMapping(value = UNIT_URL + "/unions", method = RequestMethod.GET)
    @ApiOperation("Get All Unions by organization ")
    // @PreAuthorize("@customPermissionEvaluator.isAuthorized()")
    public ResponseEntity<Map<String, Object>> getAllUnionByOrganization(@PathVariable Long unitId) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true,
                unionService.getAllUnionByOrganization(unitId));
    }
}
