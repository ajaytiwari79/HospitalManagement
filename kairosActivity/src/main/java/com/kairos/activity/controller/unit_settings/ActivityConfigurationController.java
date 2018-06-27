package com.kairos.activity.controller.unit_settings;

import com.kairos.activity.service.unit_settings.ActivityConfigurationService;
import com.kairos.activity.unit_settings.activity_configuration.AbsencePlannedTime;
import com.kairos.activity.unit_settings.activity_configuration.PresencePlannedTime;
import com.kairos.activity.util.response.ResponseHandler;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.inject.Inject;
import java.math.BigInteger;
import java.util.Map;

import static com.kairos.activity.constants.ApiConstants.ACTIVITY_CONFIGURATION;

@RestController

@Api(ACTIVITY_CONFIGURATION)
@RequestMapping(ACTIVITY_CONFIGURATION)
public class ActivityConfigurationController {
    @Inject
    private ActivityConfigurationService activityConfigurationService;

    @ApiOperation("Update presence Activity Configuration ")
    @PutMapping(value = "/presence")
    //@PreAuthorize("@customPermissionEvaluator.isAuthorized()")
    public ResponseEntity<Map<String, Object>> updatePresenceActivityConfiguration(@PathVariable Long unitId, @RequestBody PresencePlannedTime presencePlannedTime) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true, activityConfigurationService.updatePresenceActivityConfiguration(unitId, presencePlannedTime));
    }

    @ApiOperation("Update absence Activity Configuration ")
    @PutMapping(value = "/absence/{activityConfigurationId}")
    //@PreAuthorize("@customPermissionEvaluator.isAuthorized()")
    public ResponseEntity<Map<String, Object>> updateAbsenceActivityConfiguration(@PathVariable Long unitId, @PathVariable BigInteger activityConfigurationId, @RequestBody AbsencePlannedTime absencePlannedTime) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true, activityConfigurationService.updateAbsenceActivityConfiguration(unitId, activityConfigurationId, absencePlannedTime));
    }


    @ApiOperation("create exception absence Activity Configuration ")
    @PostMapping(value = "/absence")
    //@PreAuthorize("@customPermissionEvaluator.isAuthorized()")
    public ResponseEntity<Map<String, Object>> createAbsenceExceptionActivityConfiguration(@PathVariable Long unitId, @RequestBody AbsencePlannedTime absencePlannedTime) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true, activityConfigurationService.createAbsenceExceptionActivityConfiguration(unitId, absencePlannedTime));
    }


    @ApiOperation("CREATE Activity Configuration ")
    @PostMapping
    //@PreAuthorize("@customPermissionEvaluator.isAuthorized()")
    public ResponseEntity<Map<String, Object>> createDefaultPhaseSettings(@PathVariable Long unitId, @RequestParam("countryId") Long countryId) {
        activityConfigurationService.createDefaultSettings(unitId, countryId, null);
        return ResponseHandler.generateResponse(HttpStatus.OK, true, true);
    }

    @ApiOperation("Get presence Activity Configuration")
    @GetMapping(value = "/presence")
    //@PreAuthorize("@customPermissionEvaluator.isAuthorized()")
    public ResponseEntity<Map<String, Object>> getAbsenceActivityConfiguration(@PathVariable Long unitId) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true, activityConfigurationService.getPresenceActivityConfiguration(unitId));
    }

    @ApiOperation("Get absence Activity Configuration")
    @GetMapping(value = "/absence")
    //@PreAuthorize("@customPermissionEvaluator.isAuthorized()")
    public ResponseEntity<Map<String, Object>> getPresenceActivityConfiguration(@PathVariable Long unitId) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true, activityConfigurationService.getAbsenceActivityConfiguration(unitId));
    }

    @ApiOperation("Get default data ")
    @GetMapping(value = "/default_data")
    //@PreAuthorize("@customPermissionEvaluator.isAuthorized()")
    public ResponseEntity<Map<String, Object>> getDefaultData(@PathVariable Long unitId) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true, activityConfigurationService.getDefaultData(unitId));
    }

}
