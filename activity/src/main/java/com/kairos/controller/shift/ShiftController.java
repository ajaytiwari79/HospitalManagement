package com.kairos.controller.shift;

import com.kairos.commons.utils.DateUtils;
import com.kairos.dto.activity.shift.*;
import com.kairos.dto.user.access_permission.AccessGroupRole;
import com.kairos.dto.user.staff.StaffFilterDTO;
import com.kairos.dto.user.user.staff.StaffAdditionalInfoDTO;
import com.kairos.dto.user_context.UserContext;
import com.kairos.enums.BreakAction;
import com.kairos.enums.shift.ShiftActionType;
import com.kairos.enums.shift.ShiftFilterParam;
import com.kairos.enums.shift.ShiftStatus;
import com.kairos.enums.shift.ViewType;
import com.kairos.enums.todo.TodoType;
import com.kairos.service.activity.ActivityService;
import com.kairos.service.open_shift.OpenShiftService;
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
import java.time.LocalDate;
import java.util.Date;
import java.util.List;
import java.util.Map;

import static com.kairos.commons.utils.ObjectUtils.isNull;
import static com.kairos.constants.ApiConstants.API_UNIT_URL;


/**
 * Created by vipul on 30/8/17.
 */
@RestController
@RequestMapping(API_UNIT_URL)
@Api(API_UNIT_URL)
public class ShiftController {
    @Inject
    private ShiftService shiftService;
    @Inject
    private ShiftFetchService shiftFetchService;
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
    @Inject
    private ShiftValidatorService shiftValidatorService;
    @Inject
    private ShiftStatusService shiftStatusService;
    @Inject
    private RequestAbsenceService requestAbsenceService;
    @Inject
    private ShiftBreakService shiftBreakService;
    @Inject
    private OpenShiftService openShiftService;

    @ApiOperation("Create Shift of a staff")
    @PostMapping(value = "/shift")
    //  @PreAuthorize("@customPermissionEvaluator.isAuthorized()")
    public ResponseEntity<Map<String, Object>> createShift(@PathVariable Long unitId, @RequestBody @Valid ShiftDTO shiftDTO, @RequestParam(required = false, value = "shiftActionType") ShiftActionType shiftActionType) {
        if (UserContext.getUserDetails().isStaff()) {
            shiftActionType = ShiftActionType.SAVE;
        }
        return ResponseHandler.generateResponse(HttpStatus.OK, true, shiftService.createShift(unitId, shiftDTO, shiftActionType));
    }

    @ApiOperation("Create Shifts of a staff")
    @PostMapping(value = "/create_shifts")
    //  @PreAuthorize("@customPermissionEvaluator.isAuthorized()")
    public ResponseEntity<Map<String, Object>> createShifts(@PathVariable Long unitId, @RequestBody @Valid List<ShiftDTO> shiftDTOS, @RequestParam(required = false, value = "shiftActionType") ShiftActionType shiftActionType) {
        if (UserContext.getUserDetails().getUnitWiseAccessRole().containsKey(unitId.toString()) && AccessGroupRole.STAFF.toString().equals(UserContext.getUserDetails().getUnitWiseAccessRole().get(unitId.toString()))) {
            shiftActionType = ShiftActionType.SAVE;
        }
        return ResponseHandler.generateResponse(HttpStatus.OK, true, shiftService.createShifts(unitId, shiftDTOS, shiftActionType));
    }


    @ApiOperation("save or delete Shift after validation")
    @PostMapping(value = "/shift/validated")
    //  @PreAuthorize("@customPermissionEvaluator.isAuthorized()")
    public ResponseEntity<Map<String, Object>> saveOrDeleteShiftAfterValidation(@PathVariable Long unitId,
                                                                                @RequestBody @Valid ShiftWithViolatedInfoDTO shiftWithViolatedInfo,
                                                                                @RequestParam(value = "validatedByStaff", required = false) Boolean validatedByStaff, @RequestParam(value = "updateShiftState", required = false) boolean updateShiftState,
                                                                                @RequestParam(required = false, value = "shiftActionType") ShiftActionType shiftActionType,
                                                                                @RequestParam(required = false) TodoType todoType) {
        ShiftWithViolatedInfoDTO shiftWithViolatedInfoDTO = ShiftActionType.DELETE.equals(shiftActionType) ? shiftService.deleteShiftAfterValidation(shiftWithViolatedInfo) : shiftService.saveShiftAfterValidation(shiftWithViolatedInfo, validatedByStaff, updateShiftState, unitId, shiftActionType, todoType);
        return ResponseHandler.generateResponse(HttpStatus.OK, true, shiftWithViolatedInfoDTO);
    }

    @ApiOperation("save or delete Shifts after validation")
    @PostMapping(value = "/shifts/validated")
    //  @PreAuthorize("@customPermissionEvaluator.isAuthorized()")
    public ResponseEntity<Map<String, Object>> saveOrDeleteShiftsAfterValidation(@PathVariable Long unitId,
                                                                                 @RequestBody @Valid List<ShiftWithViolatedInfoDTO> shiftWithViolatedInfoDTOS,
                                                                                 @RequestParam(value = "validatedByStaff", required = false) Boolean validatedByStaff,
                                                                                 @RequestParam(value = "updateShiftState", required = false) boolean updateShiftState,
                                                                                 @RequestParam(required = false, value = "shiftActionType") ShiftActionType shiftActionType,
                                                                                 @RequestParam(required = false) TodoType todoType) {
        List<ShiftWithViolatedInfoDTO> shiftWithViolatedInfoDTOS1 = ShiftActionType.DELETE.equals(shiftActionType) ? shiftService.deleteShiftsAfterValidation(shiftWithViolatedInfoDTOS) : shiftService.saveShiftsAfterValidation(shiftWithViolatedInfoDTOS, validatedByStaff, updateShiftState, unitId, shiftActionType, todoType);
        return ResponseHandler.generateResponse(HttpStatus.OK, true, shiftWithViolatedInfoDTOS1);
    }

    @ApiOperation("update a Shifts of a staff")
    @PutMapping(value = "/shifts")
    //  @PreAuthorize("@customPermissionEvaluator.isAuthorized()")
    public ResponseEntity<Map<String, Object>> updateShifts(@PathVariable Long unitId, @RequestBody @Valid List<ShiftDTO> shiftDTOS, @RequestParam(required = false, value = "shiftActionType") ShiftActionType shiftActionType) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true, shiftService.updateShifts(shiftDTOS, false, false, shiftActionType));
    }

    @ApiOperation("update a Shift of a staff")
    @PutMapping(value = "/shift")
    //  @PreAuthorize("@customPermissionEvaluator.isAuthorized()")
    public ResponseEntity<Map<String, Object>> updateShift(@PathVariable Long unitId, @RequestBody @Valid ShiftDTO shiftDTO, @RequestParam(required = false, value = "shiftActionType") ShiftActionType shiftActionType) {
        shiftDTO.setUnitId(unitId);
        return ResponseHandler.generateResponse(HttpStatus.OK, true, shiftService.updateShift(shiftDTO, false, false, shiftActionType));
    }

    @ApiOperation("delete a Shift of a staff")
    @DeleteMapping(value = "/shift/{shiftId}")
    //  @PreAuthorize("@customPermissionEvaluator.isAuthorized()")
    public ResponseEntity<Map<String, Object>> deleteShift(@PathVariable BigInteger shiftId) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true, shiftService.deleteAllLinkedShifts(shiftId));
    }

    //This is delete api but mapping with post because in delete mapping frontend cannot send list data in payload.
    @ApiOperation("delete Shifts")
    @PostMapping(value = "/delete/shifts")
    //  @PreAuthorize("@customPermissionEvaluator.isAuthorized()")
    public ResponseEntity<Map<String, Object>> deleteAllShifts(@RequestBody List<BigInteger> shiftIds) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true, shiftService.deleteAllShifts(shiftIds));
    }

    @ApiOperation("update status of shifts")
    @PutMapping(value = "/shift/update_status")
    //  @PreAuthorize("@customPermissionEvaluator.isAuthorized()")
    public ResponseEntity<Map<String, Object>> updateStatusOfShifts(@PathVariable Long unitId, @RequestBody @Valid ShiftPublishDTO shiftPublishDTO) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true, shiftStatusService.updateStatusOfShifts(unitId, shiftPublishDTO));
    }

   /* @ApiOperation("copy shifts from 1 employee to others")
    @PutMapping(value = "/copy_shifts")
    public ResponseEntity<Map<String, Object>> copyShifts(@PathVariable long unitId, @RequestBody @Valid CopyShiftDTO copyShiftDTO) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true, shiftCopyService.copyShifts(unitId, copyShiftDTO));
    }*/

    @ApiOperation("create shift using template")
    @PostMapping(value = "shift/from_shift_template")
    //  @PreAuthorize("@customPermissionEvaluator.isAuthorized()")
    public ResponseEntity<Map<String, Object>> createShiftUsingTemplate(@PathVariable Long unitId, @RequestBody ShiftDTO shiftDTO) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true, shiftTemplateService.createShiftUsingTemplate(unitId, shiftDTO));
    }

    @ApiOperation("delete shifts and update openshifts")
    @PutMapping(value = "/staff/{staffId}/shifts_and_openshifts")
    public ResponseEntity<Map<String, Object>> deleteShiftsAndOpenShiftsOnEmploymentEnd(@RequestParam(value = "employmentEndDate")
                                                                                                Long employmentEndDate, @PathVariable Long staffId,
                                                                                        @PathVariable Long unitId) {

        openShiftService.deleteShiftsAndOpenShiftsOnEmploymentEnd(staffId, DateUtils.getLocalDatetimeFromLong(employmentEndDate));
        return ResponseHandler.generateResponse(HttpStatus.OK, true, null);

    }

    @ApiOperation("delete all shifts of staff after employment end")
    @PutMapping(value = "/delete_shifts/employment/{employmentId}")
    //  @PreAuthorize("@customPermissionEvaluator.isAuthorized()")
    public ResponseEntity<Map<String, Object>> deleteShiftsAfterEmploymentEndDate(@PathVariable Long employmentId, @RequestParam("endDate") String endDate, @RequestBody StaffAdditionalInfoDTO staffAdditionalInfoDTO) {
        shiftService.deleteShiftsAfterEmploymentEndDate(employmentId, DateUtils.asLocalDate(endDate), staffAdditionalInfoDTO);
        return ResponseHandler.generateResponse(HttpStatus.OK, true, true);
    }

  /*  @ApiOperation("API is used to add shift of user when user is sick")
    @PostMapping("/staff/{staffId}/shift_on_sick")
    public ResponseEntity<Map<String, Object>> markUserAsSick(@PathVariable Long unitId, @PathVariable Long staffId, @RequestParam("activitySelected") BigInteger activityId,
                                                              @RequestBody Duration duration) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true, shiftSickService.createSicknessShiftsOfStaff(unitId, activityId, staffId, duration));
    }
*/

    @ApiOperation("update shift by detail view")
    @PutMapping("/shift/update_shift_by_details_view")
    public ResponseEntity<Map<String, Object>> updateShiftByDetailsView(@PathVariable Long unitId, @RequestBody ShiftDTO shiftDTO) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true, shiftService.updateShiftByTandA(unitId, shiftDTO));
    }


    @ApiOperation("validate shift by detail view")
    @PostMapping("/shift/validate_shift_by_details_view")
    public ResponseEntity<Map<String, Object>> validateShiftByDetailsView(@PathVariable Long unitId, @RequestBody @Valid ShiftDTO shiftDTO, @RequestParam Boolean validatedByStaff) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true, shiftValidatorService.validateShift(shiftDTO, validatedByStaff, unitId));
    }

    @ApiOperation("get a Shift detail by id")
    @PostMapping(value = "/shift/details")
    //  @PreAuthorize("@customPermissionEvaluator.isAuthorized()")
    public ResponseEntity<Map<String, Object>> shiftDetailsById(@PathVariable Long unitId, @RequestBody List<BigInteger> shiftIds, @RequestParam(required = false, value = "showDraft") boolean showDraft) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true, shiftDetailsService.shiftDetailsById(unitId, shiftIds, showDraft));

    }


    @ApiOperation("update remarks in shift activity")
    @PutMapping(value = "/shift/shiftActivity/{shiftActivityId}")
    //  @PreAuthorize("@customPermissionEvaluator.isAuthorized()")
    public ResponseEntity<Map<String, Object>> updateRemarkInShiftActivity(@PathVariable BigInteger shiftActivityId, @RequestBody ShiftActivityDTO shiftActivityDTO) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true, shiftDetailsService.updateRemarkInShiftActivity(shiftActivityId, shiftActivityDTO));
    }

    @ApiOperation("send shift in time and attendance phase")
    @PostMapping("/shift/send_shift_in_time_and_attendance_phase")
    public ResponseEntity<Map<String, Object>> sendShiftInTimeAndAttendancePhase(@PathVariable Long unitId, @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") Date startDate, @RequestParam(value = "endDate", required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") Date endDate, @RequestParam(value = "staffId", required = false) Long staffId) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true, shiftStateService.sendShiftInTimeAndAttendancePhase(unitId, startDate, staffId));
    }

    @ApiOperation("Get shifts by staff/unit/expertise/date ")
    @PostMapping(value = "/shifts")
    //  @PreAuthorize("@customPermissionEvaluator.isAuthorized()")
    public ResponseEntity<Map<String, Object>> getAllShiftAndStates(@PathVariable Long unitId,
                                                                    @RequestParam(value = "employmentId", required = false) Long employmentId,
                                                                    @RequestParam(value = "startDate")
                                                                    @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate startDate,
                                                                    @RequestParam(value = "endDate", required = false)
                                                                    @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate endDate,
                                                                    @RequestParam(value = "viewType", required = false) ViewType viewType,
                                                                    @RequestParam(value = "staffId", required = false) Long staffId,
                                                                    @RequestParam(value = "expertiseId", required = false) Long expertiseId,
                                                                    @RequestParam(value = "shiftFilterParam") ShiftFilterParam shiftFilterParam, @RequestBody(required = false) StaffFilterDTO staffFilterDTO) {

        //TODO temporary initializing it but it must be mendatory from FE
        if (isNull(staffFilterDTO)) {
            staffFilterDTO = new StaffFilterDTO();
        }
        return ResponseHandler.generateResponse(HttpStatus.OK, true, shiftFetchService.getAllShiftAndStates(unitId, staffId, startDate, endDate, employmentId, viewType, shiftFilterParam, expertiseId, staffFilterDTO));
    }

    @ApiOperation("Update planning period id in Shifts")
    @PutMapping(value = "/update_planning_period_in_shifts")
    //  @PreAuthorize("@customPermissionEvaluator.isAuthorized()")
    public ResponseEntity<Map<String, Object>> updatePlanningPeriodInShifts() {
        return ResponseHandler.generateResponse(HttpStatus.OK, true, shiftService.updatePlanningPeriodInShifts());
    }

    @ApiOperation("save draft shift after publish")
    @PutMapping(value = "/draft_shift")
    //  @PreAuthorize("@customPermissionEvaluator.isAuthorized()")
    public ResponseEntity<Map<String, Object>> saveDraftShift(@PathVariable Long unitId,
                                                              @RequestParam(value = "employmentId", required = false) Long employmentId,
                                                              @RequestParam(value = "startDate", required = false)
                                                              @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate startDate, @RequestParam(value = "endDate", required = false)
                                                              @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate endDate, @RequestParam(value = "viewType", required = false) ViewType viewType,
                                                              @RequestParam(value = "staffId", required = false) Long staffId,
                                                              @RequestParam(value = "shiftFilterParam") ShiftFilterParam shiftFilterParam,
                                                              @RequestParam(value = "shiftActionType") ShiftActionType shiftActionType, @RequestBody StaffFilterDTO staffFilterDTO) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true, shiftService.saveAndCancelDraftShift(unitId, staffId, startDate, endDate, employmentId, viewType, shiftFilterParam, shiftActionType, staffFilterDTO));
    }

    @ApiOperation("create request absence")
    @PostMapping("/request_absence")
    //  @PreAuthorize("@customPermissionEvaluator.isAuthorized()")
    public ResponseEntity<Map<String, Object>> createRequestAbsence(@RequestBody @Valid RequestAbsenceDTO requestAbsenceDTO) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true, requestAbsenceService.createOrUpdateRequestAbsence(requestAbsenceDTO));
    }

    @ApiOperation("delete request absence")
    @DeleteMapping("/request_absence")
    //  @PreAuthorize("@customPermissionEvaluator.isAuthorized()")
    public ResponseEntity<Map<String, Object>> createRequestAbsence(@RequestParam BigInteger shiftId) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true, requestAbsenceService.deleteRequestAbsence(shiftId));
    }

    @ApiOperation("get shift count")
    @GetMapping("employment/{employmentId}/shift_count")
    public ResponseEntity<Map<String, Object>> getPublishShiftCount(@PathVariable Long employmentId) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true, shiftService.getPublishShiftCount(employmentId));
    }

    @ApiOperation("update a break interrupt")
    @PutMapping(value = "/shift/break_interrupt/{shiftId}")
    //  @PreAuthorize("@customPermissionEvaluator.isAuthorized()")
    public ResponseEntity<Map<String, Object>> breakInterrupt(@PathVariable BigInteger shiftId, @RequestParam("breakAction") BreakAction breakAction) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true, shiftBreakService.interruptBreak(shiftId, breakAction));
    }

    @ApiOperation("update status of absence request")
    @PutMapping(value = "/absence_request")
    //  @PreAuthorize("@customPermissionEvaluator.isAuthorized()")
    public ResponseEntity<Map<String, Object>> updateShiftStatus(@PathVariable Long unitId, @RequestParam("shift_status") ShiftStatus shiftStatus, @RequestBody ShiftActivitiesIdDTO shiftActivitiesIdDTO) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true, shiftStatusService.updateShiftStatus(unitId, shiftStatus, shiftActivitiesIdDTO));
    }
}
