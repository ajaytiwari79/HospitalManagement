package com.kairos.controller.shift;

import com.kairos.commons.utils.DateUtils;
import com.kairos.dto.activity.shift.*;
import com.kairos.dto.activity.staffing_level.Duration;
import com.kairos.enums.shift.ViewType;
import com.kairos.service.activity.ActivityService;
import com.kairos.service.shift.*;
import com.kairos.utils.response.ResponseHandler;
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
import java.time.LocalDate;
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
    private ShiftService shiftService;
    @Inject
    private ShiftSickService shiftSickService;
    @Inject
    private ShiftTemplateService shiftTemplateService;
    @Inject
    private ActivityService activityService;
    @Inject
    private ShiftCopyService shiftCopyService;
    @Inject
    private ShiftDetailsService shiftDetailsService;
    @Inject
    private ShiftStateService shiftStateService;

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
                                                                 @RequestParam(value = "startDate", required = false)
                                                                     @DateTimeFormat(pattern="yyyy-MM-dd") LocalDate startDate,
                                                                 @RequestParam(value = "endDate", required = false)
                                                                     @DateTimeFormat(pattern="yyyy-MM-dd") LocalDate endDate) throws ParseException {

        return ResponseHandler.generateResponse(HttpStatus.OK, true, shiftService.getShiftByStaffId(unitId, staffId, startDate, endDate, week, unitPositionId, type));
    }


    @ApiOperation("save Shift after validation")
    @PostMapping(value = "/shift/validated")
    //  @PreAuthorize("@customPermissionEvaluator.isAuthorized()")
    public ResponseEntity<Map<String, Object>> saveShiftAfterValidation(@PathVariable Long organizationId, @PathVariable Long unitId, @RequestParam("type") String type, @RequestBody @Valid ShiftWithViolatedInfoDTO shiftWithViolatedInfo) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true, shiftService.saveShiftAfterValidation(shiftWithViolatedInfo, type));
    }

    @ApiOperation("update a Shift of a staff")
    @PutMapping(value = "/shift")
    //  @PreAuthorize("@customPermissionEvaluator.isAuthorized()")
    public ResponseEntity<Map<String, Object>> updateShift(@PathVariable Long organizationId, @PathVariable Long unitId, @RequestParam("type") String type, @RequestBody @Valid ShiftDTO shiftDTO) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true, shiftService.updateShift( shiftDTO, type,false));
    }

    @ApiOperation("delete a Shift of a staff")
    @DeleteMapping(value = "/shift/{shiftId}")
    //  @PreAuthorize("@customPermissionEvaluator.isAuthorized()")
    public ResponseEntity<Map<String, Object>> deleteShift(@PathVariable BigInteger shiftId) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true, shiftService.deleteShift(shiftId));
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

    /*// sub shifts workflow
    @ApiOperation("add a sub shift for a staff")
    @PutMapping(value = "/sub-shift")
    //  @PreAuthorize("@customPermissionEvaluator.isAuthorized()")
    public ResponseEntity<Map<String, Object>> addSubShift(@RequestParam("type") String type, @PathVariable long unitId, @RequestBody @Valid ShiftDTO shiftDTO) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true, shiftService.addSubShift(unitId, shiftDTO, type));
    }*/

    /*@ApiOperation("update a sub shifts for a staff")
    @PutMapping(value = "/sub-shifts")
    //  @PreAuthorize("@customPermissionEvaluator.isAuthorized()")
    public ResponseEntity<Map<String, Object>> addSubShifts(@RequestParam("type") String type, @PathVariable long unitId, @RequestBody List<ShiftDTO> shiftDTOS) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true, shiftService.addSubShifts(unitId, shiftDTOS, type));
    }*/

    @ApiOperation("update status of shifts")
    @PutMapping(value = "/shift/update_status")
    //  @PreAuthorize("@customPermissionEvaluator.isAuthorized()")
    public ResponseEntity<Map<String, Object>> updateStatusOfShifts(@PathVariable Long unitId,@RequestBody @Valid ShiftPublishDTO shiftPublishDTO) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true, shiftService.updateStatusOfShifts(unitId,shiftPublishDTO));
    }


    @ApiOperation("get all open and assigned shifts of all staff for a particular date.")
    @GetMapping(value = "/shifts")
    //  @PreAuthorize("@customPermissionEvaluator.isAuthorized()")
    public ResponseEntity<Map<String, Object>> getAllShiftsOfSelectedDate(@PathVariable long unitId, @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") Date startDate,@RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") Date endDate,@RequestParam(required=false)ViewType viewType) throws ParseException {
        return ResponseHandler.generateResponse(HttpStatus.OK, true, shiftService.getAllShiftsOfSelectedDate(unitId, startDate,endDate,viewType));
    }

    @ApiOperation("copy shifts from 1 employee to others")
    @PutMapping(value = "/copy_shifts")
    public ResponseEntity<Map<String, Object>> copyShifts(@PathVariable long unitId,@RequestBody @Valid CopyShiftDTO copyShiftDTO) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true, shiftCopyService.copyShifts(unitId, copyShiftDTO));
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
        return ResponseHandler.generateResponse(HttpStatus.OK, true, shiftTemplateService.createShiftUsingTemplate(unitId,shiftDTO));
    }
    @ApiOperation("delete shifts and update openshifts")
    @PutMapping(value = "/staff/{staffId}/shifts_and_openshifts")
    public ResponseEntity<Map<String, Object>> deleteShiftsAndOpenShiftsOnEmploymentEnd(@RequestParam(value = "employmentEndDate")
                                                                                                    Long employmentEndDate,@PathVariable Long staffId,
                                                                                        @PathVariable Long unitId) {

        shiftService.deleteShiftsAndOpenShiftsOnEmploymentEnd(staffId, DateUtils.getLocalDatetimeFromLong(employmentEndDate),unitId);
        return ResponseHandler.generateResponse(HttpStatus.OK, true,null );

    }
    @ApiOperation("delete all shifts of staff after employment end")
    @DeleteMapping(value = "/delete_shifts/staff/{staffId}")
    //  @PreAuthorize("@customPermissionEvaluator.isAuthorized()")
    public ResponseEntity<Map<String, Object>> deleteShiftsAfterEmploymentEndDate(@PathVariable Long unitId,@PathVariable Long staffId,@RequestParam("endDate")String endDate) {
        shiftService.deleteShiftsAfterEmploymentEndDate(staffId,unitId,DateUtils.asLocalDate(endDate));
        return ResponseHandler.generateResponse(HttpStatus.OK, true, true);
    }
    @ApiOperation("API is used to add shift of user when user is sick")
    @PostMapping("/staff/{staffId}/shift_on_sick")
    public ResponseEntity<Map<String,Object>> markUserAsSick(@PathVariable Long unitId, @PathVariable Long staffId, @RequestParam ("activitySelected") BigInteger activityId,
                                                             @RequestBody Duration duration){
        return ResponseHandler.generateResponse(HttpStatus.OK,true,shiftSickService.createSicknessShiftsOfStaff(unitId,activityId,staffId,duration));
    }

    @ApiOperation("shifts details by date")
    @GetMapping("/shift/details_by_date")
    public ResponseEntity<Map<String,Object>> getShiftsDetailsByDate(@PathVariable Long unitId,@RequestParam Long staffId,@RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") Date shiftStartDate){
        return ResponseHandler.generateResponse(HttpStatus.OK,true,shiftService.getDetailViewInfo(unitId,staffId,shiftStartDate));
    }

    @ApiOperation("update shift by detail view")
    @PutMapping("/shift/update_shift_by_details_view")
    public ResponseEntity<Map<String,Object>> updateShiftByDetailsView(@PathVariable Long unitId,@RequestParam String type,@RequestBody ShiftDTO shiftDTO,@RequestParam Boolean validatedByStaff){
        return ResponseHandler.generateResponse(HttpStatus.OK,true,shiftService.updateShiftByTandA(unitId,shiftDTO,type,validatedByStaff));
    }


    @ApiOperation("validate shift by detail view")
    @PostMapping("/shift/validate_shift_by_details_view")
    public ResponseEntity<Map<String,Object>> validateShiftByDetailsView(@PathVariable Long unitId,@RequestParam String type,@RequestBody ShiftDTO shiftDTO,@RequestParam Boolean validatedByStaff){
        return ResponseHandler.generateResponse(HttpStatus.OK,true,shiftService.validateShift(shiftDTO,validatedByStaff,unitId,type));
    }

    @ApiOperation("shifts details by date")
    @GetMapping("/shift/compact_view_details_by_date")
    public ResponseEntity<Map<String,Object>> getShiftsDetailsForCompactViewByDate(@PathVariable Long unitId,@RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") Date shiftStartDate){
        return ResponseHandler.generateResponse(HttpStatus.OK,true,shiftService.getCompactViewDetails(unitId,shiftStartDate));
    }

    @ApiOperation("get a Shift detail by id")
    @PostMapping(value = "/shift/details")
    //  @PreAuthorize("@customPermissionEvaluator.isAuthorized()")
    public ResponseEntity<Map<String, Object>> shiftDetailsById(@PathVariable Long unitId,@RequestBody List<BigInteger> shiftIds) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true, shiftDetailsService.shiftDetailsById(unitId,shiftIds));
    }



    @ApiOperation("update remarks in shift activity")
    @PutMapping(value = "/shift/shiftActivity/{shiftActivityId}")
    //  @PreAuthorize("@customPermissionEvaluator.isAuthorized()")
    public ResponseEntity<Map<String, Object>> updateRemarkInShiftActivity(@PathVariable BigInteger shiftActivityId,@RequestBody ShiftActivityDTO shiftActivityDTO) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true, shiftDetailsService.updateRemarkInShiftActivity(shiftActivityId,shiftActivityDTO));
    }

    @ApiOperation("create shift state")
    @GetMapping("/shift/create_state")
    public ResponseEntity<Map<String,Object>> createShiftState(@PathVariable Long unitId,@RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") Date startDate,@RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") Date endDate){
        return ResponseHandler.generateResponse(HttpStatus.OK,true,shiftStateService.createShiftState(unitId,startDate,endDate));
    }

}