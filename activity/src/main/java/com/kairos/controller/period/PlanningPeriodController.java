package com.kairos.controller.period;

import com.kairos.service.period.PlanningPeriodService;
import com.kairos.util.response.ResponseHandler;
import com.kairos.activity.period.PlanningPeriodDTO;
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
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

import static com.kairos.constants.ApiConstants.API_ORGANIZATION_UNIT_URL;

/**
 * Created by prerna on 6/4/18.
 */
@RestController()
@Api(API_ORGANIZATION_UNIT_URL)
@RequestMapping(API_ORGANIZATION_UNIT_URL)
public class PlanningPeriodController {

    @Inject
    PlanningPeriodService planningPeriodService;

    @ApiOperation(value = "Create Planning Period")
    @PostMapping(value="/period")
    //@PreAuthorize("@customPermissionEvaluator.isAuthorized()")
    public ResponseEntity<Map<String, Object>> createPlanningPeriod(@PathVariable Long unitId,  @RequestBody @Valid PlanningPeriodDTO planningPeriodDTO) {

        return ResponseHandler.generateResponse(HttpStatus.OK, true, planningPeriodService.addPlanningPeriods(unitId, planningPeriodDTO));


    }

    @ApiOperation(value = "Get Planning Period")
    @GetMapping(value="/period")
    //@PreAuthorize("@customPermissionEvaluator.isAuthorized()")
    public ResponseEntity<Map<String, Object>> getPlanningPeriod(@PathVariable Long unitId) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true, planningPeriodService.getPlanningPeriods(unitId, null, null));
    }

    @ApiOperation(value = "Get Planning Period with phase for self roastering")
    @GetMapping(value="/period_of_interval")
    public ResponseEntity<Map<String, Object>> getPlanningPeriodOfInterval(@PathVariable Long unitId, @RequestParam(required = true, value = "startDate") @DateTimeFormat(pattern="yyyy-MM-dd") LocalDate startDate, @RequestParam(required = true, value = "endDate") @DateTimeFormat(pattern="yyyy-MM-dd") LocalDate endDate) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true, planningPeriodService.getPeriodOfInterval(unitId, startDate,endDate));
    }

    @ApiOperation(value = "update period by unit Id and Period Id")
    @PutMapping(value = "/period/{periodId}")
    //@PreAuthorize("@customPermissionEvaluator.isAuthorized()")
    public ResponseEntity<Map<String, Object>> updatePhase(@PathVariable BigInteger periodId, @PathVariable Long unitId, @RequestBody @Valid PlanningPeriodDTO planningPeriodDTO) {
         return ResponseHandler.generateResponse(HttpStatus.OK, true, planningPeriodService.updatePlanningPeriod(unitId, periodId, planningPeriodDTO));

    }

    @ApiOperation(value = "Remove Period")
    @DeleteMapping(value = "/period/{periodId}")
    public ResponseEntity<Map<String, Object>> deletePhase(@PathVariable Long unitId, @PathVariable BigInteger periodId) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true, planningPeriodService.deletePlanningPeriod(unitId, periodId));
    }


    @ApiOperation(value = "update period's phase to next phase")
    @PutMapping(value = "/period/{periodId}/next_phase")
    //@PreAuthorize("@customPermissionEvaluator.isAuthorized()")
    public ResponseEntity<Map<String, Object>> updatePlanningPeriodPhaseToNext(@PathVariable BigInteger periodId, @PathVariable Long unitId) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true, planningPeriodService.setPlanningPeriodPhaseToNext(unitId, periodId));
    }

    @ApiOperation(value = "update period's flipping Date")
    @PutMapping(value = "/period/{periodId}/flip_phase/{date}")
    //@PreAuthorize("@customPermissionEvaluator.isAuthorized()")
    public ResponseEntity<Map<String, Object>> updateFlippingDates(@PathVariable BigInteger periodId, @PathVariable Long unitId, @PathVariable @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm") LocalDateTime date) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true, planningPeriodService.updateFlippingDate(periodId, unitId, date));

    }
    @ApiOperation(value = "restore shift base planning period and phase id")
    @PutMapping(value = "/restore_shift")
    //@PreAuthorize("@customPermissionEvaluator.isAuthorized()")
    public ResponseEntity<Map<String, Object>> restoreShiftsData(@RequestParam("planningPeriodId") BigInteger planningPeriodId, @PathVariable Long unitId,@RequestParam("phaseId") BigInteger phaseId) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true, planningPeriodService.setShiftsDataToInitialData(planningPeriodId,phaseId,unitId));

    }

    @ApiOperation(value = "restore shift data based on shiftIds and phase")
    @PutMapping(value = "/restore_shift_ids")
    //@PreAuthorize("@customPermissionEvaluator.isAuthorized()")
    public ResponseEntity<Map<String, Object>> restoreShiftsDataByShiftIds(@RequestBody List<BigInteger> shiftIds, @PathVariable Long unitId, @RequestParam("phaseId") BigInteger phaseId) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true, planningPeriodService.setShiftsDataToInitialDataOfShiftIds(shiftIds,phaseId,unitId));

    }
    @ApiOperation(value = "Migrate Planning Period")
    @PostMapping(value="/migrate_planning_period")
    //@PreAuthorize("@customPermissionEvaluator.isAuthorized()")
    public ResponseEntity<Map<String, Object>> migratePlanningPeriod(@PathVariable Long unitId,  @RequestBody @Valid PlanningPeriodDTO planningPeriodDTO) {

        return ResponseHandler.generateResponse(HttpStatus.OK, true, planningPeriodService.migratePlanningPeriods(unitId, planningPeriodDTO));
    }

}
