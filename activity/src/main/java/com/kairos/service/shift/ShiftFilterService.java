package com.kairos.service.shift;

import com.kairos.dto.activity.shift.SelfRosteringFilterDTO;
import com.kairos.dto.activity.shift.ShiftDTO;
import com.kairos.dto.activity.shift.ShiftFilterDefaultData;
import com.kairos.dto.gdpr.FilterSelectionDTO;
import com.kairos.dto.user.country.time_slot.TimeSlotDTO;
import com.kairos.dto.user.staff.StaffFilterDTO;
import com.kairos.enums.FilterType;
import com.kairos.persistence.model.activity.TimeType;
import com.kairos.persistence.model.shift.ShiftState;
import com.kairos.rest_client.UserIntegrationService;
import com.kairos.service.activity.TimeTypeService;
import com.kairos.utils.user_context.UserContext;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import java.math.BigInteger;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

import static com.kairos.commons.utils.ObjectUtils.isCollectionNotEmpty;
import static com.kairos.commons.utils.ObjectUtils.isNull;
import static com.kairos.enums.FilterType.*;

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
    @Inject
    private TimeTypeService timeTypeService;
    public <T extends ShiftDTO> List<T> getShiftsByFilters(List<T> shiftWithActivityDTOS, StaffFilterDTO staffFilterDTO) {
        List<BigInteger> shiftStateIds=new ArrayList<>();
        Long unitId = UserContext.getUserDetails().getLastSelectedOrganizationId();
        if (isNull(staffFilterDTO)) {
            staffFilterDTO = new StaffFilterDTO();
            staffFilterDTO.setFiltersData(new ArrayList<>());
        }
        List<TimeSlotDTO> timeSlotDTOS = userIntegrationService.getUnitTimeSlot(unitId);
        Map<FilterType, Set<String>> filterTypeMap = staffFilterDTO.getFiltersData().stream().collect(Collectors.toMap(FilterSelectionDTO::getName, v -> v.getValue()));
        List<BigInteger> timeTypeIds = new ArrayList<>();
        if(filterTypeMap.containsKey(TIME_TYPE) && isCollectionNotEmpty(filterTypeMap.get(TIME_TYPE))) {
            List<BigInteger> ids = getBigInteger(filterTypeMap.get(TIME_TYPE));
            timeTypeIds = timeTypeService.getAllTimeTypeWithItsLowerLevel(UserContext.getUserDetails().getCountryId(), ids);
        }
        ShiftFilter timeTypeFilter = new TimeTypeFilter(filterTypeMap, timeTypeIds);
        ShiftFilter activityTimecalculationTypeFilter = new ActivityTimeCalculationTypeFilter(filterTypeMap);
        ShiftFilter activityStatusFilter = new ActivityStatusFilter(filterTypeMap);
        ShiftFilter timeSlotFilter = new TimeSlotFilter(filterTypeMap,timeSlotDTOS);
        List<BigInteger> selectedActivityIds = new ArrayList<>();
        if(filterTypeMap.containsKey(ABSENCE_ACTIVITY) && isCollectionNotEmpty(filterTypeMap.get(ABSENCE_ACTIVITY))) {
            selectedActivityIds.addAll(filterTypeMap.get(ABSENCE_ACTIVITY).stream().map(s -> new BigInteger(s)).collect(Collectors.toList()));
        }
        if(filterTypeMap.containsKey(TEAM) && isCollectionNotEmpty(filterTypeMap.get(TEAM))){
            Set<String> teamIds = filterTypeMap.get(TEAM);
            ShiftFilterDefaultData shiftFilterDefaultData = userIntegrationService.getShiftFilterDefaultData(new SelfRosteringFilterDTO(unitId,teamIds));
            selectedActivityIds.addAll(shiftFilterDefaultData.getTeamActivityIds());
        }
        ShiftFilter activityFilter = new ActivityFilter(filterTypeMap, selectedActivityIds);
        ShiftFilter plannedTimeTypeFilter=new PlannedTimeTypeFilter(filterTypeMap);
        if(filterTypeMap.containsKey(FilterType.VALIDATED_BY) && isCollectionNotEmpty(filterTypeMap.get(VALIDATED_BY))) {
            Set<BigInteger> shiftIds = shiftWithActivityDTOS.stream().map(shiftDTO -> shiftDTO.getId()).collect(Collectors.toSet());
            List<ShiftState> shiftStates = shiftStateService.findAllByShiftIdsByAccessgroupRole(shiftIds, filterTypeMap.get(FilterType.VALIDATED_BY));
            shiftStateIds=shiftStates.stream().map(shiftState -> shiftState.getShiftId()).collect(Collectors.toList());
        }
        ShiftFilter TimeAndAttendanceFilter=new TimeAndAttendanceFilter(filterTypeMap,shiftStateIds);
        Set<LocalDate> functionDates = new HashSet<>();
        if(filterTypeMap.containsKey(FilterType.FUNCTIONS) && isCollectionNotEmpty(filterTypeMap.get(FUNCTIONS))) {
            List<Long> functionIds = filterTypeMap.get(FUNCTIONS).stream().map(s -> new Long(s)).collect(Collectors.toList());
            functionDates = userIntegrationService.getAllDateByFunctionIds(unitId, functionIds);
        }
        ShiftFilter functionsFilter = new FunctionsFilter(filterTypeMap, functionDates);
        Set<BigInteger> sickTimeTypes = new HashSet<>();
        if(filterTypeMap.containsKey(REAL_TIME_STATUS) && isCollectionNotEmpty(filterTypeMap.get(REAL_TIME_STATUS))) {
            sickTimeTypes = userIntegrationService.getSickSettingsOfUnit(unitId);
        }
        ShiftFilter realTimeStatusFilter=new RealTimeStatusFilter(filterTypeMap, sickTimeTypes);
        ShiftFilter shiftFilter = new AndShiftFilter(timeTypeFilter, activityTimecalculationTypeFilter).and(activityStatusFilter).and(timeSlotFilter).and(activityFilter).and(plannedTimeTypeFilter).and(TimeAndAttendanceFilter)
                                    .and(functionsFilter).and(realTimeStatusFilter);
        return shiftFilter.meetCriteria(shiftWithActivityDTOS);
    }

    private <T> List<BigInteger> getBigInteger(Collection<T> objects) {
        List<BigInteger> ids = new ArrayList<>();
        for (T object : objects) {
            String id = (object instanceof String) ? (String) object : ""+object;
            ids.add(new BigInteger(id));
        }
        return ids;
    }


}
