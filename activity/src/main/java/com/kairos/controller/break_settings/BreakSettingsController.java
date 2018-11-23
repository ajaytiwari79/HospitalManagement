package com.kairos.controller.break_settings;

import com.kairos.dto.activity.break_settings.BreakSettingsDTO;
import com.kairos.service.break_settings.BreakSettingsService;
import com.kairos.utils.response.ResponseHandler;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.inject.Inject;
import java.math.BigInteger;
import java.util.Map;

import static com.kairos.constants.ApiConstants.*;


@RestController
@Api(API_V1)
@RequestMapping(API_V1)
public class BreakSettingsController {

    @Inject
    private BreakSettingsService breakSettingsService;

    @ApiOperation("Create break settings ")
    @PostMapping(COUNTRY_URL+API_EXPERTISE_BREAK_URL)
    //@PreAuthorize("@customPermissionEvaluator.isAuthorized()")
    public ResponseEntity<Map<String, Object>> createBreakSettings(@PathVariable Long countryId, @PathVariable Long expertiseId, @RequestBody @Validated BreakSettingsDTO breakSettingsDTO) {
        return ResponseHandler.generateResponse(HttpStatus.CREATED, true, breakSettingsService.createBreakSettings(countryId,expertiseId, breakSettingsDTO));
    }

    @ApiOperation("Get all break settings ")
    @GetMapping(COUNTRY_URL+API_EXPERTISE_BREAK_URL)
    //@PreAuthorize("@customPermissionEvaluator.isAuthorized()")
    public ResponseEntity<Map<String, Object>> getBreakSettings(@PathVariable Long countryId,@PathVariable Long expertiseId) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true, breakSettingsService.getBreakSettings(countryId,expertiseId));
    }

    @ApiOperation("Get remove a particular break settings from unit")
    @DeleteMapping(COUNTRY_URL+API_EXPERTISE_BREAK_URL+ "/{breakSettingsId}")
    public ResponseEntity<Map<String, Object>> deleteBreakSettings(@PathVariable BigInteger breakSettingsId) {
        return ResponseHandler.generateResponse(HttpStatus.OK,true,breakSettingsService.removeBreakSettings(breakSettingsId));
    }
    @ApiOperation("update a particular break settings for unit")
    @PutMapping(COUNTRY_URL+API_EXPERTISE_BREAK_URL+ "/{breakSettingsId}")
    public ResponseEntity<Map<String,Object>> updateBreakSettings(@PathVariable Long countryId, @PathVariable Long expertiseId,@PathVariable BigInteger breakSettingsId, @RequestBody @Validated BreakSettingsDTO breakSettingsDTO){
        return ResponseHandler.generateResponse(HttpStatus.ACCEPTED,true,breakSettingsService.updateBreakSettings(countryId,expertiseId,breakSettingsId,breakSettingsDTO));
    }

    @ApiOperation("Get all break settings  by expertise id")
    @GetMapping(UNIT_URL+API_EXPERTISE_BREAK_URL)
    //@PreAuthorize("@customPermissionEvaluator.isAuthorized()")
    public ResponseEntity<Map<String, Object>> getBreakSettingsByExpertiseId(@PathVariable Long expertiseId) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true, breakSettingsService.getBreakSettingsByExpertiseId(expertiseId));
    }


}
