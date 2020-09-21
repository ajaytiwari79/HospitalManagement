package com.kairos.controller.organization;

import com.kairos.dto.activity.shift.ShiftSearchDTO;
import com.kairos.dto.user_context.CurrentUserDetails;
import com.kairos.dto.user_context.UserContext;
import com.kairos.enums.shift.ShiftFilterDurationType;
import com.kairos.service.organization.ShiftPlanningService;
import com.kairos.utils.response.ResponseHandler;
import com.kairos.wrapper.shift.StaffShiftDetailsDTO;
import io.swagger.annotations.Api;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.inject.Inject;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static com.kairos.commons.utils.ObjectUtils.isNotNull;
import static com.kairos.constants.ApiConstants.API_UNIT_PLANNING_URL;

@RestController
@Api(API_UNIT_PLANNING_URL)
@RequestMapping(API_UNIT_PLANNING_URL)
public class ShiftPlanningController {

    @Inject
    private ShiftPlanningService shiftPlanningService;

    @PostMapping(value = "/search/shifts/staff/{staffId}")
    public StaffShiftDetailsDTO getShiftPlanningDetailsForOneStaff(@PathVariable Long unitId, @RequestBody ShiftSearchDTO searchDTO){
        CurrentUserDetails currentUserDetails = UserContext.getUserDetails();
        Long loggedInUserId = currentUserDetails.getId();
        searchDTO.setLoggedInUserId(loggedInUserId);
        return shiftPlanningService.getShiftPlanningDetailsForOneStaff(unitId,searchDTO,true);
    }

    @PostMapping(value = "/search/shifts")
    public List<StaffShiftDetailsDTO> shiftsAndPlanningSettingsForAllStaff(@PathVariable Long unitId, @RequestBody ShiftSearchDTO searchDTO) {
        CurrentUserDetails currentUserDetails = UserContext.getUserDetails();
        Long loggedInUserId = currentUserDetails.getId();
        searchDTO.setLoggedInUserId(loggedInUserId);
        if (searchDTO.getShiftFilterDurationType().equals(ShiftFilterDurationType.INDIVIDUAL)) {
            return shiftPlanningService.getUnitPlanningAndShiftForSelectedStaff(unitId, searchDTO,true);
        } else {
            return shiftPlanningService.getShiftPlanningDetailsForUnit(unitId, searchDTO,true);
        }
    }

    @PostMapping(value = "/search/shiftFilters/staff")
    public ResponseEntity<Map<String, Object>> getStaffListForShiftFilters(@PathVariable Long unitId, @RequestBody ShiftSearchDTO searchDTO,@RequestParam(required = false) Boolean showAllStaffs) {
        CurrentUserDetails currentUserDetails = UserContext.getUserDetails();
        Long loggedInUserId = currentUserDetails.getId();
        searchDTO.setLoggedInUserId(loggedInUserId);
        Map staffData = new HashMap();
        List<StaffShiftDetailsDTO> staffShiftDetailDTOS =shiftPlanningService.getFilteredStaffForMatchingFilter(unitId, searchDTO,isNotNull(showAllStaffs) ? showAllStaffs : true);
        staffData.put("staffList", staffShiftDetailDTOS);
        staffData.put("employmentTypes",shiftPlanningService.getEmploymentTypes(unitId));
        return ResponseHandler.generateResponse(HttpStatus.OK, true, staffData);
    }


    @PostMapping(value = "/search/staffById")
    public Set<Long> getStaffListForCriteria(@RequestParam Long unitId, @RequestBody Set<String> realtimeStatusList) {
        return shiftPlanningService.getStaffListAsId(unitId, realtimeStatusList);
    }
}