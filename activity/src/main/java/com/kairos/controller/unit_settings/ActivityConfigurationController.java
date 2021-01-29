package com.kairos.controller.unit_settings;

import com.kairos.dto.activity.unit_settings.activity_configuration.AbsencePlannedTime;
import com.kairos.dto.activity.unit_settings.activity_configuration.NonWorkingPlannedTime;
import com.kairos.dto.activity.unit_settings.activity_configuration.PresencePlannedTime;
import com.kairos.service.unit_settings.ActivityConfigurationService;
import com.kairos.utils.response.ResponseHandler;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.inject.Inject;
import javax.validation.Valid;
import java.math.BigInteger;
import java.util.Map;

import static com.kairos.constants.ApiConstants.*;

@RestController

@Api(API_V1)
@RequestMapping(API_V1)
public class ActivityConfigurationController {
    @Inject
    private ActivityConfigurationService activityConfigurationService;

    @ApiOperation("Update presence Activity Configuration ")
    @PutMapping(value = UNIT_ACTIVITY_CONFIGURATION+"/presence")
    //@PreAuthorize("@customPermissionEvaluator.isAuthorized()")
    public ResponseEntity<Map<String, Object>> updatePresenceActivityConfiguration(@PathVariable Long unitId, @RequestBody @Valid PresencePlannedTime presencePlannedTime) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true, activityConfigurationService.updatePresenceActivityConfiguration(unitId, presencePlannedTime));
    }

    @ApiOperation("Update absence Activity Configuration ")
    @PutMapping(value = UNIT_ACTIVITY_CONFIGURATION+"/absence/{activityConfigurationId}")
    //@PreAuthorize("@customPermissionEvaluator.isAuthorized()")
    public ResponseEntity<Map<String, Object>> updateAbsenceActivityConfiguration(@PathVariable Long unitId, @PathVariable BigInteger activityConfigurationId, @RequestBody @Valid AbsencePlannedTime absencePlannedTime) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true, activityConfigurationService.updateAbsenceActivityConfiguration(activityConfigurationId, absencePlannedTime));
    }


    @ApiOperation("create exception absence Activity Configuration ")
    @PostMapping(value = UNIT_ACTIVITY_CONFIGURATION+"/absence")
    //@PreAuthorize("@customPermissionEvaluator.isAuthorized()")
    public ResponseEntity<Map<String, Object>> createAbsenceExceptionActivityConfiguration(@PathVariable Long unitId, @RequestBody @Valid AbsencePlannedTime absencePlannedTime) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true, activityConfigurationService.createAbsenceExceptionActivityConfiguration(unitId, absencePlannedTime, false));
    }


    /*@ApiOperation("CREATE Activity Configuration ")
    @PostMapping(value = UNIT_ACTIVITY_CONFIGURATION)
    //@PreAuthorize("@customPermissionEvaluator.isAuthorized()")
    public ResponseEntity<Map<String, Object>> createDefaultSettings(@PathVariable Long unitId, @RequestParam("countryId") Long countryId) {
        activityConfigurationService.createDefaultSettings(unitId, countryId, null);
        return ResponseHandler.generateResponse(HttpStatus.OK, true, true);
    }*/

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
        return ResponseHandler.generateResponse(HttpStatus.OK, true, activityConfigurationService.updateAbsenceActivityConfiguration(activityConfigurationId, absencePlannedTime));
    }


    @ApiOperation("create exception absence Activity Configuration ")
    @PostMapping(value = COUNTRY_ACTIVITY_CONFIGURATION+"/absence")
    //@PreAuthorize("@customPermissionEvaluator.isAuthorized()")
    public ResponseEntity<Map<String, Object>> createAbsenceExceptionActivityConfigurationForCountry(@PathVariable Long countryId, @RequestBody AbsencePlannedTime absencePlannedTime) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true, activityConfigurationService.createAbsenceExceptionActivityConfiguration(countryId, absencePlannedTime, true));
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
    public ResponseEntity<Map<String, Object>> getPresenceActivityConfigurationForCountry(@PathVariable Long countryId) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true, activityConfigurationService.getPresenceActivityConfigurationForCountry(countryId));
    }

    @ApiOperation("Get absence Activity Configuration")
    @GetMapping(value = COUNTRY_ACTIVITY_CONFIGURATION+"/absence")
    //@PreAuthorize("@customPermissionEvaluator.isAuthorized()")
    public ResponseEntity<Map<String, Object>> getAbsenceActivityConfigurationForCountry(@PathVariable Long countryId) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true, activityConfigurationService.getAbsenceActivityConfigurationForCountry(countryId));
    }

    @ApiOperation("Get default data ")
    @GetMapping(value = COUNTRY_ACTIVITY_CONFIGURATION+"/default_data")
    //@PreAuthorize("@customPermissionEvaluator.isAuthorized()")
    public ResponseEntity<Map<String, Object>> getDefaultDataForCountry(@PathVariable Long countryId) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true, activityConfigurationService.getDefaultDataForCountry(countryId));
    }

    @ApiOperation("create exception non working Activity Configuration ")
    @PostMapping(value = UNIT_ACTIVITY_CONFIGURATION+"/non_working")
    //@PreAuthorize("@customPermissionEvaluator.isAuthorized()")
    public ResponseEntity<Map<String, Object>> createNonWorkingExceptionActivityConfiguration(@PathVariable Long unitId, @RequestBody @Valid NonWorkingPlannedTime nonWorkingPlannedTime) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true, activityConfigurationService.createNonWorkingExceptionActivityConfiguration(unitId, nonWorkingPlannedTime, false));
    }

    @ApiOperation("Update non working Activity Configuration ")
    @PutMapping(value = UNIT_ACTIVITY_CONFIGURATION+"/non_working/{activityConfigurationId}")
    //@PreAuthorize("@customPermissionEvaluator.isAuthorized()")
    public ResponseEntity<Map<String, Object>> updateNonWorkingActivityConfiguration(@PathVariable Long unitId, @PathVariable BigInteger activityConfigurationId, @RequestBody @Valid NonWorkingPlannedTime nonWorkingPlannedTime) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true, activityConfigurationService.updateNonWorkingActivityConfiguration(activityConfigurationId, nonWorkingPlannedTime));
    }

    @ApiOperation("Get non working Activity Configuration")
    @GetMapping(value = UNIT_ACTIVITY_CONFIGURATION+"/non_working")
    //@PreAuthorize("@customPermissionEvaluator.isAuthorized()")
    public ResponseEntity<Map<String, Object>> getNonWorkingActivityConfiguration(@PathVariable Long unitId) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true, activityConfigurationService.getNonWorkingActivityConfiguration(unitId));
    }

    @ApiOperation("create exception non working Activity Configuration ")
    @PostMapping(value = COUNTRY_ACTIVITY_CONFIGURATION+"/non_working")
    //@PreAuthorize("@customPermissionEvaluator.isAuthorized()")
    public ResponseEntity<Map<String, Object>> createNonWorkingExceptionActivityConfigurationForCountry(@PathVariable Long countryId, @RequestBody NonWorkingPlannedTime nonWorkingPlannedTime) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true, activityConfigurationService.createNonWorkingExceptionActivityConfiguration(countryId, nonWorkingPlannedTime, true));
    }

    @ApiOperation("Update non working Activity Configuration ")
    @PutMapping(value = COUNTRY_ACTIVITY_CONFIGURATION+"/non_working/{activityConfigurationId}")
    //@PreAuthorize("@customPermissionEvaluator.isAuthorized()")
    public ResponseEntity<Map<String, Object>> updateNonWorkingActivityConfigurationForCountry(@PathVariable Long countryId, @PathVariable BigInteger activityConfigurationId, @RequestBody NonWorkingPlannedTime nonWorkingPlannedTime) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true, activityConfigurationService.updateNonWorkingActivityConfiguration(activityConfigurationId, nonWorkingPlannedTime));
    }

    @ApiOperation("Get non working Activity Configuration")
    @GetMapping(value = COUNTRY_ACTIVITY_CONFIGURATION+"/non_working")
    //@PreAuthorize("@customPermissionEvaluator.isAuthorized()")
    public ResponseEntity<Map<String, Object>> getNonWorkingActivityConfigurationForCountry(@PathVariable Long countryId) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true, activityConfigurationService.getNonWorkingActivityConfigurationForCountry(countryId));
    }

    @ApiOperation("create exception non working Activity Configuration ")
    @PostMapping(value = "/copy_non_working_from_absence")
    //@PreAuthorize("@customPermissionEvaluator.isAuthorized()")
    public ResponseEntity<Map<String, Object>> copyNonWorkingActivityConfigurationFromAbsence() {
        return ResponseHandler.generateResponse(HttpStatus.OK, true, activityConfigurationService.copyNonWorkingActivityConfigurationFromAbsence());
    }

    @ApiOperation("Get Planned TimeType Configuration")
    @GetMapping(value = UNIT_ACTIVITY_CONFIGURATION+"/get_planned_timetype_configuration")
    //@PreAuthorize("@customPermissionEvaluator.isAuthorized()")
    public ResponseEntity<Map<String, Object>> getPlannedTimeTypeConfiguration(@PathVariable Long unitId) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true, activityConfigurationService.getPlannedTimeTypeConfiguration(unitId));
    }

}
