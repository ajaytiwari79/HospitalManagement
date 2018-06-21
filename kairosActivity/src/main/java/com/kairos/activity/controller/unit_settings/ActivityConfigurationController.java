package com.kairos.activity.controller.unit_settings;

import com.kairos.activity.service.unit_settings.ActivityConfigurationService;
import com.kairos.activity.util.response.ResponseHandler;
import com.kairos.enums.unit_settings.TimeTypeEnum;
import com.kairos.response.dto.web.break_settings.BreakSettingsDTO;
import com.kairos.response.dto.web.unit_settings.activity_configuration.ActivityConfigurationDTO;
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

    @ApiOperation("Update Activity Configuration ")
    @PostMapping
    //@PreAuthorize("@customPermissionEvaluator.isAuthorized()")
    public ResponseEntity<Map<String, Object>> updateActivityConfiguration(@PathVariable Long unitId, @RequestBody ActivityConfigurationDTO activityConfigurationDTO) {
        return ResponseHandler.generateResponse(HttpStatus.CREATED, true, activityConfigurationService.updateActivityConfiguration(unitId, activityConfigurationDTO));
    }

    @ApiOperation("Get Activity Configuration")
    @GetMapping
    //@PreAuthorize("@customPermissionEvaluator.isAuthorized()")
    public ResponseEntity<Map<String, Object>> getActivityConfiguration(@PathVariable Long unitId, @RequestParam TimeTypeEnum timeTypeEnum) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true, activityConfigurationService.getActivityConfiguration(unitId, timeTypeEnum));
    }

    @ApiOperation("Get default data ")
    @GetMapping(value = "/default_data")
    //@PreAuthorize("@customPermissionEvaluator.isAuthorized()")
    public ResponseEntity<Map<String, Object>> getDefaultData(@PathVariable Long unitId) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true, activityConfigurationService.getDefaultData(unitId));
    }

}
