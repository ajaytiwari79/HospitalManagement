package com.kairos.controller.break_settings;

import com.kairos.service.break_settings.BreakSettingsService;
import com.kairos.utils.response.ResponseHandler;
import com.kairos.dto.activity.break_settings.BreakSettingsDTO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.inject.Inject;
import javax.validation.Valid;
import java.math.BigInteger;
import java.util.Map;

import static com.kairos.constants.ApiConstants.API_ORGANIZATION_UNIT_URL;


@RestController
@Api(API_ORGANIZATION_UNIT_URL)
@RequestMapping(API_ORGANIZATION_UNIT_URL)
public class BreakSettingsController {

    @Inject
    private BreakSettingsService breakSettingsService;

    @ApiOperation("Create break settings ")
    @PostMapping(value = "/break")
    //@PreAuthorize("@customPermissionEvaluator.isAuthorized()")
    public ResponseEntity<Map<String, Object>> createBreakSettings(@PathVariable Long unitId, @RequestBody @Valid BreakSettingsDTO breakSettingsDTO) {
        return ResponseHandler.generateResponse(HttpStatus.CREATED, true, breakSettingsService.createBreakSettings(unitId, breakSettingsDTO));
    }

    @ApiOperation("Get all break settings ")
    @GetMapping(value = "/break")
    //@PreAuthorize("@customPermissionEvaluator.isAuthorized()")
    public ResponseEntity<Map<String, Object>> getBreakSettings(@PathVariable Long unitId) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true, breakSettingsService.getBreakSettings(unitId));
    }

    @ApiOperation("Get remove a particular break settings from unit")
    @DeleteMapping(value = "/break/{breakSettingsId}")
    public ResponseEntity<Map<String, Object>> deleteBreakSettings(@PathVariable Long unitId, @PathVariable BigInteger breakSettingsId) {
        return ResponseHandler.generateResponse(HttpStatus.OK,true,breakSettingsService.removeBreakSettings(unitId,breakSettingsId));
    }
    @ApiOperation("update a particular break settings for unit")
    @PutMapping(value = "/break/{breakSettingsId}")
    public ResponseEntity<Map<String,Object>> updateBreakSettings(@PathVariable Long unitId,@PathVariable BigInteger breakSettingsId, @RequestBody @Valid BreakSettingsDTO breakSettingsDTO){
        return ResponseHandler.generateResponse(HttpStatus.ACCEPTED,true,breakSettingsService.updateBreakSettings(unitId,breakSettingsId,breakSettingsDTO));
    }
}
