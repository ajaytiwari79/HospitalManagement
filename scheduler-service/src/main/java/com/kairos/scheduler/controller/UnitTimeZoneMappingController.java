package com.kairos.scheduler.controller;

import com.kairos.scheduler.service.UnitTimeZoneMappingService;
import com.kairos.scheduler.utils.ResponseHandler;
import io.swagger.annotations.Api;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.inject.Inject;
import java.util.Map;

import static com.kairos.scheduler.constants.ApiConstants.API_UNIT_TIMEZONE_MAPPING_URL;

@RestController
@RequestMapping(API_UNIT_TIMEZONE_MAPPING_URL)
@Api(API_UNIT_TIMEZONE_MAPPING_URL)
public class UnitTimeZoneMappingController {


    @Inject
    private UnitTimeZoneMappingService unitTimeZoneMappingService;

    @PostMapping(value="")
    public ResponseEntity<Map<String, Object>> createUnitTimeZoneMapping(@RequestParam(value = "timezone",required = true) String timeZone, @PathVariable Long unitId) {

        return ResponseHandler.generateResponse(HttpStatus.OK,true,unitTimeZoneMappingService.createUnitTimezoneMapping(unitId,timeZone));
    }

    @PutMapping(value="")
    public ResponseEntity<Map<String, Object>> updateUnitTimeZoneMapping(@RequestParam(value = "timezone",required = true) String timezone, @PathVariable Long unitId) {

        return ResponseHandler.generateResponse(HttpStatus.OK,true,unitTimeZoneMappingService.updateUnitTimezoneMapping(timezone,unitId));
    }
    @GetMapping(value="")
    public ResponseEntity<Map<String, Object>> getUnitTimeZoneMapping(@PathVariable Long unitId) {

        return ResponseHandler.generateResponse(HttpStatus.OK,true,unitTimeZoneMappingService.getUnitTimezoneMapping(unitId));
    }
}
