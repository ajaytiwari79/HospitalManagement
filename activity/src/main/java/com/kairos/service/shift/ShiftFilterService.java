package com.kairos.service.shift;

import com.kairos.dto.activity.shift.SelfRosteringFilterDTO;
import com.kairos.dto.activity.shift.ShiftDTO;
import com.kairos.dto.activity.shift.ShiftFilterDefaultData;
import com.kairos.dto.gdpr.FilterSelectionDTO;
import com.kairos.dto.user.access_permission.AccessGroupRole;
import com.kairos.dto.user.country.time_slot.TimeSlotDTO;
import com.kairos.dto.user.staff.StaffFilterDTO;
import com.kairos.dto.user_context.UserContext;
import com.kairos.enums.FilterType;
import com.kairos.persistence.model.shift.ShiftState;
import com.kairos.persistence.model.shift.ShiftViolatedRules;
import com.kairos.persistence.model.staff.personal_details.StaffPersonalDetail;
import com.kairos.rest_client.UserIntegrationService;
import com.kairos.service.activity.TimeTypeService;
import com.kairos.service.night_worker.NightWorkerService;
import com.kairos.utils.counter.KPIUtils;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import java.math.BigInteger;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

import static com.kairos.commons.utils.ObjectUtils.*;
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
    @Inject
    private NightWorkerService nightWorkerService;
    @Inject
    private ShiftValidatorService shiftValidatorService;

    public <T extends ShiftDTO> List<T> getShiftsByFilters(List<T> shiftWithActivityDTOS, StaffFilterDTO staffFilterDTO) {
        List<BigInteger> shiftStateIds=new ArrayList<>();
        Long unitId = UserContext.getUserDetails().getLastSelectedOrganizationId();
        if (isNull(staffFilterDTO)) {
            staffFilterDTO = new StaffFilterDTO();
            staffFilterDTO.setFiltersData(new ArrayList<>());
        }

        List<TimeSlotDTO> timeSlotDTOS = userIntegrationService.getUnitTimeSlot(unitId);
        Map<FilterType, Set<String>> filterTypeMap = staffFilterDTO.getFiltersData().stream().collect(Collectors.toMap(FilterSelectionDTO::getName, v -> v.getValue()));
        ShiftFilter timeTypeFilter = getTimeTypeFilter(filterTypeMap);
        ShiftFilter activityTimecalculationTypeFilter = new ActivityTimeCalculationTypeFilter(filterTypeMap);
        ShiftFilter activityStatusFilter = new ActivityStatusFilter(filterTypeMap);
        ShiftFilter timeSlotFilter = new TimeSlotFilter(filterTypeMap,timeSlotDTOS);
        ShiftFilter activityFilter = getActivityFilter(unitId, filterTypeMap);
        ShiftFilter plannedTimeTypeFilter=new PlannedTimeTypeFilter(filterTypeMap);
        ShiftFilter TimeAndAttendanceFilter = getValidatedFilter(shiftWithActivityDTOS, shiftStateIds, filterTypeMap);
        ShiftFilter functionsFilter = getFunctionFilter(unitId, filterTypeMap);
        ShiftFilter realTimeStatusFilter = getSickTimeTypeFilter(unitId, filterTypeMap);
        ShiftFilter plannedByFilter = getPlannedByFilter(unitId,filterTypeMap);
        ShiftFilter phaseFilter = new PhaseFilter(filterTypeMap);
        ShiftFilter groupFilter = getGroupFilter(unitId, filterTypeMap);
        ShiftFilter escalationFilter = getEscalationFilter(shiftWithActivityDTOS.stream().map(shift->shift.getId()).collect(Collectors.toList()), filterTypeMap);
        ShiftFilter shiftFilter = new AndShiftFilter(timeTypeFilter, activityTimecalculationTypeFilter).and(activityStatusFilter).and(timeSlotFilter).and(activityFilter).and(plannedTimeTypeFilter).and(TimeAndAttendanceFilter)
                                    .and(functionsFilter).and(realTimeStatusFilter).and(phaseFilter).and(plannedByFilter).and(groupFilter).and(escalationFilter);
        return shiftFilter.meetCriteria(shiftWithActivityDTOS);
        /*List<Long> staffIds = shiftWithActivityDTOS.stream().map(s->s.getStaffId()).collect(Collectors.toList());
        ShiftFilter nightWorkerFilter = getNightWorkerFilter(staffIds, filterTypeMap);
        return nightWorkerFilter.meetCriteria(shiftWithActivityDTOS);*/
    }

    private ShiftFilter getEscalationFilter(List<BigInteger> shiftIds, Map<FilterType, Set<String>> filterTypeMap){
        List<ShiftViolatedRules> shiftViolatedRules = shiftValidatorService.findAllViolatedRulesByShiftIds(shiftIds,false);
        Map<BigInteger, ShiftViolatedRules> shiftViolatedRulesMap = shiftViolatedRules.stream().collect(Collectors.toMap(k -> k.getShiftId(), v -> v));
        return new EscalationFilter(shiftViolatedRulesMap, filterTypeMap);
    }

    private ShiftFilter getGroupFilter(Long unitId, Map<FilterType, Set<String>> filterTypeMap) {
        Set<Long> groupMembers = new HashSet<>();
        if(filterTypeMap.containsKey(GROUPS) && isCollectionNotEmpty(filterTypeMap.get(GROUPS))) {
            List<Long> groupIds = filterTypeMap.get(GROUPS).stream().map(s -> new Long(s)).collect(Collectors.toList());
            groupMembers = userIntegrationService.getAllStaffIdsByGroupIds(unitId, groupIds);
        }
        return new GroupFilter(groupMembers,filterTypeMap);
    }

    private ShiftFilter getSickTimeTypeFilter(Long unitId, Map<FilterType, Set<String>> filterTypeMap) {
        Set<BigInteger> sickTimeTypes = new HashSet<>();
        if(filterTypeMap.containsKey(REAL_TIME_STATUS) && isCollectionNotEmpty(filterTypeMap.get(REAL_TIME_STATUS))) {
            sickTimeTypes = userIntegrationService.getSickSettingsOfUnit(unitId);
        }
        return new RealTimeStatusFilter(filterTypeMap, sickTimeTypes);
    }

    private ShiftFilter getFunctionFilter(Long unitId, Map<FilterType, Set<String>> filterTypeMap) {
        Set<LocalDate> functionDates = new HashSet<>();
        if(filterTypeMap.containsKey(FilterType.FUNCTIONS) && isCollectionNotEmpty(filterTypeMap.get(FUNCTIONS))) {
            List<Long> functionIds = filterTypeMap.get(FUNCTIONS).stream().map(s -> new Long(s)).collect(Collectors.toList());
            functionDates = userIntegrationService.getAllDateByFunctionIds(unitId, functionIds);
        }
        return new FunctionsFilter(filterTypeMap, functionDates);
    }

    private <T extends ShiftDTO> ShiftFilter getValidatedFilter(List<T> shiftWithActivityDTOS, List<BigInteger> shiftStateIds, Map<FilterType, Set<String>> filterTypeMap) {
        if(filterTypeMap.containsKey(FilterType.VALIDATED_BY) && isCollectionNotEmpty(filterTypeMap.get(VALIDATED_BY))) {
            Set<BigInteger> shiftIds = shiftWithActivityDTOS.stream().map(shiftDTO -> shiftDTO.getId()).collect(Collectors.toSet());
            List<ShiftState> shiftStates = shiftStateService.findAllByShiftIdsByAccessgroupRole(shiftIds, filterTypeMap.get(FilterType.VALIDATED_BY));
            shiftStateIds=shiftStates.stream().map(shiftState -> shiftState.getShiftId()).collect(Collectors.toList());
        }
        return new TimeAndAttendanceFilter(filterTypeMap,shiftStateIds);
    }

    private ShiftFilter getActivityFilter(Long unitId, Map<FilterType, Set<String>> filterTypeMap) {
        List<BigInteger> selectedActivityIds = new ArrayList<>();
        if(filterTypeMap.containsKey(ABSENCE_ACTIVITY) && isCollectionNotEmpty(filterTypeMap.get(ABSENCE_ACTIVITY))) {
            selectedActivityIds.addAll(filterTypeMap.get(ABSENCE_ACTIVITY).stream().map(s -> new BigInteger(s)).collect(Collectors.toList()));
        }
        if(filterTypeMap.containsKey(TEAM) && isCollectionNotEmpty(filterTypeMap.get(TEAM))){
            Set<String> teamIds = KPIUtils.getStringByList(filterTypeMap.get(TEAM));
            ShiftFilterDefaultData shiftFilterDefaultData = userIntegrationService.getShiftFilterDefaultData(new SelfRosteringFilterDTO(unitId,teamIds));
            selectedActivityIds.addAll(shiftFilterDefaultData.getTeamActivityIds());
        }
        return new ActivityFilter(filterTypeMap, selectedActivityIds);
    }

    private ShiftFilter getTimeTypeFilter(Map<FilterType, Set<String>> filterTypeMap) {
        Set<BigInteger> timeTypeIds = new HashSet<>();
        if(filterTypeMap.containsKey(TIME_TYPE) && isCollectionNotEmpty(filterTypeMap.get(TIME_TYPE))) {
            Set<BigInteger> ids = new HashSet<>(getBigInteger(filterTypeMap.get(TIME_TYPE)));
            timeTypeIds = timeTypeService.getAllTimeTypeWithItsLowerLevel(UserContext.getUserDetails().getCountryId(), ids).keySet();
        }
        return new TimeTypeFilter(filterTypeMap, timeTypeIds);
    }

    private <T> List<BigInteger> getBigInteger(Collection<T> objects) {
        List<BigInteger> ids = new ArrayList<>();
        for (T object : objects) {
            String id = (object instanceof String) ? (String) object : ""+object;
            ids.add(new BigInteger(id));
        }
        return ids;
    }

    private ShiftFilter getPlannedByFilter(Long unitId,Map<FilterType, Set<String>> filterTypeMap) {
        Set<Long> staffUserIds = new HashSet<>();
        if(filterTypeMap.containsKey(PLANNED_BY) && isCollectionNotEmpty(filterTypeMap.get(PLANNED_BY))){
            List<StaffPersonalDetail> staffDTOS = userIntegrationService.getStaffByUnitId(unitId);
            Set<AccessGroupRole> accessGroups = filterTypeMap.get(PLANNED_BY).stream().map(s -> AccessGroupRole.valueOf(s)).collect(Collectors.toSet());
            for (StaffPersonalDetail staffDTO : staffDTOS) {
                if(isNotNull(staffDTO.getRoles()) && CollectionUtils.containsAny(staffDTO.getRoles(),accessGroups)){
                    staffUserIds.add(staffDTO.getStaffUserId());
                }
            }
        }
        return new PlannedByFilter(staffUserIds,filterTypeMap);
    }

    private ShiftFilter getNightWorkerFilter(List<Long> staffIds, Map<FilterType, Set<String>> filterTypeMap){
        Map<Long, Boolean> nightWorkerMap = nightWorkerService.getStaffIdAndNightWorkerMap(staffIds);
        return new NightWorkerFilter(nightWorkerMap, filterTypeMap);
    }

}
