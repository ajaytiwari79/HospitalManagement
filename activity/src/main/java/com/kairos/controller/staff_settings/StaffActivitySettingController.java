package com.kairos.controller.staff_settings;

import com.kairos.service.staff_settings.StaffActivitySettingService;
import com.kairos.user.staff.staff_settings.StaffActivitySettingDTO;
import com.kairos.util.response.ResponseHandler;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.inject.Inject;
import javax.validation.Valid;
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
    public ResponseEntity<Map<String, Object>> createShift(@PathVariable Long unitId, @RequestBody @Valid StaffActivitySettingDTO staffActivitySettingDTO) {

        return ResponseHandler.generateResponse(HttpStatus.OK, true, staffActivitySettingService.createStaffActivitySetting(unitId, staffActivitySettingDTO));
    }

    @ApiOperation("Get Staff Personalized activity settings")
    @GetMapping(value = "/staff_activity_setting")
    //  @PreAuthorize("@customPermissionEvaluator.isAuthorized()")
    public ResponseEntity<Map<String, Object>> getShiftByStaffId( @PathVariable Long unitId) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true, staffActivitySettingService.getStaffActivitySettings(unitId));
    }


    @ApiOperation("update a Shift of a staff")
    @PutMapping(value = "/shift")
    //  @PreAuthorize("@customPermissionEvaluator.isAuthorized()")
    public ResponseEntity<Map<String, Object>> updateShift(@PathVariable Long organizationId, @PathVariable Long unitId, @RequestParam("type") String type, @RequestBody @Valid ShiftDTO shiftDTO) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true, shiftService.updateShift(organizationId, shiftDTO, type));
    }

    @ApiOperation("delete a Shift of a staff")
    @DeleteMapping(value = "/shift/{shiftId}")
    //  @PreAuthorize("@customPermissionEvaluator.isAuthorized()")
    public ResponseEntity<Map<String, Object>> deleteShift(@PathVariable BigInteger shiftId) {
        shiftService.deleteShift(shiftId);
        return ResponseHandler.generateResponse(HttpStatus.OK, true, true);
    }
}
