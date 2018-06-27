package com.kairos.activity.controller.attendence_setting;

import com.kairos.response.dto.web.attendance.AttendanceDuration;
import com.kairos.activity.service.attendence_setting.AttendanceSettingService;
import com.kairos.activity.util.response.ResponseHandler;
import io.swagger.annotations.Api;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.inject.Inject;

import java.util.Map;

import static com.kairos.activity.constants.ApiConstants.API_ORGANIZATION_UNIT_URL;

@RestController
@RequestMapping(API_ORGANIZATION_UNIT_URL)
@Api(value = API_ORGANIZATION_UNIT_URL )
public class AttendanceSettingController {

   @Inject
   private AttendanceSettingService attendanceSettingService;

   @GetMapping(value ="/attendance_setting")
   public ResponseEntity<Map<String, Object>> getAttendanceSettings( ) {
       return ResponseHandler.generateResponse(HttpStatus.OK, true,attendanceSettingService.getAttendanceSetting());
   }

   @PostMapping(value = "/attendance_setting")
    public ResponseEntity<Map<String,Object>> updateAttendanceSetting( @RequestBody AttendanceDuration attendanceDuration){
       return ResponseHandler.generateResponse(HttpStatus.OK, true,attendanceSettingService.updateAttendanceSetting(attendanceDuration));
   }

}


