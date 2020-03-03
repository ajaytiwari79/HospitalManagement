package com.kairos.controller.organization;

import com.kairos.service.organization.ShiftPlanningService;
import com.kairos.wrapper.shift.StaffShiftDetails;
import io.swagger.annotations.Api;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.inject.Inject;
import java.util.List;

import static com.kairos.constants.ApiConstants.API_UNIT_PLANNING_URL;

@RestController
@Api(API_UNIT_PLANNING_URL)
@RequestMapping(API_UNIT_PLANNING_URL)
public class ShiftPlanningController {

    @Inject
    private ShiftPlanningService shiftPlanningService;

    @GetMapping(value = "/staff_and_shift_details")
    public List<StaffShiftDetails> shiftsAndPlanningSettings(@PathVariable Long unitId){
       return shiftPlanningService.getShiftPlanningDetailsForUnit(unitId);
    }
}
