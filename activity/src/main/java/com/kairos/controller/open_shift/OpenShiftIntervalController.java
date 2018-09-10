package com.kairos.controller.open_shift;

import com.kairos.service.open_shift.OpenShiftIntervalService;
import com.kairos.utils.response.ResponseHandler;
import com.kairos.dto.activity.open_shift.OpenShiftIntervalDTO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.inject.Inject;
import javax.validation.Valid;
import java.math.BigInteger;
import java.util.Map;

import static com.kairos.constants.ApiConstants.API_ORGANIZATION_COUNTRY_URL;

@RestController
@RequestMapping(API_ORGANIZATION_COUNTRY_URL)
@Api(API_ORGANIZATION_COUNTRY_URL)
public class OpenShiftIntervalController {
    @Inject  private OpenShiftIntervalService openShiftIntervalService;
    @ApiOperation("Create Interval")
    @PostMapping(value = "/interval")
    //  @PreAuthorize("@customPermissionEvaluator.isAuthorized()")
    public ResponseEntity<Map<String, Object>> createInterval(@PathVariable Long countryId, @RequestBody @Valid OpenShiftIntervalDTO openShiftIntervalDTO) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true, openShiftIntervalService.createInterval(countryId, openShiftIntervalDTO));
    }

    @ApiOperation("Get all Intervals based on countryId")
    @GetMapping(value = "/intervals")
    //@PreAuthorize("@customPermissionEvaluator.isAuthorized()")
    public ResponseEntity<Map<String, Object>> getIntervals(@PathVariable long countryId) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true, openShiftIntervalService.getAllIntervalsByCountryId(countryId));
    }

    @ApiOperation("Update Interval of Activity")
    @PutMapping(value = "/interval/{openShiftIntervalId}")
        //@PreAuthorize("@customPermissionEvaluator.isAuthorized()")
    ResponseEntity<Map<String, Object>> updateInterval(@PathVariable Long countryId, @PathVariable BigInteger openShiftIntervalId, @RequestBody OpenShiftIntervalDTO openShiftIntervalDTO) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true, openShiftIntervalService.updateInterval(countryId,openShiftIntervalId, openShiftIntervalDTO));
    }

    @ApiOperation("delete an Interval based on countryId")
    @DeleteMapping(value = "/interval/{openShiftIntervalId}")
    //@PreAuthorize("@customPermissionEvaluator.isAuthorized()")
    public ResponseEntity<Map<String, Object>> deleteInterval(@PathVariable Long countryId,@PathVariable BigInteger openShiftIntervalId) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true, openShiftIntervalService.deleteOpenShiftInterval(countryId,openShiftIntervalId));
    }
}
