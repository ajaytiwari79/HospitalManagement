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

    @ApiOperation("Create granularity setting")
    @PostMapping(COUNTRY_URL+"/granularity_setting")
    //@PreAuthorize("@customPermissionEvaluator.isAuthorized()")
    public ResponseEntity<Map<String, Object>> createGranularitySettingsForCountry(@RequestBody GranularitySettingDTO granularitySettingDTO) {
        return ResponseHandler.generateResponse(HttpStatus.CREATED, true, granularitySettingService.createGranularitySettingForCountry(granularitySettingDTO));
    }

    @ApiOperation("Update granularity setting")
    @PutMapping(COUNTRY_URL+"/granularity_setting")
    //@PreAuthorize("@customPermissionEvaluator.isAuthorized()")
    public ResponseEntity<Map<String, Object>> updateGranularitySettingsForCountry(@RequestBody List<GranularitySettingDTO> granularitySettingDTOS) {
        return ResponseHandler.generateResponse(HttpStatus.CREATED, true, granularitySettingService.updateGranularitySettingsForCountry(granularitySettingDTOS));
    }

    @ApiOperation("Get all granularity setting")
    @GetMapping(COUNTRY_URL+"/granularity_setting")
    //@PreAuthorize("@customPermissionEvaluator.isAuthorized()")
    public ResponseEntity<Map<String, Object>> getGranularitySettingsForCountry(@PathVariable Long countryId) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true, granularitySettingService.getGranularitySettingsForCountry(countryId));
    }

    @ApiOperation("Delete granularity setting")
    @DeleteMapping(COUNTRY_URL+"/granularity_setting")
    public ResponseEntity<Map<String, Object>> deleteGranularitySettingsForCountry(@PathVariable Long countryId, @PathParam("organisationTypeId") Long organisationTypeId) {
        return ResponseHandler.generateResponse(HttpStatus.OK,true,granularitySettingService.deleteGranularitySettingsForCountry(countryId, organisationTypeId));
    }


}
