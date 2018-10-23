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

import static com.kairos.constants.ApiConstants.API_EXPERTISE_BREAK_URL;


@RestController
@Api(API_EXPERTISE_BREAK_URL)
@RequestMapping(API_EXPERTISE_BREAK_URL)
public class BreakSettingsController {

    @Inject
    private BreakSettingsService breakSettingsService;

    @ApiOperation("Create break settings ")
    @PostMapping(value = "/break")
    //@PreAuthorize("@customPermissionEvaluator.isAuthorized()")
    public ResponseEntity<Map<String, Object>> createBreakSettings(@PathVariable Long countryId, @PathVariable Long expertiseId, @RequestBody @Validated BreakSettingsDTO breakSettingsDTO) {
        return ResponseHandler.generateResponse(HttpStatus.CREATED, true, breakSettingsService.createBreakSettings(countryId,expertiseId, breakSettingsDTO));
    }

    @ApiOperation("Get all break settings ")
    @GetMapping(value = "/break")
    //@PreAuthorize("@customPermissionEvaluator.isAuthorized()")
    public ResponseEntity<Map<String, Object>> getBreakSettings(@PathVariable Long countryId,@PathVariable Long expertiseId) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true, breakSettingsService.getBreakSettings(countryId,expertiseId));
    }

    @ApiOperation("Get remove a particular break settings from unit")
    @DeleteMapping(value = "/break/{breakSettingsId}")
    public ResponseEntity<Map<String, Object>> deleteBreakSettings(@PathVariable BigInteger breakSettingsId) {
        return ResponseHandler.generateResponse(HttpStatus.OK,true,breakSettingsService.removeBreakSettings(breakSettingsId));
    }
    @ApiOperation("update a particular break settings for unit")
    @PutMapping(value = "/break/{breakSettingsId}")
    public ResponseEntity<Map<String,Object>> updateBreakSettings(@PathVariable Long countryId, @PathVariable Long expertiseId,@PathVariable BigInteger breakSettingsId, @RequestBody @Validated BreakSettingsDTO breakSettingsDTO){
        return ResponseHandler.generateResponse(HttpStatus.ACCEPTED,true,breakSettingsService.updateBreakSettings(countryId,expertiseId,breakSettingsId,breakSettingsDTO));
    }
}
