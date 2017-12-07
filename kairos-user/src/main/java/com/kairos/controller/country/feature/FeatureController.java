package com.kairos.controller.country.feature;

import com.kairos.response.dto.web.feature.FeatureDTO;
import com.kairos.response.dto.web.feature.VehicleFeaturesDTO;
import com.kairos.service.country.feature.FeatureService;
import com.kairos.util.response.ResponseHandler;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.inject.Inject;
import java.util.Map;

import static com.kairos.constants.ApiConstants.API_ORGANIZATION_URL;
import static com.kairos.constants.ApiConstants.COUNTRY_URL;

/**
 * Created by prerna on 4/12/17.
 */
@RestController

@RequestMapping(API_ORGANIZATION_URL)
@Api(API_ORGANIZATION_URL)
public class FeatureController {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Inject
    FeatureService featureService;

    @ApiOperation(value = "Create a New Feature in Country")
    @RequestMapping(value = COUNTRY_URL + "/feature", method = RequestMethod.POST)
    //@PreAuthorize("@customPermissionEvaluator.isAuthorized()")
    public ResponseEntity<Map<String, Object>> addCountryTag(@Validated @RequestBody FeatureDTO featureDTO, @PathVariable long countryId) {
        return ResponseHandler.generateResponse(HttpStatus.CREATED, true,featureService.addCountryFeature(countryId,featureDTO));
    }

    @ApiOperation(value = "Update a Country Feature")
    @RequestMapping(value = COUNTRY_URL + "/feature/{featureId}", method = RequestMethod.PUT)
    //@PreAuthorize("@customPermissionEvaluator.isAuthorized()")
    public ResponseEntity<Map<String, Object>> updateCountryTag(@Validated @RequestBody FeatureDTO featureDTO, @PathVariable long countryId, @PathVariable long featureId) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true,featureService.updateFeature(countryId, featureId, featureDTO));
    }

    @ApiOperation(value = "Get list of Features")
    @RequestMapping(value = COUNTRY_URL + "/feature", method = RequestMethod.GET)
    //@PreAuthorize("@customPermissionEvaluator.isAuthorized()")
    public ResponseEntity<Map<String, Object>> getCountryTag(@PathVariable long countryId,
                                                             @RequestParam(value = "filterText",required = false) String filterText) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true,featureService.getListOfFeatures(countryId,filterText));
    }

    @ApiOperation(value = "Delete Feature")
    @RequestMapping(value = COUNTRY_URL + "/feature/{featureId}", method = RequestMethod.DELETE)
    //@PreAuthorize("@customPermissionEvaluator.isAuthorized()")
    public ResponseEntity<Map<String, Object>> deleteCountryTag(@PathVariable long countryId, @PathVariable long featureId) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true,featureService.deleteFeature(countryId, featureId));
    }

    @ApiOperation(value = "Update Features of vehicles")
    @RequestMapping(value = COUNTRY_URL + "/vehicle/{vehicleId}/feature", method = RequestMethod.PUT)
    //@PreAuthorize("@customPermissionEvaluator.isAuthorized()")
    public ResponseEntity<Map<String, Object>> updateFeaturesOfVehicle(@Validated @RequestBody VehicleFeaturesDTO vehicleFeatureDTO, @PathVariable long countryId, @PathVariable long vehicleId) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true,featureService.updateFeaturesOfVehicle(countryId, vehicleId, vehicleFeatureDTO));
    }
}
