package com.kairos.controller.attendence_setting;

import com.kairos.service.attendence_setting.AttendanceSettingService;
import com.kairos.util.response.ResponseHandler;
import io.swagger.annotations.Api;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.inject.Inject;

import java.util.Map;


import static com.kairos.constants.ApiConstants.API_ORGANIZATION_UNIT_URL;
import static com.kairos.constants.ApiConstants.API_ORGANIZATION_URL;

@RestController
@RequestMapping(API_ORGANIZATION_URL)
@Api(value = API_ORGANIZATION_URL )
public class AttendanceSettingController {

    @Inject
    private AttendanceSettingService attendanceSettingService;

    @GetMapping(value ="/attendance_setting")
    public ResponseEntity<Map<String, Object>> getAttendanceSettings( ) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true,attendanceSettingService.getAttendanceSetting());
    }

    @PostMapping(value = "/attendance_setting")
    public ResponseEntity<Map<String,Object>> updateAttendanceSetting(@RequestParam(value = "unitId",required=false) Long unitId,@RequestParam(value = "reasonCodeId",required=false) Long reasonCodeId,@RequestParam("checkIn") boolean checkIn){
        return ResponseHandler.generateResponse(HttpStatus.OK, true,attendanceSettingService.updateAttendanceSetting(unitId,reasonCodeId,checkIn));
    }


}


