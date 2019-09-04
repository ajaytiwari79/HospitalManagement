package com.kairos.controller.attendence_setting;

import com.kairos.service.attendence_setting.TimeAndAttendanceService;
import com.kairos.utils.response.ResponseHandler;
import io.swagger.annotations.Api;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.inject.Inject;
import java.util.Map;

import static com.kairos.constants.ApiConstants.API_V1;
import static com.kairos.constants.ApiConstants.UNIT_URL;

@RestController
@RequestMapping(API_V1)
@Api(value = API_V1)
public class TimeAndAttendanceController {

    @Inject
    private TimeAndAttendanceService timeAndAttendanceService;

    @GetMapping(value ="/attendance_setting")
    public ResponseEntity<Map<String, Object>> getTimeAndAttendance( ) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true, timeAndAttendanceService.getAttendanceSetting());
    }

    @PostMapping(value = "/attendance_setting")
    public ResponseEntity<Map<String,Object>> updateTimeAndAttendance(@RequestParam(value = "unitId",required=false) Long unitId, @RequestParam(value = "reasonCodeId",required=false) Long reasonCodeId,@RequestParam(value = "employmentId",required=false) Long employmentId, @RequestParam("checkIn") boolean checkIn){
        return ResponseHandler.generateResponse(HttpStatus.OK, true, timeAndAttendanceService.updateTimeAndAttendance(unitId,reasonCodeId,employmentId,checkIn));
    }
}


