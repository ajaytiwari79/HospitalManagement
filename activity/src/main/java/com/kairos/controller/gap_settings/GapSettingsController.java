package com.kairos.controller.gap_settings;

import com.kairos.dto.activity.gap_settings.GapSettingsDTO;
import com.kairos.enums.gap_settings.GapSettingsRule;
import com.kairos.service.gap_settings.GapSettingsService;
import com.kairos.utils.response.ResponseHandler;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.inject.Inject;
import java.util.Map;

import static com.kairos.constants.ApiConstants.*;

@RestController
@Api(API_V1)
@RequestMapping(API_V1)
public class GapSettingsController {
    @Inject
    private GapSettingsService gapSettingsService;

    @ApiOperation("Create gap settings for country")
    @PostMapping(COUNTRY_URL + GAP_FILLING_SETTINGS_URL)
    //@PreAuthorize("@customPermissionEvaluator.isAuthorized()")
    public ResponseEntity<Map<String, Object>> createGapSettingsForCountry(@RequestBody @Validated GapSettingsDTO gapSettingsDTO) {
        return ResponseHandler.generateResponse(HttpStatus.CREATED, true, gapSettingsService.createGapSettings(gapSettingsDTO, true));
    }

    @ApiOperation("update a particular gap settings for country")
    @PutMapping(COUNTRY_URL + GAP_FILLING_SETTINGS_URL)
    public ResponseEntity<Map<String,Object>> updateGapSettingsForCountry(@RequestBody @Validated GapSettingsDTO gapSettingsDTO){
        return ResponseHandler.generateResponse(HttpStatus.ACCEPTED,true, gapSettingsService.updateGapSettings(gapSettingsDTO, true));
    }

    @ApiOperation("Get all gap settings for country")
    @GetMapping(COUNTRY_URL + GAP_FILLING_SETTINGS_URL)
    //@PreAuthorize("@customPermissionEvaluator.isAuthorized()")
    public ResponseEntity<Map<String, Object>> getAllGapSettingsForCountry(@PathVariable Long countryId) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true, gapSettingsService.getAllGapSettings(countryId, true));
    }

    @ApiOperation("Create gap settings for unit")
    @PostMapping(UNIT_URL + GAP_FILLING_SETTINGS_URL)
    //@PreAuthorize("@customPermissionEvaluator.isAuthorized()")
    public ResponseEntity<Map<String, Object>> createGapSettingsForUnit(@RequestBody @Validated GapSettingsDTO gapSettingsDTO) {
        return ResponseHandler.generateResponse(HttpStatus.CREATED, true, gapSettingsService.createGapSettings(gapSettingsDTO, false));
    }

    @ApiOperation("update a particular gap settings for unit")
    @PutMapping(UNIT_URL + GAP_FILLING_SETTINGS_URL)
    public ResponseEntity<Map<String,Object>> updateGapSettingsForUnit(@RequestBody @Validated GapSettingsDTO gapSettingsDTO){
        return ResponseHandler.generateResponse(HttpStatus.ACCEPTED,true, gapSettingsService.updateGapSettings(gapSettingsDTO, false));
    }

    @ApiOperation("Get all gap settings for unit")
    @GetMapping(UNIT_URL + GAP_FILLING_SETTINGS_URL)
    //@PreAuthorize("@customPermissionEvaluator.isAuthorized()")
    public ResponseEntity<Map<String, Object>> getAllGapSettingsForUnit(@PathVariable Long unitId) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true, gapSettingsService.getAllGapSettings(unitId, false));
    }

    @ApiOperation("Get all gap settings for unit")
    @GetMapping("/all_gap_setings_rules")
    //@PreAuthorize("@customPermissionEvaluator.isAuthorized()")
    public ResponseEntity<Map<String, Object>> getAllGapSettingsRules() {
        return ResponseHandler.generateResponse(HttpStatus.OK, true, GapSettingsRule.getAllGapSettingsRules());
    }

}
