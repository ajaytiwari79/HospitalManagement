package com.kairos.controller.unit_settings;

import com.kairos.service.unit_settings.ActivityConfigurationService;
import com.kairos.dto.activity.unit_settings.activity_configuration.AbsencePlannedTime;
import com.kairos.dto.activity.unit_settings.activity_configuration.PresencePlannedTime;
import com.kairos.utils.response.ResponseHandler;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.inject.Inject;
import java.math.BigInteger;
import java.util.Map;

import static com.kairos.constants.ApiConstants.*;

@RestController

@Api(API_ORGANIZATION_URL)
@RequestMapping(API_ORGANIZATION_URL)
public class ActivityConfigurationController {
    @Inject
    private ActivityConfigurationService activityConfigurationService;

    @ApiOperation("Update presence Activity Configuration ")
    @PutMapping(value = UNIT_ACTIVITY_CONFIGURATION+"/presence")
    //@PreAuthorize("@customPermissionEvaluator.isAuthorized()")
    public ResponseEntity<Map<String, Object>> updatePresenceActivityConfiguration(@PathVariable Long unitId, @RequestBody PresencePlannedTime presencePlannedTime) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true, activityConfigurationService.updatePresenceActivityConfiguration(unitId, presencePlannedTime));
    }

    @ApiOperation("Update absence Activity Configuration ")
    @PutMapping(value = UNIT_ACTIVITY_CONFIGURATION+"/absence/{activityConfigurationId}")
    //@PreAuthorize("@customPermissionEvaluator.isAuthorized()")
    public ResponseEntity<Map<String, Object>> updateAbsenceActivityConfiguration(@PathVariable Long unitId, @PathVariable BigInteger activityConfigurationId, @RequestBody AbsencePlannedTime absencePlannedTime) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true, activityConfigurationService.updateAbsenceActivityConfiguration(unitId, activityConfigurationId, absencePlannedTime));
    }


    @ApiOperation("create exception absence Activity Configuration ")
    @PostMapping(value = UNIT_ACTIVITY_CONFIGURATION+"/absence")
    //@PreAuthorize("@customPermissionEvaluator.isAuthorized()")
    public ResponseEntity<Map<String, Object>> createAbsenceExceptionActivityConfiguration(@PathVariable Long unitId, @RequestBody AbsencePlannedTime absencePlannedTime) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true, activityConfigurationService.createAbsenceExceptionActivityConfiguration(unitId, absencePlannedTime));
    }


    @ApiOperation("CREATE Activity Configuration ")
    @PostMapping(value = UNIT_ACTIVITY_CONFIGURATION)
    //@PreAuthorize("@customPermissionEvaluator.isAuthorized()")
    public ResponseEntity<Map<String, Object>> createDefaultSettings(@PathVariable Long unitId, @RequestParam("countryId") Long countryId) {
        activityConfigurationService.createDefaultSettings(unitId, countryId, null);
        return ResponseHandler.generateResponse(HttpStatus.OK, true, true);
    }

    @ApiOperation("Get presence Activity Configuration")
    @GetMapping(value = UNIT_ACTIVITY_CONFIGURATION+"/presence")
    //@PreAuthorize("@customPermissionEvaluator.isAuthorized()")
    public ResponseEntity<Map<String, Object>> getAbsenceActivityConfiguration(@PathVariable Long unitId) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true, activityConfigurationService.getPresenceActivityConfiguration(unitId));
    }

    @ApiOperation("Get absence Activity Configuration")
    @GetMapping(value = UNIT_ACTIVITY_CONFIGURATION+"/absence")
    //@PreAuthorize("@customPermissionEvaluator.isAuthorized()")
    public ResponseEntity<Map<String, Object>> getPresenceActivityConfiguration(@PathVariable Long unitId) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true, activityConfigurationService.getAbsenceActivityConfiguration(unitId));
    }

    @ApiOperation("Get default data ")
    @GetMapping(value = UNIT_ACTIVITY_CONFIGURATION+"/default_data")
    //@PreAuthorize("@customPermissionEvaluator.isAuthorized()")
    public ResponseEntity<Map<String, Object>> getDefaultData(@PathVariable Long unitId) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true, activityConfigurationService.getDefaultData(unitId));
    }


    @ApiOperation("Update presence Activity Configuration ")
    @PutMapping(value = COUNTRY_ACTIVITY_CONFIGURATION+"/presence")
    //@PreAuthorize("@customPermissionEvaluator.isAuthorized()")
    public ResponseEntity<Map<String, Object>> updatePresenceActivityConfigurationForCountry(@PathVariable Long countryId, @RequestBody PresencePlannedTime presencePlannedTime) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true, activityConfigurationService.updatePresenceActivityConfigurationForCountry(countryId, presencePlannedTime));
    }

    @ApiOperation("Update absence Activity Configuration ")
    @PutMapping(value = COUNTRY_ACTIVITY_CONFIGURATION+"/absence/{activityConfigurationId}")
    //@PreAuthorize("@customPermissionEvaluator.isAuthorized()")
    public ResponseEntity<Map<String, Object>> updateAbsenceActivityConfigurationForCountry(@PathVariable Long countryId, @PathVariable BigInteger activityConfigurationId, @RequestBody AbsencePlannedTime absencePlannedTime) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true, activityConfigurationService.updateAbsenceActivityConfigurationForCountry(countryId, activityConfigurationId, absencePlannedTime));
    }


    @ApiOperation("create exception absence Activity Configuration ")
    @PostMapping(value = COUNTRY_ACTIVITY_CONFIGURATION+"/absence")
    //@PreAuthorize("@customPermissionEvaluator.isAuthorized()")
    public ResponseEntity<Map<String, Object>> createAbsenceExceptionActivityConfigurationForCountry(@PathVariable Long countryId, @RequestBody AbsencePlannedTime absencePlannedTime) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true, activityConfigurationService.createAbsenceExceptionActivityConfigurationForCountry(countryId, absencePlannedTime));
    }


    @ApiOperation("CREATE Activity Configuration ")
    @PostMapping(value = COUNTRY_ACTIVITY_CONFIGURATION)
    //@PreAuthorize("@customPermissionEvaluator.isAuthorized()")
    public ResponseEntity<Map<String, Object>> createDefaultPhaseSettingsForCountry(@PathVariable Long countryId) {
        activityConfigurationService.createDefaultSettingsForCountry( countryId, null);
        return ResponseHandler.generateResponse(HttpStatus.OK, true, true);
    }

    @ApiOperation("Get presence Activity Configuration")
    @GetMapping(value = COUNTRY_ACTIVITY_CONFIGURATION+"/presence")
    //@PreAuthorize("@customPermissionEvaluator.isAuthorized()")
    public ResponseEntity<Map<String, Object>> getAbsenceActivityConfigurationForCountry(@PathVariable Long countryId) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true, activityConfigurationService.getPresenceActivityConfigurationForCountry(countryId));
    }

    @ApiOperation("Get absence Activity Configuration")
    @GetMapping(value = COUNTRY_ACTIVITY_CONFIGURATION+"/absence")
    //@PreAuthorize("@customPermissionEvaluator.isAuthorized()")
    public ResponseEntity<Map<String, Object>> getPresenceActivityConfigurationForCountry(@PathVariable Long countryId) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true, activityConfigurationService.getAbsenceActivityConfigurationForCountry(countryId));
    }

    @ApiOperation("Get default data ")
    @GetMapping(value = COUNTRY_ACTIVITY_CONFIGURATION+"/default_data")
    //@PreAuthorize("@customPermissionEvaluator.isAuthorized()")
    public ResponseEntity<Map<String, Object>> getDefaultDataForCountry(@PathVariable Long countryId) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true, activityConfigurationService.getDefaultDataForCountry(countryId));
    }


}
