package com.kairos.controller.organization;

import com.kairos.service.organization.ShiftPlanningService;
import com.kairos.wrapper.shift.ShiftsAndPlanningSettingsDTO;
import io.swagger.annotations.Api;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.inject.Inject;

import static com.kairos.constants.ApiConstants.API_UNIT_PLANNING_URL;

@RestController
@Api(API_UNIT_PLANNING_URL)
@RequestMapping(API_UNIT_PLANNING_URL)
public class ShiftPlanningController {

    @Inject
    private ShiftPlanningService shiftPlanningService;

    public ShiftsAndPlanningSettingsDTO shiftsAndPlanningSettings(long unitId){
       return shiftPlanningService.getShiftPlanningDetailsForUnit(unitId);
    }
}
