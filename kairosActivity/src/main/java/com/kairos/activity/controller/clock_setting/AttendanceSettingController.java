package com.kairos.activity.controller.clock_setting;

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
@RequestMapping(API_ORGANIZATION_UNIT_URL + "/staff")
@Api(value = API_ORGANIZATION_UNIT_URL + "/staff")
public class AttendanceSettingController {

   @Inject
   private AttendanceSettingService attendanceSettingService;

   @GetMapping(value ="/{staffId}/attendance_setting")
   public ResponseEntity<Map<String, Object>> getAttendanceSettings(@PathVariable Long unitId, @PathVariable Long staffId) {
       return ResponseHandler.generateResponse(HttpStatus.OK, true,attendanceSettingService.getAttendanceSetting(unitId,staffId));
   }

   @PostMapping(value = "/{staffId}/attendance_setting")
    public ResponseEntity<Map<String,Object>> updateAttendanceSetting(@PathVariable Long unitId, @PathVariable Long staffId, @RequestBody Duration clockInclockOutDuration){
       return ResponseHandler.generateResponse(HttpStatus.OK, true,attendanceSettingService.updateAttendanceSetting(unitId,staffId,clockInclockOutDuration));
   }

}


