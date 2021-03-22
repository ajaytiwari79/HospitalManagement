package com.kairos.controller.shift;

import com.kairos.dto.activity.shift.CoverShiftSettingDTO;
import com.kairos.dto.activity.shift.CoverShift;
import com.kairos.persistence.model.shift.CoverShiftSetting;
import com.kairos.service.shift.CoverShiftService;
import com.kairos.utils.response.ResponseHandler;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.inject.Inject;
import java.math.BigInteger;
import java.util.Map;

import static com.kairos.constants.ApiConstants.API_UNIT_URL;

@RestController
@RequestMapping(API_UNIT_URL)
@Api(API_UNIT_URL)
public class CoverShiftController {

    @Inject private CoverShiftService coverShiftService;

    @ApiOperation("get eligible staffs")
    @PostMapping(value = "/get_eligible_staffs")
    //  @PreAuthorize("@customPermissionEvaluator.isAuthorized()")
    public ResponseEntity<Map<String, Object>> getEligibleStaffs(@PathVariable Long unitId, @RequestParam BigInteger shiftId, @RequestBody CoverShiftSetting coverShiftSetting) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true, coverShiftService.getEligibleStaffs(shiftId,coverShiftSetting));
    }

    @ApiOperation("create cover shift setting by unit")
    @PostMapping(value = "/cover_shift_setting")
    //  @PreAuthorize("@customPermissionEvaluator.isAuthorized()")
    public ResponseEntity<Map<String, Object>> createCoverShiftSettingByUnit(@PathVariable Long unitId,@RequestBody CoverShiftSettingDTO coverShiftSettingDTO) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true, coverShiftService.createCoverShiftSettingByUnit(unitId,coverShiftSettingDTO));
    }

    @ApiOperation("update cover shift setting by unit")
    @PutMapping(value = "/cover_shift_setting")
    //  @PreAuthorize("@customPermissionEvaluator.isAuthorized()")
    public ResponseEntity<Map<String, Object>> updateCoverShiftSettingByUnit(@PathVariable Long unitId,@RequestBody CoverShiftSettingDTO coverShiftSettingDTO) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true, coverShiftService.updateCoverShiftSettingByUnit(unitId,coverShiftSettingDTO));
    }

    @ApiOperation("get cover shift setting by unit")
    @GetMapping(value = "/cover_shift_setting")
    //  @PreAuthorize("@customPermissionEvaluator.isAuthorized()")
    public ResponseEntity<Map<String, Object>> getCoverShiftSettingByUnit(@PathVariable Long unitId) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true, coverShiftService.getCoverShiftSettingByUnit(unitId));
    }

    @ApiOperation("get cover shift setting by unit")
    @GetMapping(value = "/cover_shift_details/{shiftId}")
    //  @PreAuthorize("@customPermissionEvaluator.isAuthorized()")
    public ResponseEntity<Map<String, Object>> getCoverShiftByShiftId(@PathVariable BigInteger shiftId) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true, coverShiftService.getCoverShiftDetails(shiftId));
    }

    @ApiOperation("get cover shift setting by unit")
    @PutMapping(value = "/update_cover_shift_details/{shiftId}")
    //  @PreAuthorize("@customPermissionEvaluator.isAuthorized()")
    public ResponseEntity<Map<String, Object>> updateCoverShiftByShiftId(@PathVariable BigInteger shiftId, @RequestBody CoverShift coverShift) {
        coverShiftService.updateCoverShiftDetails(shiftId,coverShift);
        return ResponseHandler.generateResponse(HttpStatus.OK, true, null);
    }
}
