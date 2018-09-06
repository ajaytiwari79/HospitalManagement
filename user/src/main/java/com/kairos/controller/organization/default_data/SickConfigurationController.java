package com.kairos.controller.organization.default_data;

import com.kairos.service.organization_meta_data.SickConfigurationService;
import com.kairos.util.response.ResponseHandler;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.inject.Inject;
import java.math.BigInteger;
import java.util.Map;
import java.util.Set;

import static com.kairos.constants.ApiConstants.API_SICK_SETTINGS_URL;

/**
 * CreatedBy vipulpandey on 29/8/18
 **/
@RestController
@RequestMapping(API_SICK_SETTINGS_URL)
@Api(API_SICK_SETTINGS_URL)
public class SickConfigurationController {
    @Inject
    private SickConfigurationService sickConfigurationService;

    @ApiOperation(value = "api used to save the sick settings of the organization")
    @PostMapping
    public ResponseEntity<Map<String,Object>> saveSickSettingsOfUnit(@RequestBody Set<BigInteger> timeTypes, @PathVariable Long unitId){
        return ResponseHandler.generateResponse(HttpStatus.OK,true, sickConfigurationService.saveSickSettingsOfUnit(unitId,timeTypes));
    }

    @ApiOperation(value = "used to get sick settings of the organization")
    @GetMapping
    public ResponseEntity<Map<String,Object>> getSickSettingsWithDefaultData(@PathVariable Long unitId){
        return ResponseHandler.generateResponse(HttpStatus.OK  ,true,
                sickConfigurationService.getSickSettingsAndDefaultDataOfUnit(unitId));
    }

    @ApiOperation(value = "used to get sick settings of the organization")
    @GetMapping("/default")
    public ResponseEntity<Map<String,Object>> getSickSettingsOfUnit(@PathVariable Long unitId){
        return ResponseHandler.generateResponse(HttpStatus.OK  ,true,
                sickConfigurationService.getSickSettingsOfUnit(unitId));
    }

}
