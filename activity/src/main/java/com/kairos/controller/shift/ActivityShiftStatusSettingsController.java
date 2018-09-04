package com.kairos.controller.shift;
/*
 *Created By Pavan on 29/8/18
 *
 */

import com.kairos.activity.shift.ActivityShiftStatusSettingsDTO;
import com.kairos.service.shift.ActivityShiftStatusSettingsService;
import com.kairos.util.response.ResponseHandler;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.inject.Inject;
import javax.validation.Valid;
import java.math.BigInteger;
import java.util.Map;

import static com.kairos.constants.ApiConstants.API_ORGANIZATION_URL;
import static com.kairos.constants.ApiConstants.ACTIVITY_SHIFT_STATUS_SETTINGS_URL;

@RestController
@RequestMapping(API_ORGANIZATION_URL)
@Api(API_ORGANIZATION_URL)
public class ActivityShiftStatusSettingsController {

    @Inject
    private ActivityShiftStatusSettingsService activityShiftStatusSettingsService;


    @PostMapping(value = ACTIVITY_SHIFT_STATUS_SETTINGS_URL)
    @ApiOperation("create Activity and shift status setting")
    //@PreAuthorize("@customPermissionEvaluator.isAuthorized()")
    public ResponseEntity<Map<String,Object>> addActivityAndShiftStatusSetting(@PathVariable Long unitId, @Valid @RequestBody ActivityShiftStatusSettingsDTO activityShiftStatusSettingsDTO){
        return ResponseHandler.generateResponse(HttpStatus.OK,true, activityShiftStatusSettingsService.addActivityAndShiftStatusSetting(unitId, activityShiftStatusSettingsDTO));
    }

    @GetMapping(value = ACTIVITY_SHIFT_STATUS_SETTINGS_URL +"/activity/{activityId}")
    @ApiOperation("get All Activity and shift status setting")
    //@PreAuthorize("@customPermissionEvaluator.isAuthorized()")
    public ResponseEntity<Map<String,Object>> getAllActivityAndShiftStatusSettingsByActivityId(@PathVariable Long unitId,@PathVariable BigInteger activityId){
        return ResponseHandler.generateResponse(HttpStatus.OK,true, activityShiftStatusSettingsService.getActivityAndShiftStatusSettingsGroupedByStatus(unitId,activityId));
    }

    @PutMapping(value = ACTIVITY_SHIFT_STATUS_SETTINGS_URL)
    @ApiOperation("update Activity and shift status setting")
    //@PreAuthorize("@customPermissionEvaluator.isAuthorized()")
    public ResponseEntity<Map<String,Object>> updateActivityAndShiftStatusSettings(@PathVariable Long unitId,@RequestBody ActivityShiftStatusSettingsDTO activityShiftStatusSettingsDTO){
        return ResponseHandler.generateResponse(HttpStatus.OK,true, activityShiftStatusSettingsService.updateActivityAndShiftStatusSettings(unitId, activityShiftStatusSettingsDTO));
    }


}
