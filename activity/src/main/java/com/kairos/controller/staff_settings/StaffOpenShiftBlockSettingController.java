package com.kairos.controller.staff_settings;

import com.kairos.service.staff_settings.StaffOpenShiftBlockSettingService;
import com.kairos.dto.user.staff.staff.StaffPreferencesDTO;
import com.kairos.utils.response.ResponseHandler;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.inject.Inject;
import java.util.Map;

import static com.kairos.constants.ApiConstants.API_ORGANIZATION_UNIT_URL;

/*
 *Created By Pavan on 17/8/18
 *
 */
@RestController
@RequestMapping(API_ORGANIZATION_UNIT_URL+"/staff")
@Api(API_ORGANIZATION_UNIT_URL+"/staff")
public class StaffOpenShiftBlockSettingController {

    @Inject private StaffOpenShiftBlockSettingService staffOpenShiftBlockSettingService;

    @ApiOperation(value = "Staff personalized view in daily view")
    @PutMapping(value = "/open_shift_block_setting/daily_view")
    // @PreAuthorize("@customPermissionEvaluator.isAuthorized()")
    public ResponseEntity<Map<String, Object>> blockOpenShiftByStaff(@PathVariable Long unitId, @RequestBody StaffPreferencesDTO staffPreferencesDTO){
        return ResponseHandler.generateResponse(HttpStatus.OK, true, staffOpenShiftBlockSettingService.savePersonalizedSettings(unitId,staffPreferencesDTO));
    }
}
