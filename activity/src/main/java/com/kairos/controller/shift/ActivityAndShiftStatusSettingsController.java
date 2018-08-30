package com.kairos.controller.shift;
/*
 *Created By Pavan on 29/8/18
 *
 */

import com.kairos.activity.shift.ActivityAndShiftStatusSettingsDTO;
import com.kairos.service.shift.ActivityAndShiftStatusSettingsService;
import com.kairos.util.response.ResponseHandler;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.inject.Inject;
import javax.validation.Valid;
import java.math.BigInteger;
import java.util.Map;

import static com.kairos.constants.ApiConstants.API_ORGANIZATION_URL;
import static com.kairos.constants.ApiConstants.COUNTRY_ACTIVITY_AND_SHIFT_STATUS_SETTINGS_URL;
import static com.kairos.constants.ApiConstants.UNIT_ACTIVITY_AND_SHIFT_STATUS_SETTINGS_URL;

@RestController
@RequestMapping(API_ORGANIZATION_URL)
@Api(API_ORGANIZATION_URL)
public class ActivityAndShiftStatusSettingsController {

    @Inject
    private ActivityAndShiftStatusSettingsService activityAndShiftStatusSettingsService;

    @PostMapping(value = COUNTRY_ACTIVITY_AND_SHIFT_STATUS_SETTINGS_URL)
    @ApiOperation("create Activity and shift status setting")
    //@PreAuthorize("@customPermissionEvaluator.isAuthorized()")
    public ResponseEntity<Map<String,Object>> addActivityAndShiftStatusSetting(@PathVariable Long countryId, @Valid @RequestBody ActivityAndShiftStatusSettingsDTO activityAndShiftStatusSettingsDTO){
        return ResponseHandler.generateResponse(HttpStatus.OK,true,activityAndShiftStatusSettingsService.addActivityAndShiftStatusSettings(countryId,activityAndShiftStatusSettingsDTO));
    }

    @GetMapping(value = COUNTRY_ACTIVITY_AND_SHIFT_STATUS_SETTINGS_URL)
    @ApiOperation("get All Activity and shift status setting")
    //@PreAuthorize("@customPermissionEvaluator.isAuthorized()")
    public ResponseEntity<Map<String,Object>> getAllActivityAndShiftStatusSettings(@PathVariable Long countryId){
        return ResponseHandler.generateResponse(HttpStatus.OK,true,activityAndShiftStatusSettingsService.getActivityAndShiftStatusSettingsGroupedByStatus(countryId));
    }

    @PutMapping(value = COUNTRY_ACTIVITY_AND_SHIFT_STATUS_SETTINGS_URL)
    @ApiOperation("update Activity and shift status setting")
    //@PreAuthorize("@customPermissionEvaluator.isAuthorized()")
    public ResponseEntity<Map<String,Object>> updateActivityAndShiftStatusSettings(@PathVariable Long countryId,@RequestBody ActivityAndShiftStatusSettingsDTO activityAndShiftStatusSettingsDTO){
        return ResponseHandler.generateResponse(HttpStatus.OK,true,activityAndShiftStatusSettingsService.updateActivityAndShiftStatusSettings(countryId,activityAndShiftStatusSettingsDTO));
    }

    @DeleteMapping(value = COUNTRY_ACTIVITY_AND_SHIFT_STATUS_SETTINGS_URL+"/{id}")
    @ApiOperation("delete Activity and shift status setting")
   // @PreAuthorize("@customPermissionEvaluator.isAuthorized()")
    public ResponseEntity<Map<String,Object>> deleteActivityAndShiftStatusSettings(@PathVariable BigInteger id){
        return ResponseHandler.generateResponse(HttpStatus.NO_CONTENT,true,activityAndShiftStatusSettingsService.deleteActivityAndShiftStatusSettings(id));
    }


    // EndPoints for unit

    @PostMapping(value = UNIT_ACTIVITY_AND_SHIFT_STATUS_SETTINGS_URL)
    @ApiOperation("create Activity and shift status setting")
    //@PreAuthorize("@customPermissionEvaluator.isAuthorized()")
    public ResponseEntity<Map<String,Object>> addActivityAndShiftStatusSettingForUnit(@PathVariable Long unitId, @Valid @RequestBody ActivityAndShiftStatusSettingsDTO activityAndShiftStatusSettingsDTO){
        return ResponseHandler.generateResponse(HttpStatus.OK,true,activityAndShiftStatusSettingsService.addActivityAndShiftStatusSettingForUnit(unitId,activityAndShiftStatusSettingsDTO));
    }

    @GetMapping(value = UNIT_ACTIVITY_AND_SHIFT_STATUS_SETTINGS_URL)
    @ApiOperation("get All Activity and shift status setting")
    //@PreAuthorize("@customPermissionEvaluator.isAuthorized()")
    public ResponseEntity<Map<String,Object>> getAllActivityAndShiftStatusSettingsForUnit(@PathVariable Long unitId){
        return ResponseHandler.generateResponse(HttpStatus.OK,true,activityAndShiftStatusSettingsService.getActivityAndShiftStatusSettingsGroupedByStatusForUnit(unitId));
    }

    @PutMapping(value = UNIT_ACTIVITY_AND_SHIFT_STATUS_SETTINGS_URL)
    @ApiOperation("update Activity and shift status setting")
    //@PreAuthorize("@customPermissionEvaluator.isAuthorized()")
    public ResponseEntity<Map<String,Object>> updateActivityAndShiftStatusSettingsForUnit(@PathVariable Long unitId,@RequestBody ActivityAndShiftStatusSettingsDTO activityAndShiftStatusSettingsDTO){
        return ResponseHandler.generateResponse(HttpStatus.OK,true,activityAndShiftStatusSettingsService.updateActivityAndShiftStatusSettingsForUnit(unitId,activityAndShiftStatusSettingsDTO));
    }

    @DeleteMapping(value = UNIT_ACTIVITY_AND_SHIFT_STATUS_SETTINGS_URL+"/{id}")
    @ApiOperation("delete Activity and shift status setting")
    // @PreAuthorize("@customPermissionEvaluator.isAuthorized()")
    public ResponseEntity<Map<String,Object>> deleteActivityAndShiftStatusSettingsForUnit(@PathVariable BigInteger id){
        return ResponseHandler.generateResponse(HttpStatus.NO_CONTENT,true,activityAndShiftStatusSettingsService.deleteActivityAndShiftStatusSettings(id));
    }

}
