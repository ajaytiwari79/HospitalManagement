package com.kairos.controller.period;

import com.kairos.service.period.PeriodSettingsService;
import com.kairos.utils.response.ResponseHandler;
import com.kairos.dto.activity.period.PeriodSettingsDTO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.inject.Inject;
import javax.validation.Valid;
import java.util.Map;

import static com.kairos.constants.ApiConstants.API_ORGANIZATION_UNIT_URL;

/**
 * Created by prerna on 30/3/18.
 */
@RestController()
@Api(API_ORGANIZATION_UNIT_URL)
@RequestMapping(API_ORGANIZATION_UNIT_URL)
public class PeriodSettingsController {

    @Inject
    PeriodSettingsService periodSettingsService;

    @ApiOperation(value = "Set default period settings of Organization")
    @PostMapping(value="/period_setting")
    //@PreAuthorize("@customPermissionEvaluator.isAuthorized()")
    public ResponseEntity<Map<String, Object>> createDefaultPeriodSettings(@PathVariable Long unitId) {
        periodSettingsService.createDefaultPeriodSettings(unitId);
        return ResponseHandler.generateResponse(HttpStatus.OK, true, null);
    }

    @ApiOperation(value = "get period settings of Organization")
    @GetMapping(value="/period_setting")
    //@PreAuthorize("@customPermissionEvaluator.isAuthorized()")
    public ResponseEntity<Map<String, Object>> getPeriodSettings(@PathVariable Long unitId) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true, periodSettingsService.getPeriodSettings(unitId));
    }

    @ApiOperation(value = "update period settings of Organization")
    @PutMapping(value="/period_setting")
    //@PreAuthorize("@customPermissionEvaluator.isAuthorized()")
    public ResponseEntity<Map<String, Object>> updatePeriodSettings(@PathVariable Long unitId, @RequestBody @Valid PeriodSettingsDTO periodSettingsDTO) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true, periodSettingsService.updatePeriodSettings(unitId, periodSettingsDTO));
    }


}