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
        planningPeriodService.createPeriod(unitId, planningPeriodDTO);
        return ResponseHandler.generateResponse(HttpStatus.OK, true, null);
    }


}
