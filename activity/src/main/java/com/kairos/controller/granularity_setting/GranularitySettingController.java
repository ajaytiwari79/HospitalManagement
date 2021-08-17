package com.kairos.controller.granularity_setting;

import com.kairos.dto.activity.granularity_setting.GranularitySettingDTO;
import com.kairos.service.granularity_setting.GranularitySettingService;
import com.kairos.utils.response.ResponseHandler;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.inject.Inject;
import javax.websocket.server.PathParam;
import java.util.List;
import java.util.Map;

import static com.kairos.constants.ApiConstants.*;

@RestController
@Api(API_V1)
@RequestMapping(API_V1)
public class GranularitySettingController {

    @Inject private GranularitySettingService granularitySettingService;

    @ApiOperation("Create granularity setting for country")
    @PostMapping(COUNTRY_URL+"/granularity_setting")
    //@PreAuthorize("@customPermissionEvaluator.isAuthorized()")
    public ResponseEntity<Map<String, Object>> createGranularitySettingsForCountry(@RequestBody GranularitySettingDTO granularitySettingDTO) {
        return ResponseHandler.generateResponse(HttpStatus.CREATED, true, granularitySettingService.createGranularitySettingForCountry(granularitySettingDTO));
    }

    @ApiOperation("Update granularity setting for country")
    @PutMapping(COUNTRY_URL+"/granularity_setting")
    //@PreAuthorize("@customPermissionEvaluator.isAuthorized()")
    public ResponseEntity<Map<String, Object>> updateGranularitySettingsForCountry(@RequestBody List<GranularitySettingDTO> granularitySettingDTOS) {
        return ResponseHandler.generateResponse(HttpStatus.CREATED, true, granularitySettingService.updateGranularitySettingsForCountry(granularitySettingDTOS));
    }

    @ApiOperation("Get all granularity setting for country")
    @GetMapping(COUNTRY_URL+"/granularity_setting")
    //@PreAuthorize("@customPermissionEvaluator.isAuthorized()")
    public ResponseEntity<Map<String, Object>> getGranularitySettingsForCountry(@PathVariable Long countryId) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true, granularitySettingService.getGranularitySettingsForCountry(countryId));
    }

    @ApiOperation("Delete granularity setting for country")
    @DeleteMapping(COUNTRY_URL+"/granularity_setting")
    public ResponseEntity<Map<String, Object>> deleteGranularitySettingsForCountry(@PathVariable Long countryId, @PathParam("organisationTypeId") Long organisationTypeId) {
        return ResponseHandler.generateResponse(HttpStatus.OK,true,granularitySettingService.deleteGranularitySettingsForCountry(countryId, organisationTypeId));
    }

    @ApiOperation("Update granularity setting for unit")
    @PutMapping(UNIT_URL+"/granularity_setting")
    //@PreAuthorize("@customPermissionEvaluator.isAuthorized()")
    public ResponseEntity<Map<String, Object>> updateGranularitySettingsForUnit(@PathVariable Long unitId, @RequestBody GranularitySettingDTO granularitySettingDTO) {
        return ResponseHandler.generateResponse(HttpStatus.CREATED, true, granularitySettingService.updateGranularitySettingsForUnit(unitId, granularitySettingDTO));
    }

    @ApiOperation("Get all granularity setting for unit")
    @GetMapping(UNIT_URL+"/granularity_setting")
    //@PreAuthorize("@customPermissionEvaluator.isAuthorized()")
    public ResponseEntity<Map<String, Object>> getCurrentGranularitySettingForUnit(@PathVariable Long unitId) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true, granularitySettingService.getCurrentGranularitySettingForUnit(unitId));
    }

    //this api for create granularity setting for already create org type and unit
    @ApiOperation("create all granularity setting for unit and orgType")
    @PostMapping(COUNTRY_URL+"/granularity_setting/data")
    //@PreAuthorize("@customPermissionEvaluator.isAuthorized()")
    public ResponseEntity<Map<String, Object>> createData(@PathVariable Long countryId) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true, granularitySettingService.createDefaultDataForCountry(countryId));
    }
}
