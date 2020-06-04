package com.kairos.controller.organization;

import com.kairos.dto.activity.shift.ShiftSearchDTO;
import com.kairos.dto.user_context.CurrentUserDetails;
import com.kairos.dto.user_context.UserContext;
import com.kairos.enums.shift.ShiftFilterDurationType;
import com.kairos.service.organization.ShiftPlanningService;
import com.kairos.wrapper.shift.StaffShiftDetails;
import io.swagger.annotations.Api;
import org.springframework.web.bind.annotation.*;

import javax.inject.Inject;
import java.util.List;
import java.util.Set;

import static com.kairos.constants.ApiConstants.API_UNIT_PLANNING_URL;

@RestController
@Api(API_UNIT_PLANNING_URL)
@RequestMapping(API_UNIT_PLANNING_URL)
public class ShiftPlanningController {

    @Inject
    private ShiftPlanningService shiftPlanningService;

    @PostMapping(value = "/search/shifts/staff/{staffId}")
    public StaffShiftDetails getShiftPlanningDetailsForOneStaff(@PathVariable Long unitId, @RequestBody ShiftSearchDTO searchDTO){
        CurrentUserDetails currentUserDetails = UserContext.getUserDetails();
        Long loggedInUserId = currentUserDetails.getId();
        searchDTO.setLoggedInUserId(loggedInUserId);
        return shiftPlanningService.getShiftPlanningDetailsForOneStaff(unitId,searchDTO);
    }

    @PostMapping(value = "/search/shifts")
    public List<StaffShiftDetails> shiftsAndPlanningSettingsForAllStaff(@PathVariable Long unitId, @RequestBody ShiftSearchDTO searchDTO) {
        CurrentUserDetails currentUserDetails = UserContext.getUserDetails();
        Long loggedInUserId = currentUserDetails.getId();
        searchDTO.setLoggedInUserId(loggedInUserId);
        if (searchDTO.getShiftFilterDurationType().equals(ShiftFilterDurationType.INDIVIDUAL)) {
            return shiftPlanningService.getUnitPlanningAndShiftForSelectedStaff(unitId, searchDTO);
        } else {
            return shiftPlanningService.getShiftPlanningDetailsForUnit(unitId, searchDTO);
        }
    }

    @PostMapping(value = "/search/shiftFilters/staff")
    public List<StaffShiftDetails> getStaffListForShiftFilters(@PathVariable Long unitId, @RequestBody ShiftSearchDTO searchDTO) {
        CurrentUserDetails currentUserDetails = UserContext.getUserDetails();
        Long loggedInUserId = currentUserDetails.getId();
        searchDTO.setLoggedInUserId(loggedInUserId);
        return shiftPlanningService.getFilteredStaffForMatchingFilter(unitId, searchDTO);
    }


    @PostMapping(value = "/search/staffById")
    public Set<Long> getStaffListForCriteria(@RequestParam Long unitId, @RequestBody Set<String> realtimeStatusList) {
        return shiftPlanningService.getStaffListAsId(unitId, realtimeStatusList);
    }
}