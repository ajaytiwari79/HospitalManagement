package com.kairos.controller.auto_gap_fill_settings;

import com.kairos.dto.activity.auto_gap_fill_settings.AutoFillGapSettingsDTO;
import com.kairos.enums.auto_gap_fill_settings.AutoFillGapSettingsRule;
import com.kairos.service.auto_gap_fill_settings.AutoFillGapSettingsService;
import com.kairos.utils.response.ResponseHandler;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.inject.Inject;
import java.math.BigInteger;
import java.time.LocalDate;
import java.util.Map;

import static com.kairos.constants.ApiConstants.*;

@RestController
@Api(API_V1)
@RequestMapping(API_V1)
public class AutoFillGapSettingsController {
    @Inject
    private AutoFillGapSettingsService autoFillGapSettingsService;

    @ApiOperation("Create gap settings for country")
    @PostMapping(COUNTRY_URL + AUTO_FILL_GAP_SETTINGS_URL)
    //@PreAuthorize("@customPermissionEvaluator.isAuthorized()")
    public ResponseEntity<Map<String, Object>> createAutoFillGapSettingsForCountry(@RequestBody @Validated AutoFillGapSettingsDTO autoFillGapSettingsDTO, @RequestParam String action, @RequestParam(value = "publish_date", required = false) LocalDate publishDate) {
        return ResponseHandler.generateResponse(HttpStatus.CREATED, true, autoFillGapSettingsService.createAutoFillGapSettings(autoFillGapSettingsDTO, action, publishDate, true));
    }

    @ApiOperation("update a particular gap settings for country")
    @PutMapping(COUNTRY_URL + AUTO_FILL_GAP_SETTINGS_URL)
    public ResponseEntity<Map<String,Object>> updateAutoFillGapSettingsForCountry(@RequestBody @Validated AutoFillGapSettingsDTO autoFillGapSettingsDTO, @RequestParam String action, @RequestParam(value = "publish_date", required = false) LocalDate publishDate){
        return ResponseHandler.generateResponse(HttpStatus.ACCEPTED,true, autoFillGapSettingsService.updateAutoFillGapSettings(autoFillGapSettingsDTO, action, publishDate, true));
    }

    @ApiOperation("Get all gap settings for country")
    @GetMapping(COUNTRY_URL + AUTO_FILL_GAP_SETTINGS_URL)
    //@PreAuthorize("@customPermissionEvaluator.isAuthorized()")
    public ResponseEntity<Map<String, Object>> getAllAutoFillGapSettingsForCountry(@PathVariable Long countryId) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true, autoFillGapSettingsService.getAllAutoFillGapSettings(countryId, true));
    }

    @ApiOperation("Delete gap settings for country")
    @DeleteMapping(COUNTRY_URL + AUTO_FILL_GAP_SETTINGS_URL + "/{autoFillGapSettingsId}")
    //@PreAuthorize("@customPermissionEvaluator.isAuthorized()")
    public ResponseEntity<Map<String, Object>> deleteAutoFillGapSettingsForCountry(@PathVariable BigInteger autoFillGapSettingsId) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true, autoFillGapSettingsService.deleteAutoFillGapSettings(autoFillGapSettingsId));
    }

    @ApiOperation("Create gap settings for unit")
    @PostMapping(UNIT_URL + AUTO_FILL_GAP_SETTINGS_URL)
    //@PreAuthorize("@customPermissionEvaluator.isAuthorized()")
    public ResponseEntity<Map<String, Object>> createAutoFillGapSettingsForUnit(@RequestBody @Validated AutoFillGapSettingsDTO autoFillGapSettingsDTO, @RequestParam String action, @RequestParam(value = "publish_date", required = false) LocalDate publishDate) {
        return ResponseHandler.generateResponse(HttpStatus.CREATED, true, autoFillGapSettingsService.createAutoFillGapSettings(autoFillGapSettingsDTO, action, publishDate, false));
    }

    @ApiOperation("update a particular gap settings for unit")
    @PutMapping(UNIT_URL + AUTO_FILL_GAP_SETTINGS_URL)
    public ResponseEntity<Map<String,Object>> updateAutoFillGapSettingsForUnit(@RequestBody @Validated AutoFillGapSettingsDTO autoFillGapSettingsDTO, @RequestParam String action, @RequestParam(value = "publish_date", required = false) LocalDate publishDate){
        return ResponseHandler.generateResponse(HttpStatus.ACCEPTED,true, autoFillGapSettingsService.updateAutoFillGapSettings(autoFillGapSettingsDTO, action, publishDate, false));
    }

    @ApiOperation("Get all gap settings for unit")
    @GetMapping(UNIT_URL + AUTO_FILL_GAP_SETTINGS_URL)
    //@PreAuthorize("@customPermissionEvaluator.isAuthorized()")
    public ResponseEntity<Map<String, Object>> getAllAutoFillGapSettingsForUnit(@PathVariable Long unitId) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true, autoFillGapSettingsService.getAllAutoFillGapSettings(unitId, false));
    }

    @ApiOperation("Delete gap settings for unit")
    @DeleteMapping(UNIT_URL + AUTO_FILL_GAP_SETTINGS_URL + "/{autoFillGapSettingsId}")
    //@PreAuthorize("@customPermissionEvaluator.isAuthorized()")
    public ResponseEntity<Map<String, Object>> deleteAutoFillGapSettingsForUnit(@PathVariable BigInteger autoFillGapSettingsId) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true, autoFillGapSettingsService.deleteAutoFillGapSettings(autoFillGapSettingsId));
    }

    @ApiOperation("Get all gap settings for unit")
    @GetMapping("/all_gap_settings_rules")
    //@PreAuthorize("@customPermissionEvaluator.isAuthorized()")
    public ResponseEntity<Map<String, Object>> getAllAutoFillGapSettingsRules() {
        return ResponseHandler.generateResponse(HttpStatus.OK, true, AutoFillGapSettingsRule.getAllAutoFillGapSettingsRules());
    }

}
