package com.kairos.service.shift;

import com.kairos.dto.activity.shift.ShiftDTO;
import com.kairos.dto.gdpr.FilterSelectionDTO;
import com.kairos.dto.user.country.time_slot.TimeSlotDTO;
import com.kairos.dto.user.staff.StaffFilterDTO;
import com.kairos.enums.FilterType;
import com.kairos.persistence.model.shift.ShiftState;
import com.kairos.rest_client.UserIntegrationService;
import com.kairos.utils.user_context.UserContext;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import java.math.BigInteger;
import java.util.*;
import java.util.stream.Collectors;

import static com.kairos.commons.utils.ObjectUtils.isNull;
import static com.kairos.enums.FilterType.FUNCTIONS;

/**
 * Created by pradeep
 * Created at 27/6/19
 **/
@Service
public class ShiftFilterService {

    @Inject
    private UserIntegrationService userIntegrationService;

    @Inject
    private ShiftStateService shiftStateService;

    public <T extends ShiftDTO> List<T> getShiftsByFilters(List<T> shiftWithActivityDTOS, StaffFilterDTO staffFilterDTO) {
        List<BigInteger> shiftStateIds=new ArrayList<>();
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
//      ShiftFilterDefaultData shiftFilterDefaultData = userIntegrationService.getShiftFilterDefaultData(new SelfRosteringFilterDTO(UserContext.getUnitId(),teamIds));
        ShiftFilter timeTypeFilter = new TimeTypeFilter(filterTypeMap);
        ShiftFilter activityTimecalculationTypeFilter = new ActivityTimeCalculationTypeFilter(filterTypeMap);
        ShiftFilter activityStatusFilter = new ActivityStatusFilter(filterTypeMap);
        ShiftFilter timeSlotFilter = new TimeSlotFilter(filterTypeMap,timeSlotDTOS);
        ShiftFilter activityFilter = new ActivityFilter(filterTypeMap);
        ShiftFilter plannedTimeTypeFilter=new PlannedTimeTypeFilter(filterTypeMap);
        if(filterTypeMap.containsKey(FilterType.VALIDATED_BY)) {
            Set<BigInteger> shiftIds = shiftWithActivityDTOS.stream().map(shiftDTO -> shiftDTO.getId()).collect(Collectors.toSet());
           List<ShiftState> shiftStates = shiftStateService.findAllByShiftIdsByAccessgroupRole(shiftIds, filterTypeMap.get(FilterType.VALIDATED_BY));
           shiftStateIds=shiftStates.stream().map(shiftState -> shiftState.getShiftId()).collect(Collectors.toList());
        }
        ShiftFilter TimeAndAttendanceFilter=new TimeAndAttendanceFilter(filterTypeMap,shiftStateIds);
        List<Date> functionDates = new ArrayList<>();
        if(filterTypeMap.containsKey(FilterType.FUNCTIONS)) {
            List<Long> functionIds = filterTypeMap.get(FUNCTIONS).stream().map(s -> new Long(s)).collect(Collectors.toList());
            functionDates = userIntegrationService.getAllDateByFunctionIds(shiftWithActivityDTOS.get(0).getUnitId(), functionIds);
        }
        ShiftFilter functionsFilter = new FunctionsFilter(filterTypeMap, functionDates);
        ShiftFilter realTimeStatusFilter=new RealTimeStatusFilter(filterTypeMap);
        ShiftFilter shiftFilter = new AndShiftFilter(timeTypeFilter, activityTimecalculationTypeFilter).and(activityStatusFilter).and(timeSlotFilter).and(activityFilter).and(plannedTimeTypeFilter).and(TimeAndAttendanceFilter)
                                    .and(functionsFilter).and(realTimeStatusFilter);
        return shiftFilter.meetCriteria(shiftWithActivityDTOS);
    }
}
