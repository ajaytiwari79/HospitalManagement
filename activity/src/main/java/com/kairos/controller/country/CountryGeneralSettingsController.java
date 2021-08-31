package com.kairos.controller.country;

import com.kairos.persistence.model.country.CountryGeneralSettings;
import com.kairos.service.country.CountryGeneralSettingsService;
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
public class CountryGeneralSettingsController {

    @Inject private CountryGeneralSettingsService countryGeneralSettingsService;

    @ApiOperation(value = "update Country General Setting")
    @PutMapping(value = COUNTRY_URL+"/country_general_settings")
    //@PreAuthorize("@customPermissionEvaluator.isAuthorized()")
    public ResponseEntity<Map<String, Object>> updateCountryGeneralSettings(@PathVariable Long countryId,@RequestBody CountryGeneralSettings countryGeneralSettings) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true, countryGeneralSettingsService.updateCountryGeneralSettings(countryId,countryGeneralSettings));
    }

    @ApiOperation(value = "get Country General Setting")
    @GetMapping(value = COUNTRY_URL+"/country_general_settings")
    //@PreAuthorize("@customPermissionEvaluator.isAuthorized()")
    public ResponseEntity<Map<String, Object>> getCountryGeneralSettings(@PathVariable Long countryId) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true, countryGeneralSettingsService.getCountryGeneralSettings(countryId));
    }

    @ApiOperation(value = "update Country General Setting")
    @PutMapping(value = UNIT_URL+"/country_general_settings")
    //@PreAuthorize("@customPermissionEvaluator.isAuthorized()")
    public ResponseEntity<Map<String, Object>> updateCountryGeneralSettingsForUnit(@PathVariable Long unitId,@RequestBody CountryGeneralSettings countryGeneralSettings) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true, countryGeneralSettingsService.updateCountryGeneralSettingsForUnit(unitId,countryGeneralSettings));
    }

    @ApiOperation(value = "get Country General Setting")
    @GetMapping(value = UNIT_URL+"/country_general_settings")
    //@PreAuthorize("@customPermissionEvaluator.isAuthorized()")
    public ResponseEntity<Map<String, Object>> getCountryGeneralSettingsForUnit(@PathVariable Long unitId) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true, countryGeneralSettingsService.getCountryGeneralSettingsForUnit(unitId));
    }
}
