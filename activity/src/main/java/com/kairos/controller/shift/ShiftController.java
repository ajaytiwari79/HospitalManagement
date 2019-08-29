package com.kairos.controller.shift;

import com.kairos.commons.utils.DateUtils;
import com.kairos.dto.activity.shift.*;
import com.kairos.dto.activity.staffing_level.Duration;
import com.kairos.dto.user.staff.StaffFilterDTO;
import com.kairos.enums.shift.ShiftActionType;
import com.kairos.enums.shift.ShiftFilterParam;
import com.kairos.enums.shift.ViewType;
import com.kairos.enums.todo.TodoType;
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
import java.time.LocalDate;
import java.util.Date;
import java.util.List;
import java.util.Map;

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
    @Inject private RequestAbsenceService requestAbsenceService;

    @ApiOperation("Create Shift of a staff")
    @PostMapping(value = "/shift")
    //  @PreAuthorize("@customPermissionEvaluator.isAuthorized()")
    public ResponseEntity<Map<String, Object>> createShift( @PathVariable Long unitId, @RequestBody @Valid ShiftDTO shiftDTO , @RequestParam(required = false ,value = "shiftActionType") ShiftActionType shiftActionType) {

        return ResponseHandler.generateResponse(HttpStatus.OK, true, shiftService.createShift(unitId, shiftDTO ,shiftActionType));
    }


    @ApiOperation("save Shift after validation")
    @PostMapping(value = "/shift/validated")
    //  @PreAuthorize("@customPermissionEvaluator.isAuthorized()")
    public ResponseEntity<Map<String, Object>> saveShiftAfterValidation(@PathVariable Long unitId,
                                                                        @RequestBody @Valid ShiftWithViolatedInfoDTO shiftWithViolatedInfo,
                                                                        @RequestParam(value = "validatedByStaff", required = false) Boolean validatedByStaff,
                                                                        @RequestParam(value = "updateShiftState", required = false) boolean updateShiftState,
                                                                        @RequestParam(required = false, value = "shiftActionType") ShiftActionType shiftActionType,
                                                                        @RequestParam(required = false) TodoType todoType) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true, shiftService.saveShiftAfterValidation(shiftWithViolatedInfo, validatedByStaff, updateShiftState, unitId, shiftActionType,todoType));
    }

    @ApiOperation("update a Shift of a staff")
    @PutMapping(value = "/shift")
    //  @PreAuthorize("@customPermissionEvaluator.isAuthorized()")
    public ResponseEntity<Map<String, Object>> updateShift(@PathVariable Long unitId, @RequestBody @Valid ShiftDTO shiftDTO, @RequestParam(required = false, value = "shiftActionType") ShiftActionType shiftActionType) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true, shiftService.updateShift(shiftDTO, false, false, shiftActionType));
    }

    @ApiOperation("delete a Shift of a staff")
    @DeleteMapping(value = "/shift/{shiftId}")
    //  @PreAuthorize("@customPermissionEvaluator.isAuthorized()")
    public ResponseEntity<Map<String, Object>> deleteShift(@PathVariable BigInteger shiftId) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true, shiftService.deleteAllLinkedShifts(shiftId));
    }

    @ApiOperation("update status of shifts")
    @PutMapping(value = "/shift/update_status")
    //  @PreAuthorize("@customPermissionEvaluator.isAuthorized()")
    public ResponseEntity<Map<String, Object>> updateStatusOfShifts(@PathVariable Long unitId, @RequestBody @Valid ShiftPublishDTO shiftPublishDTO) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true, shiftStatusService.updateStatusOfShifts(unitId, shiftPublishDTO));
    }

    @ApiOperation("copy shifts from 1 employee to others")
    @PutMapping(value = "/copy_shifts")
    public ResponseEntity<Map<String, Object>> copyShifts(@PathVariable long unitId, @RequestBody @Valid CopyShiftDTO copyShiftDTO) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true, shiftCopyService.copyShifts(unitId, copyShiftDTO));
    }

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

        shiftService.deleteShiftsAndOpenShiftsOnEmploymentEnd(staffId, DateUtils.getLocalDatetimeFromLong(employmentEndDate), unitId);
        return ResponseHandler.generateResponse(HttpStatus.OK, true, null);

    }

    @ApiOperation("delete all shifts of staff after employment end")
    @DeleteMapping(value = "/delete_shifts/staff/{staffId}")
    //  @PreAuthorize("@customPermissionEvaluator.isAuthorized()")
    public ResponseEntity<Map<String, Object>> deleteShiftsAfterEmploymentEndDate(@PathVariable Long unitId, @PathVariable Long staffId, @RequestParam("endDate") String endDate) {
        shiftService.deleteShiftsAfterEmploymentEndDate(staffId, unitId, DateUtils.asLocalDate(endDate));
        return ResponseHandler.generateResponse(HttpStatus.OK, true, true);
    }

    @ApiOperation("API is used to add shift of user when user is sick")
    @PostMapping("/staff/{staffId}/shift_on_sick")
    public ResponseEntity<Map<String, Object>> markUserAsSick(@PathVariable Long unitId, @PathVariable Long staffId, @RequestParam("activitySelected") BigInteger activityId,
                                                              @RequestBody Duration duration) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true, shiftSickService.createSicknessShiftsOfStaff(unitId, activityId, staffId, duration));
    }


    @ApiOperation("update shift by detail view")
    @PutMapping("/shift/update_shift_by_details_view")
    public ResponseEntity<Map<String, Object>> updateShiftByDetailsView(@PathVariable Long unitId, @RequestBody ShiftDTO shiftDTO, @RequestParam Boolean updatedByStaff) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true, shiftService.updateShiftByTandA(unitId, shiftDTO, updatedByStaff));
    }


    @ApiOperation("validate shift by detail view")
    @PostMapping("/shift/validate_shift_by_details_view")
    public ResponseEntity<Map<String, Object>> validateShiftByDetailsView(@PathVariable Long unitId,  @RequestBody @Valid ShiftDTO shiftDTO, @RequestParam Boolean validatedByStaff) {
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

    @ApiOperation("create shift state")
    @PostMapping("/shift/create_state")
    public ResponseEntity<Map<String, Object>> createShiftState(@PathVariable Long unitId, @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") Date startDate, @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") Date endDate) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true, shiftStateService.createShiftState(unitId, startDate, endDate));
    }

    @ApiOperation("Get shifts by staff/unit/expertise/date ")
    @PostMapping(value = "/shifts")
    //  @PreAuthorize("@customPermissionEvaluator.isAuthorized()")
    public ResponseEntity<Map<String, Object>> getAllShiftAndStates(@PathVariable Long unitId,
                                                                    @RequestParam(value = "employmentId", required = false) Long employmentId,
                                                                    @RequestParam(value = "startDate")
                                                                    @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate startDate, @RequestParam(value = "endDate", required = false)
                                                                    @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate endDate, @RequestParam(value = "viewType", required = false) ViewType viewType,
                                                                    @RequestParam(value = "staffId", required = false) Long staffId,
                                                                    @RequestParam(value = "expertiseId", required = false) Long expertiseId,
                                                                    @RequestParam(value = "shiftFilterParam") ShiftFilterParam shiftFilterParam,@RequestBody(required = false) StaffFilterDTO staffFilterDTO) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true, shiftService.getAllShiftAndStates(unitId, staffId, startDate, endDate, employmentId, viewType, shiftFilterParam, expertiseId,staffFilterDTO));
    }

    @ApiOperation("Update planning period id in Shifts")
    @PutMapping(value = "/update_planning_period_in_shifts")
    //  @PreAuthorize("@customPermissionEvaluator.isAuthorized()")
    public ResponseEntity<Map<String, Object>> updatePlanningPeriodInShifts() {
        return ResponseHandler.generateResponse(HttpStatus.OK, true, shiftService.updatePlanningPeriodInShifts());
    }


    //TODO We need to remove this API After closing sprint 44
    @ApiOperation("delete Duplicate Entry Of ShiftViolatedInfo")
    @DeleteMapping(value = "/delete_duplicate_entry_of_shift_violated_info")
    //  @PreAuthorize("@customPermissionEvaluator.isAuthorized()")
    public ResponseEntity<Map<String, Object>> deleteDuplicateEntryOfShiftViolatedInfo() {
        return ResponseHandler.generateResponse(HttpStatus.OK, true, shiftValidatorService.deleteDuplicateEntryOfShiftViolatedInfo());
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
                                                              @RequestParam(value = "shiftActionType") ShiftActionType shiftActionType,@RequestBody StaffFilterDTO staffFilterDTO) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true, shiftService.saveAndCancelDraftShift(unitId, staffId, startDate, endDate, employmentId, viewType, shiftFilterParam, shiftActionType,staffFilterDTO));
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
    public ResponseEntity<Map<String, Object>> getShiftCount(@PathVariable Long employmentId){
        return ResponseHandler.generateResponse(HttpStatus.OK, true, shiftService.getShiftCount(employmentId));
    }
}