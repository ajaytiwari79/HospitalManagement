package com.kairos.controller.unit_settings;

import com.kairos.service.unit_settings.TimeAttendanceGracePeriodService;
import com.kairos.util.response.ResponseHandler;
import com.kairos.activity.unit_settings.TAndAGracePeriodSettingDTO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.inject.Inject;
import java.util.Map;

import static com.kairos.constants.ApiConstants.API_ORGANIZATION_UNIT_URL;

@RestController
@RequestMapping(API_ORGANIZATION_UNIT_URL)
@Api(API_ORGANIZATION_UNIT_URL)
public class TimeAttendanceGracePeriodController {

    @Inject
    private TimeAttendanceGracePeriodService tAndAGracePeriodService;


    @ApiOperation(value = "get unit T&A GracePeriod settings")
    @GetMapping(value = "/grace_period_setting")
    public ResponseEntity<Map<String, Object>> getGracePeriodSettings(@PathVariable Long unitId) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true,tAndAGracePeriodService.getTAndAGracePeriodSetting(unitId));
    }

    @ApiOperation(value = "save unit T&A GracePeriod settings")
    @PostMapping(value = "/grace_period_setting")
    public ResponseEntity<Map<String, Object>> updateGracePeriodSettings(@PathVariable Long unitId,
                                                                         @RequestBody TAndAGracePeriodSettingDTO tAndAGracePeriodSettingDTO) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true, tAndAGracePeriodService.updateTAndAGracePeriodSetting(unitId,tAndAGracePeriodSettingDTO));
    }
}
