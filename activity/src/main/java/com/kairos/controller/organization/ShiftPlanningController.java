package com.kairos.controller.organization;

import com.kairos.dto.activity.shift.ShiftSearchDTO;
import com.kairos.service.organization.ShiftPlanningService;
import com.kairos.wrapper.shift.StaffShiftDetails;
import io.swagger.annotations.Api;
import org.springframework.web.bind.annotation.*;

import javax.inject.Inject;
import java.util.List;

import static com.kairos.constants.ApiConstants.API_UNIT_PLANNING_URL;

@RestController
@Api(API_UNIT_PLANNING_URL)
@RequestMapping(API_UNIT_PLANNING_URL)
public class ShiftPlanningController {

    @Inject
    private ShiftPlanningService shiftPlanningService;

    @PostMapping(value = "/search/shifts")
    public List<StaffShiftDetails> shiftsAndPlanningSettings(@PathVariable Long unitId, @RequestBody ShiftSearchDTO searchDTO){
       return shiftPlanningService.getShiftPlanningDetailsForUnit(unitId,searchDTO);
    }
}
