package com.kairos.controller.staff_settings;

import com.kairos.service.staff_settings.StaffActivitySettingService;
import com.kairos.dto.user.staff.staff_settings.StaffActivitySettingDTO;
import com.kairos.dto.user.staff.staff_settings.StaffAndActivitySettingWrapper;
import com.kairos.utils.response.ResponseHandler;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.inject.Inject;
import javax.validation.Valid;
import java.math.BigInteger;
import java.util.List;
import java.util.Map;

import static com.kairos.constants.ApiConstants.API_ORGANIZATION_UNIT_URL;

@RestController
@RequestMapping(API_ORGANIZATION_UNIT_URL)
@Api(API_ORGANIZATION_UNIT_URL)
public class StaffActivitySettingController {

    @Inject private StaffActivitySettingService staffActivitySettingService;

    @ApiOperation("Create Staff Personalized activity settings")
    @PostMapping(value = "/staff_activity_setting")
    //  @PreAuthorize("@customPermissionEvaluator.isAuthorized()")
    public ResponseEntity<Map<String, Object>> createStaffActivitySetting(@PathVariable Long unitId, @RequestBody @Valid StaffActivitySettingDTO staffActivitySettingDTO) {

        return ResponseHandler.generateResponse(HttpStatus.OK, true, staffActivitySettingService.createStaffActivitySetting(unitId, staffActivitySettingDTO));
    }

    @ApiOperation("Get Staff Personalized activity settings")
    @GetMapping(value = "/staff_activity_setting")
    //  @PreAuthorize("@customPermissionEvaluator.isAuthorized()")
    public ResponseEntity<Map<String, Object>> getStaffActivitySettings( @PathVariable Long unitId) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true, staffActivitySettingService.getStaffActivitySettings(unitId));
    }


    @ApiOperation("update Staff Personalized activity settings")
    @PutMapping(value = "/staff_activity_setting/{staffActivitySettingId}")
    //  @PreAuthorize("@customPermissionEvaluator.isAuthorized()")
    public ResponseEntity<Map<String, Object>> updateStaffActivitySettings(@PathVariable Long unitId, @PathVariable BigInteger staffActivitySettingId, @RequestBody @Valid StaffActivitySettingDTO staffActivitySettingDTO) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true, staffActivitySettingService.updateStaffActivitySettings(staffActivitySettingId,unitId,staffActivitySettingDTO));
    }

    @ApiOperation("delete Staff Personalized activity settings")
    @DeleteMapping(value = "/staff_activity_setting/{staffActivitySettingId}")
    //  @PreAuthorize("@customPermissionEvaluator.isAuthorized()")
    public ResponseEntity<Map<String, Object>> deleteStaffActivitySettings(@PathVariable Long unitId, @PathVariable BigInteger staffActivitySettingId) {
        staffActivitySettingService.deleteStaffActivitySettings(staffActivitySettingId);
        return ResponseHandler.generateResponse(HttpStatus.OK, true, true);
    }

    @ApiOperation("Get Staff Personalized activity settings")
    @GetMapping(value = "activity/{activityId}/default_activity_setting")
    //  @PreAuthorize("@customPermissionEvaluator.isAuthorized()")
    public ResponseEntity<Map<String, Object>> getDefaultStaffActivitySettings(@PathVariable Long unitId,@PathVariable BigInteger activityId) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true, staffActivitySettingService.getDefaultStaffActivitySettings(unitId,activityId));
    }

    @ApiOperation("Create Staff Personalized activity settings")
    @PostMapping(value = "/staff_activity_setting/assign")
    //  @PreAuthorize("@customPermissionEvaluator.isAuthorized()")
    public ResponseEntity<Map<String, Object>> assignActivitySettingToStaffs(@PathVariable Long unitId, @RequestBody @Valid StaffAndActivitySettingWrapper staffAndActivitySettingWrapper) {

        return ResponseHandler.generateResponse(HttpStatus.OK, true, staffActivitySettingService.assignActivitySettingToStaffs(unitId, staffAndActivitySettingWrapper));
    }

    @ApiOperation("Get Staff Personalized activity settings")
    @GetMapping(value = "/staff_activity_setting/staff/{staffId}")
    //  @PreAuthorize("@customPermissionEvaluator.isAuthorized()")
    public ResponseEntity<Map<String, Object>> getStaffSpecificActivitySettings( @PathVariable Long unitId,@PathVariable Long staffId) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true, staffActivitySettingService.getStaffSpecificActivitySettings(unitId,staffId));
    }

    @ApiOperation("Get Staff Personalized activity settings")
    @GetMapping(value = "/staff_activity_setting/{staffActivitySettingId}")
    //  @PreAuthorize("@customPermissionEvaluator.isAuthorized()")
    public ResponseEntity<Map<String, Object>> getStaffActivitySettings( @PathVariable Long unitId,@PathVariable BigInteger staffActivitySettingId) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true, staffActivitySettingService.getStaffActivitySettingsById(unitId,staffActivitySettingId));
    }

    @ApiOperation("update bulk Staff Personalized activity settings")
    @PutMapping(value = "/staff_activity_setting/staff/{staffId}")
    //  @PreAuthorize("@customPermissionEvaluator.isAuthorized()")
    public ResponseEntity<Map<String, Object>> updateBulkStaffActivitySettings(@PathVariable Long unitId,@PathVariable Long staffId, @RequestBody @Valid List<StaffActivitySettingDTO> staffActivitySettingDTO) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true, staffActivitySettingService.updateBulkStaffActivitySettings(unitId,staffId,staffActivitySettingDTO));
    }
}
