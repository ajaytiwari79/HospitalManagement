package com.kairos.activity.controller.unit_settings;

import com.kairos.activity.service.unit_settings.UnitSettingService;
import com.kairos.activity.util.response.ResponseHandler;
import com.kairos.response.dto.web.unit_settings.UnitAgeSettingDTO;
import com.kairos.response.dto.web.unit_settings.UnitSettingDTO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.inject.Inject;
import javax.validation.Valid;
import java.math.BigInteger;
import java.util.Map;

import static com.kairos.activity.constants.ApiConstants.API_ORGANIZATION_UNIT_URL;

@RestController
@RequestMapping(API_ORGANIZATION_UNIT_URL)
@Api(API_ORGANIZATION_UNIT_URL)
public class UnitSettingController {

    @Inject
    private UnitSettingService unitSettingService;

    @ApiOperation(value = "get unit age settings")
    @GetMapping(value = "/age_setting")
    //@PreAuthorize("@customPermissionEvaluator.isAuthorized()")
    public ResponseEntity<Map<String, Object>> getUnitAgeSettings(@PathVariable Long unitId) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true, unitSettingService.getUnitAgeSettings(unitId));
    }

    @ApiOperation(value = "update unit age settings")
    @PutMapping(value = "/age_setting")
    //@PreAuthorize("@customPermissionEvaluator.isAuthorized()")
    public ResponseEntity<Map<String, Object>> updateUnitAgeSettings(@PathVariable Long unitId,
                                                                             @RequestBody @Valid UnitAgeSettingDTO unitAgeSettingDTO) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true, unitSettingService.updateUnitAgeSettings(unitId,  unitAgeSettingDTO));
    }

    @ApiOperation(value = "get unit open shift phase settings")
    @GetMapping(value = "unit_setting/{unitSettingId}/open_shift_phase_setting")
    //@PreAuthorize("@customPermissionEvaluator.isAuthorized()")
    public ResponseEntity<Map<String, Object>> getOpenShiftPhaseSettings(@PathVariable Long unitId,@PathVariable BigInteger unitSettingId) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true, unitSettingService.getOpenShiftPhaseSettings(unitId,unitSettingId));
    }

    @ApiOperation(value = "update unit open shift phase settings")
    @PutMapping(value = "/unit_setting/{unitSettingId}/open_shift_phase_setting")
    //@PreAuthorize("@customPermissionEvaluator.isAuthorized()")
    public ResponseEntity<Map<String, Object>> updateOpenShiftPhaseSettings(@PathVariable Long unitId,@PathVariable BigInteger unitSettingId,
                                                                     @RequestBody @Valid UnitSettingDTO unitSettingDTO) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true, unitSettingService.updateOpenShiftPhaseSettings(unitId,  unitSettingId,unitSettingDTO));
    }
}
