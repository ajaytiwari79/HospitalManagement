package com.kairos.activity.controller.attendence_setting;

import com.kairos.activity.persistence.model.staffing_level.Duration;
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
@RequestMapping(API_ORGANIZATION_UNIT_URL + "/userId")
@Api(value = API_ORGANIZATION_UNIT_URL + "/userId")
public class AttendanceSettingController {

   @Inject
   private AttendanceSettingService attendanceSettingService;

   @GetMapping(value ="/{userId}/attendance_setting")
   public ResponseEntity<Map<String, Object>> getAttendanceSettings(@PathVariable Long unitId, @PathVariable Long userId) {
       return ResponseHandler.generateResponse(HttpStatus.OK, true,attendanceSettingService.getAttendanceSetting(unitId,userId));
   }

   @PostMapping(value = "/{userId}/attendance_setting")
    public ResponseEntity<Map<String,Object>> updateAttendanceSetting(@PathVariable Long unitId, @PathVariable Long userId, @RequestBody Duration attendanceDuration){
       return ResponseHandler.generateResponse(HttpStatus.OK, true,attendanceSettingService.updateAttendanceSetting(unitId,userId,attendanceDuration));
   }

}


