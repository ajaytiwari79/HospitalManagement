package com.kairos.controller.activity;

import com.kairos.activity.shift.CopyShiftDTO;
import com.kairos.activity.shift.ShiftDTO;
import com.kairos.activity.shift.ShiftPublishDTO;
import com.kairos.service.activity.ActivityService;
import com.kairos.service.shift.ShiftService;
import com.kairos.util.response.ResponseHandler;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.inject.Inject;
import javax.validation.Valid;
import java.math.BigInteger;
import java.text.ParseException;
import java.util.Date;
import java.util.List;
import java.util.Map;

import static com.kairos.constants.ApiConstants.API_ORGANIZATION_UNIT_URL;

/**
 * Created by vipul on 30/8/17.
 */
@RestController
@RequestMapping(API_ORGANIZATION_UNIT_URL)
@Api(API_ORGANIZATION_UNIT_URL)
public class ShiftController {


    @Inject
    ShiftService shiftService;
    @Inject
    private ActivityService activityService;

    @ApiOperation("Create Shift of a staff")
    @PostMapping(value = "/shift")
    //  @PreAuthorize("@customPermissionEvaluator.isAuthorized()")
    public ResponseEntity<Map<String, Object>> createShift(@RequestParam("type") String type, @PathVariable Long organizationId, @PathVariable Long unitId, @RequestBody @Valid ShiftDTO shiftDTO) {

        return ResponseHandler.generateResponse(HttpStatus.OK, true, shiftService.createShift(unitId, shiftDTO, type, false));
    }

    @ApiOperation("Get Shift of a staff")
    @GetMapping(value = "/shift/staff/{staffId}")
    //  @PreAuthorize("@customPermissionEvaluator.isAuthorized()")
    public ResponseEntity<Map<String, Object>> getShiftByStaffId(@PathVariable Long staffId, @PathVariable Long unitId,
                                                                 @RequestParam(value = "unitPositionId", required = false) Long unitPositionId,
                                                                 @RequestParam("type") String type,
                                                                 @RequestParam(value = "week", required = false) Long week,
                                                                 @RequestParam(value = "startDate", required = false) String startDate,
                                                                 @RequestParam(value = "endDate", required = false) String endDate) throws ParseException {

        return ResponseHandler.generateResponse(HttpStatus.OK, true, shiftService.getShiftByStaffId(unitId, staffId, startDate, endDate, week, unitPositionId, type));
    }


    @ApiOperation("update a Shift of a staff")
    @PutMapping(value = "/shift")
    //  @PreAuthorize("@customPermissionEvaluator.isAuthorized()")
    public ResponseEntity<Map<String, Object>> updateShift(@PathVariable Long organizationId, @PathVariable Long unitId, @RequestParam("type") String type, @RequestBody @Valid ShiftDTO shiftDTO) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true, shiftService.updateShift(unitId, shiftDTO, type));
    }

    @ApiOperation("delete a Shift of a staff")
    @DeleteMapping(value = "/shift/{shiftId}")
    //  @PreAuthorize("@customPermissionEvaluator.isAuthorized()")
    public ResponseEntity<Map<String, Object>> deleteShift(@PathVariable BigInteger shiftId) {
        shiftService.deleteShift(shiftId);
        return ResponseHandler.generateResponse(HttpStatus.OK, true, true);
    }

    @ApiOperation(value = "Get All Activities by unitId")
    @RequestMapping(value = "/activity", method = RequestMethod.GET)
    // @PreAuthorize("@customPermissionEvaluator.isAuthorized()")
    public ResponseEntity<Map<String, Object>> getActivityByUnitId(@RequestParam("type") String type, @PathVariable long unitId) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true,
                activityService.getActivityByUnitId(unitId, type));
    }

    @ApiOperation(value = "Get All Activities and Phases by unitId")
    @RequestMapping(value = "/activityWithPhase", method = RequestMethod.GET)
    // @PreAuthorize("@customPermissionEvaluator.isAuthorized()")
    public ResponseEntity<Map<String, Object>> getActivityAndPhaseByUnitId(@RequestParam("type") String type, @PathVariable long unitId) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true, activityService.getActivityAndPhaseByUnitId(unitId, type));
    }

    // sub shifts workflow
    @ApiOperation("add a sub shift for a staff")
    @PutMapping(value = "/sub-shift")
    //  @PreAuthorize("@customPermissionEvaluator.isAuthorized()")
    public ResponseEntity<Map<String, Object>> addSubShift(@RequestParam("type") String type, @PathVariable long unitId, @RequestBody @Valid ShiftDTO shiftDTO) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true, shiftService.addSubShift(unitId, shiftDTO, type));
    }

    @ApiOperation("update a sub shifts for a staff")
    @PutMapping(value = "/sub-shifts")
    //  @PreAuthorize("@customPermissionEvaluator.isAuthorized()")
    public ResponseEntity<Map<String, Object>> addSubShifts(@RequestParam("type") String type, @PathVariable long unitId, @RequestBody List<ShiftDTO> shiftDTOS) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true, shiftService.addSubShifts(unitId, shiftDTOS, type));
    }

    @ApiOperation("publish Shifts")
    @PutMapping(value = "/publish_shifts")
    //  @PreAuthorize("@customPermissionEvaluator.isAuthorized()")
    public ResponseEntity<Map<String, Object>> publishShifts(@PathVariable Long unitId,@RequestBody @Valid ShiftPublishDTO shiftPublishDTO) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true, shiftService.addStatusesInShift(unitId,shiftPublishDTO));
    }


    @ApiOperation("get all open and assigned shifts of all staff for a particular date.")
    @GetMapping(value = "/shifts")
    //  @PreAuthorize("@customPermissionEvaluator.isAuthorized()")
    public ResponseEntity<Map<String, Object>> getAllShiftsOfSelectedDate(@PathVariable long unitId, @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") Date startDate,@RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") Date endDate) throws ParseException {
        return ResponseHandler.generateResponse(HttpStatus.OK, true, shiftService.getAllShiftsOfSelectedDate(unitId, startDate,endDate));
    }

    @ApiOperation("copy shifts from 1 employee to others")
    @PutMapping(value = "/copy_shifts")
    //
    public ResponseEntity<Map<String, Object>> copyShifts(@PathVariable long unitId,@RequestBody @Valid CopyShiftDTO copyShiftDTO) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true, shiftService.copyShifts(unitId, copyShiftDTO));
    }

    @ApiOperation("Get Shift of a staff based upon expertise")
    @GetMapping(value = "/shifts/staff/{staffId}")
    //  @PreAuthorize("@customPermissionEvaluator.isAuthorized()")
    public ResponseEntity<Map<String, Object>> getShiftOfStaffByExpertiseId(@PathVariable Long staffId, @PathVariable Long unitId,
                                                                            @RequestParam Long expertiseId,
                                                                            @RequestParam(value = "startDate", required = false) String startDate,
                                                                            @RequestParam(value = "endDate", required = false) String endDate) throws ParseException {
        return ResponseHandler.generateResponse(HttpStatus.OK, true, shiftService.getShiftOfStaffByExpertiseId(unitId, staffId, startDate, endDate, expertiseId));
    }

    @ApiOperation("create shift using template")
    @PostMapping(value = "shift/from_shift_template")
    //  @PreAuthorize("@customPermissionEvaluator.isAuthorized()")
    public ResponseEntity<Map<String, Object>> createShiftUsingTemplate(@PathVariable Long unitId, @RequestBody ShiftDTO shiftDTO){
        return ResponseHandler.generateResponse(HttpStatus.OK, true, shiftService.createShiftUsingTemplate(unitId,shiftDTO));
    }

//    @ApiOperation("Add statuses in  Shifts")
//    @PutMapping(value = "/add_status")
//    //  @PreAuthorize("@customPermissionEvaluator.isAuthorized()")
//    public ResponseEntity<Map<String, Object>> addStatusesInShift(@PathVariable Long unitId, @RequestBody @Valid ShiftAndPhaseStatusDTO shiftAndPhaseStatusDTO) {
//        return ResponseHandler.generateResponse(HttpStatus.OK, true, shiftService.addStatusesInShift(unitId,shiftAndPhaseStatusDTO));
//    }






}