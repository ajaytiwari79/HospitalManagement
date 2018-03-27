package com.kairos.controller.country;

import com.kairos.response.dto.web.experties.CountryExpertiseDTO;
import com.kairos.service.expertise.ExpertiseService;
import com.kairos.util.response.ResponseHandler;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.inject.Inject;
import java.util.Map;

import static com.kairos.constants.ApiConstants.API_ORGANIZATION_COUNTRY_URL;
import static com.kairos.constants.ApiConstants.API_ORGANIZATION_URL;
import static com.kairos.constants.ApiConstants.COUNTRY_URL;

/**
 * Created by vipul on 27/3/18.
 */
@RequestMapping(API_ORGANIZATION_COUNTRY_URL)
@Api(API_ORGANIZATION_COUNTRY_URL)
@RestController
public class ExpertiseController {
    @Inject
    private ExpertiseService expertiseService;


    @ApiOperation(value = "Create Expertise")
    @RequestMapping(value = "/expertise", method = RequestMethod.POST)
    //@PreAuthorize("@customPermissionEvaluator.isAuthorized()")
    public ResponseEntity<Map<String, Object>> saveExpertise(@PathVariable long countryId, @Validated @RequestBody CountryExpertiseDTO expertise) {
        return ResponseHandler.generateResponse(HttpStatus.CREATED, true, expertiseService.saveExpertise(countryId, expertise));
    }

    @ApiOperation(value = "Get Available expertise")
    @RequestMapping(value = "/expertise", method = RequestMethod.GET)
    //@PreAuthorize("@customPermissionEvaluator.isAuthorized()")
    public ResponseEntity<Map<String, Object>> getAllExpertise(@PathVariable long countryId) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true, expertiseService.getAllExpertise(countryId));
    }

    @ApiOperation(value = "Get all Details")
    @RequestMapping(value = "/union_with_Service", method = RequestMethod.GET)
    //@PreAuthorize("@customPermissionEvaluator.isAuthorized()")
    public ResponseEntity<Map<String, Object>> getUnionsAndService(@PathVariable long countryId) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true, expertiseService.getUnionsAndService(countryId));
    }

    @ApiOperation(value = "Update expertise")
    @RequestMapping(value = "/expertise", method = RequestMethod.PUT)
    //@PreAuthorize("@customPermissionEvaluator.isAuthorized()")
    public ResponseEntity<Map<String, Object>> updateExpertise(@RequestBody @Validated CountryExpertiseDTO expertise) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true, expertiseService.updateExpertise(expertise));
    }

    @ApiOperation(value = "Delete expertise")
    @RequestMapping(value = "/expertise/{expertiseId}", method = RequestMethod.DELETE)
    //@PreAuthorize("@customPermissionEvaluator.isAuthorized()")
    public ResponseEntity<Map<String, Object>> deleteExpertise(@PathVariable long expertiseId) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true, expertiseService.deleteExpertise(expertiseId));
    }
}
