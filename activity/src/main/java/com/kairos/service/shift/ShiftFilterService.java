package com.kairos.service.shift;

import com.kairos.dto.activity.shift.SelfRosteringFilterDTO;
import com.kairos.dto.activity.shift.ShiftDTO;
import com.kairos.dto.activity.shift.ShiftFilterDefaultData;
import com.kairos.dto.gdpr.FilterSelectionDTO;
import com.kairos.dto.user.country.time_slot.TimeSlotDTO;
import com.kairos.dto.user.staff.StaffFilterDTO;
import com.kairos.enums.FilterType;
import com.kairos.rest_client.UserIntegrationService;
import com.kairos.utils.user_context.UserContext;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import java.util.*;
import java.util.stream.Collectors;

import static com.kairos.commons.utils.ObjectUtils.isCollectionNotEmpty;
import static com.kairos.commons.utils.ObjectUtils.isNull;
import static com.kairos.enums.FilterType.TEAM;

/**
 * Created by pradeep
 * Created at 27/6/19
 **/
@Service
public class ShiftFilterService {

    @Inject
    private UserIntegrationService userIntegrationService;

    public <T extends ShiftDTO> List<T> getShiftsByFilters(List<T> shiftWithActivityDTOS, StaffFilterDTO staffFilterDTO) {
        if (isNull(staffFilterDTO)) {
            staffFilterDTO = new StaffFilterDTO();
            staffFilterDTO.setFiltersData(new ArrayList<>());
        }
//        Set<String> teamIds=new HashSet<>();
        List<TimeSlotDTO> timeSlotDTOS = userIntegrationService.getUnitTimeSlot(UserContext.getUnitId());
        Map<FilterType, Set<String>> filterTypeMap = staffFilterDTO.getFiltersData().stream().collect(Collectors.toMap(FilterSelectionDTO::getName, v -> v.getValue()));
//        if(filterTypeMap.containsKey(TEAM) && isCollectionNotEmpty(filterTypeMap.get(TEAM))){
//            teamIds=filterTypeMap.get(TEAM);
//        }
  ///      ShiftFilterDefaultData shiftFilterDefaultData = userIntegrationService.getShiftFilterDefaultData(new SelfRosteringFilterDTO(UserContext.getUnitId(),teamIds));
        ShiftFilter timeTypeFilter = new TimeTypeFilter(filterTypeMap);
        ShiftFilter activityTimecalculationTypeFilter = new ActivityTimeCalculationTypeFilter(filterTypeMap);
        ShiftFilter activityStatusFilter = new ActivityStatusFilter(filterTypeMap);
        ShiftFilter timeSlotFilter = new TimeSlotFilter(filterTypeMap,timeSlotDTOS);
        ShiftFilter activityFilter = new ActivityFilter(filterTypeMap);
        ShiftFilter plannedTimeTypeFilter=new PlannedTimeTypeFilter(filterTypeMap);
        ShiftFilter TimeAndAttendanceFilter=new TimeAndAttendanceFilter((filterTypeMap));
        ShiftFilter shiftFilter = new AndShiftFilter(timeTypeFilter, activityTimecalculationTypeFilter).and(activityStatusFilter).and(timeSlotFilter).and(activityFilter).and(plannedTimeTypeFilter).and(TimeAndAttendanceFilter);
        return shiftFilter.meetCriteria(shiftWithActivityDTOS);
    }
}
