package com.kairos.activity.controller.period;

import com.kairos.activity.service.period.PlanningPeriodService;
import com.kairos.activity.util.response.ResponseHandler;
import com.kairos.response.dto.web.period.PlanningPeriodDTO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.inject.Inject;
import javax.validation.Valid;
import java.math.BigInteger;
import java.util.Map;

import static com.kairos.activity.constants.ApiConstants.API_ORGANIZATION_UNIT_URL;

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

        return ResponseHandler.generateResponse(HttpStatus.OK, true, planningPeriodService.createPeriod(unitId, planningPeriodDTO));


    }

    @ApiOperation(value = "Get Planning Period")
    @GetMapping(value="/period")
    //@PreAuthorize("@customPermissionEvaluator.isAuthorized()")
    public ResponseEntity<Map<String, Object>> getPlanningPeriod(@PathVariable Long unitId) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true, planningPeriodService.getPeriods(unitId, null, null));
    }


    @ApiOperation(value = "update period by unit Id and Period Id")
    @PutMapping(value = "/period/{periodId}")
    //@PreAuthorize("@customPermissionEvaluator.isAuthorized()")
    public ResponseEntity<Map<String, Object>> updatePhase(@PathVariable BigInteger periodId, @PathVariable Long unitId, @RequestBody @Valid PlanningPeriodDTO planningPeriodDTO) {
         return ResponseHandler.generateResponse(HttpStatus.OK, true, planningPeriodService.updatePeriod(unitId, periodId, planningPeriodDTO));

    }

    @ApiOperation(value = "Remove Period")
    @DeleteMapping(value = "/period/{periodId}")
    public ResponseEntity<Map<String, Object>> deletePhase(@PathVariable Long unitId, @PathVariable BigInteger periodId) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true, planningPeriodService.deletePeriod(unitId, periodId));
    }


    @ApiOperation(value = "update period's phase to next phase")
    @PutMapping(value = "/period/{periodId}/next_phase")
    //@PreAuthorize("@customPermissionEvaluator.isAuthorized()")
    public ResponseEntity<Map<String, Object>> updatePlanningPeriodPhaseToNext(@PathVariable BigInteger periodId, @PathVariable Long unitId) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true, planningPeriodService.setPlanningPeriodPhaseToNext(unitId, periodId));
    }

}
