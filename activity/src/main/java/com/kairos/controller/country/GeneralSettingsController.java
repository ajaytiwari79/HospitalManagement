package com.kairos.controller.country;

import com.kairos.persistence.model.country.GeneralSettings;
import com.kairos.service.country.GeneralSettingsService;
import com.kairos.utils.response.ResponseHandler;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.inject.Inject;
import java.util.Map;

import static com.kairos.constants.ApiConstants.*;

@RestController
@RequestMapping(API_V1)
@Api(API_V1)
public class GeneralSettingsController {

    @Inject private GeneralSettingsService generalSettingsService;

    @ApiOperation(value = "update Country General Setting")
    @PutMapping(value = COUNTRY_URL+"/general_settings")
    //@PreAuthorize("@customPermissionEvaluator.isAuthorized()")
    public ResponseEntity<Map<String, Object>> updateCountryGeneralSettings(@PathVariable Long countryId,@RequestBody GeneralSettings generalSettings) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true, generalSettingsService.updateCountryGeneralSettings(countryId, generalSettings));
    }

    @ApiOperation(value = "get Country General Setting")
    @GetMapping(value = COUNTRY_URL+"/general_settings")
    //@PreAuthorize("@customPermissionEvaluator.isAuthorized()")
    public ResponseEntity<Map<String, Object>> getCountryGeneralSettings(@PathVariable Long countryId) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true, generalSettingsService.getCountryGeneralSettings(countryId));
    }

    @ApiOperation(value = "update Country General Setting")
    @PutMapping(value = UNIT_URL+"/general_settings")
    //@PreAuthorize("@customPermissionEvaluator.isAuthorized()")
    public ResponseEntity<Map<String, Object>> updateUnitGeneralSettingsForUnit(@PathVariable Long unitId,@RequestBody GeneralSettings generalSettings) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true, generalSettingsService.updateUnitGeneralSettingsForUnit(unitId, generalSettings));
    }

    @ApiOperation(value = "get Country General Setting")
    @GetMapping(value = UNIT_URL+"/general_settings")
    //@PreAuthorize("@customPermissionEvaluator.isAuthorized()")
    public ResponseEntity<Map<String, Object>> getUnitGeneralSettings(@PathVariable Long unitId) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true, generalSettingsService.getUnitGeneralSettings(unitId));
    }
}
