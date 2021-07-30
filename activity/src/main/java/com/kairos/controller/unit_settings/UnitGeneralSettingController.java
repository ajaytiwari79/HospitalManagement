package com.kairos.controller.unit_settings;

import com.kairos.dto.activity.unit_settings.UnitGeneralSettingDTO;
import com.kairos.service.unit_settings.UnitGeneralSettingService;
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
@Api(value = API_V1)
public class UnitGeneralSettingController {
    @Inject
    UnitGeneralSettingService unitGeneralSettingService;

    @ApiOperation(value = "get  all absence ranking")
    @PutMapping(value = UNIT_URL + "/general_settings")
    public ResponseEntity<Map<String, Object>> updateGeneralSetting(@PathVariable Long unotId, @RequestBody UnitGeneralSettingDTO unitGeneralSettingDTO) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true, unitGeneralSettingService.updateGeneralSetting(unotId, unitGeneralSettingDTO));
    }

    @ApiOperation(value = "get  all absence ranking")
    @GetMapping(value = UNIT_URL + "/general_settings")
    public ResponseEntity<Map<String, Object>> getGeneralSetting(@PathVariable Long unotId) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true, unitGeneralSettingService.getGeneralSetting(unotId));
    }
}
