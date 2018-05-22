package com.kairos.activity.controller.open_shift;

import com.kairos.activity.response.dto.ActivityDTO;
import com.kairos.activity.service.open_shift.OpenShiftIntervalService;
import com.kairos.activity.util.response.ResponseHandler;
import com.kairos.response.dto.web.open_shift.OpenShiftIntervalDTO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.inject.Inject;
import javax.validation.Valid;
import java.util.Map;

import static com.kairos.activity.constants.ApiConstants.API_ORGANIZATION_COUNTRY_URL;

@RestController
@RequestMapping(API_ORGANIZATION_COUNTRY_URL)
@Api(API_ORGANIZATION_COUNTRY_URL)
public class OpenShiftIntervalController {
    @Inject private OpenShiftIntervalService openShiftIntervalService;
    @ApiOperation("Create Interval")
    @PostMapping(value = "/interval")
    //  @PreAuthorize("@customPermissionEvaluator.isAuthorized()")
    public ResponseEntity<Map<String, Object>> createActivity(@PathVariable Long countryId, @RequestBody @Valid OpenShiftIntervalDTO openShiftIntervalDTO) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true, openShiftIntervalService.createInterval(countryId, openShiftIntervalDTO));
    }

    @ApiOperation("Get all Intervals based on countryId")
    @GetMapping(value = "/intervals")
    //@PreAuthorize("@customPermissionEvaluator.isAuthorized()")
    public ResponseEntity<Map<String, Object>> getActivity(@PathVariable long countryId) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true, activityService.findAllActivityByCountry(countryId));
    }
}
