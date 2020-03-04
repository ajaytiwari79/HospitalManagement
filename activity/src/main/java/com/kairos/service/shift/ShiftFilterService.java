package com.kairos.service.shift;

import com.kairos.dto.activity.kpi.StaffKpiFilterDTO;
import com.kairos.dto.activity.shift.SelfRosteringFilterDTO;
import com.kairos.dto.activity.shift.ShiftDTO;
import com.kairos.dto.activity.shift.ShiftFilterDefaultData;
import com.kairos.dto.activity.time_bank.EmploymentWithCtaDetailsDTO;
import com.kairos.dto.gdpr.FilterSelectionDTO;
import com.kairos.dto.user.access_permission.AccessGroupRole;
import com.kairos.dto.user.country.time_slot.TimeSlotDTO;
import com.kairos.dto.user.employment.EmploymentLinesDTO;
import com.kairos.dto.user.staff.StaffFilterDTO;
import com.kairos.dto.user.team.TeamDTO;
import com.kairos.dto.user_context.UserContext;
import com.kairos.enums.EmploymentSubType;
import com.kairos.enums.FilterType;
import com.kairos.persistence.model.shift.ShiftState;
import com.kairos.persistence.model.shift.ShiftViolatedRules;
import com.kairos.persistence.model.staff.personal_details.StaffPersonalDetail;
import com.kairos.rest_client.UserIntegrationService;
import com.kairos.service.activity.TimeTypeService;
import com.kairos.service.counter.KPIBuilderCalculationService;
import com.kairos.service.night_worker.NightWorkerService;
import com.kairos.service.time_bank.TimeBankService;
import com.kairos.utils.counter.KPIUtils;
import org.apache.commons.collections.CollectionUtils;
import org.apache.poi.ss.formula.functions.T;
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
    @Inject
    private TimeBankService timeBankService;

    public <T extends ShiftDTO> List<T> getShiftsByFilters(List<T> shiftWithActivityDTOS, StaffFilterDTO staffFilterDTO,List<StaffKpiFilterDTO> staffKpiFilterDTOS) {
        List<BigInteger> shiftStateIds=new ArrayList<>();
        Long unitId = UserContext.getUserDetails().getLastSelectedOrganizationId();
        if (isNull(staffFilterDTO)) {
            staffFilterDTO = new StaffFilterDTO();
            staffFilterDTO.setFiltersData(new ArrayList<>());
        }
        List<TimeSlotDTO> timeSlotDTOS = userIntegrationService.getUnitTimeSlot(unitId);
        Map<FilterType, Set<T>> filterTypeMap = staffFilterDTO.getFiltersData().stream().filter(distinctByKey(filterSelectionDTO -> filterSelectionDTO.getName())).collect(Collectors.toMap(FilterSelectionDTO::getName, v -> v.getValue()));
        ShiftFilter timeTypeFilter = getTimeTypeFilter(filterTypeMap);
        ShiftFilter activityTimecalculationTypeFilter = new ActivityTimeCalculationTypeFilter(filterTypeMap);
        ShiftFilter activityStatusFilter = new ActivityStatusFilter(filterTypeMap);
        ShiftFilter timeSlotFilter = new TimeSlotFilter(filterTypeMap,timeSlotDTOS);
        ShiftFilter activityFilter = getActivityFilter(unitId, filterTypeMap);
        ShiftFilter plannedTimeTypeFilter=new PlannedTimeTypeFilter(filterTypeMap);
        ShiftFilter timeAndAttendanceFilter = getValidatedFilter(shiftWithActivityDTOS, shiftStateIds, filterTypeMap);
        ShiftFilter functionsFilter = getFunctionFilter(unitId, filterTypeMap);
        ShiftFilter realTimeStatusFilter = getSickTimeTypeFilter(unitId, filterTypeMap);
        ShiftFilter plannedByFilter = getPlannedByFilter(unitId,filterTypeMap);
        ShiftFilter phaseFilter = new PhaseFilter(filterTypeMap);
        ShiftFilter groupFilter = getGroupFilter(unitId, filterTypeMap);
        ShiftFilter escalationFilter = getEscalationFilter(shiftWithActivityDTOS.stream().map(shift->shift.getId()).collect(Collectors.toList()), filterTypeMap);
        Set<Long> employmentIds = shiftWithActivityDTOS.stream().map(s->s.getEmploymentId()).collect(Collectors.toSet());
        ShiftFilter timeBankBalanceFilter = getTimeBankBalanceFilter(unitId, filterTypeMap, employmentIds);
        ShiftFilter employmentTypeFilter = getEmploymentTypeFilter(filterTypeMap,staffKpiFilterDTOS);
        ShiftFilter employmentSubTypeFilter = getEmploymentSubTypeFilter(filterTypeMap,staffKpiFilterDTOS);
        ShiftFilter shiftFilter = new AndShiftFilter(timeTypeFilter, activityTimecalculationTypeFilter).and(activityStatusFilter).and(timeSlotFilter).and(activityFilter).and(plannedTimeTypeFilter).and(timeAndAttendanceFilter)
                                    .and(functionsFilter).and(realTimeStatusFilter).and(phaseFilter).and(plannedByFilter).and(groupFilter).and(escalationFilter)
                                    .and(timeBankBalanceFilter).and(employmentTypeFilter).and(employmentSubTypeFilter);
        return shiftFilter.meetCriteria(shiftWithActivityDTOS);
    }



    private <G> ShiftFilter getTimeBankBalanceFilter(Long unitId, Map<FilterType, Set<G>> filterTypeMap, Set<Long> employmentIds) {
        //Update loop in a single call
        Map<Long,Long> employmentIdAndActualTimeBankData = new HashMap<>();
        if(filterTypeMap.containsKey(TIME_BANK_BALANCE) && isCollectionNotEmpty(filterTypeMap.get(TIME_BANK_BALANCE))) {
            for (Long employmentId : employmentIds) {
                Long timeBank = timeBankService.getAccumulatedTimebankAndDelta(employmentId, unitId, true);
                employmentIdAndActualTimeBankData.put(employmentId, timeBank);
            }
        }
        return new TimeBankBalanceFilter(filterTypeMap, employmentIdAndActualTimeBankData);
    }

    private <G> ShiftFilter getEscalationFilter(List<BigInteger> shiftIds, Map<FilterType, Set<G>> filterTypeMap){
        Map<BigInteger, ShiftViolatedRules> shiftViolatedRulesMap = new HashMap<>();
        if(filterTypeMap.containsKey(ESCALATION_CAUSED_BY) && isCollectionNotEmpty(filterTypeMap.get(ESCALATION_CAUSED_BY))) {
            List<ShiftViolatedRules> shiftViolatedRules = shiftValidatorService.findAllViolatedRulesByShiftIds(shiftIds, false);
            shiftViolatedRulesMap = shiftViolatedRules.stream().collect(Collectors.toMap(k -> k.getShiftId(), v -> v));
        }
        return new EscalationFilter(shiftViolatedRulesMap, filterTypeMap);
    }

    private <G> ShiftFilter getGroupFilter(Long unitId, Map<FilterType, Set<G>> filterTypeMap) {
        Set<Long> groupMembers = new HashSet<>();
        if(filterTypeMap.containsKey(GROUPS) && isCollectionNotEmpty(filterTypeMap.get(GROUPS))) {
            List<Long> groupIds = filterTypeMap.get(GROUPS).stream().map(s -> new Long(s.toString())).collect(Collectors.toList());
            groupMembers = userIntegrationService.getAllStaffIdsByGroupIds(unitId, groupIds);
        }
        return new GroupFilter(groupMembers,filterTypeMap);
    }

    private <G> ShiftFilter getSickTimeTypeFilter(Long unitId, Map<FilterType, Set<G>> filterTypeMap) {
        Set<BigInteger> sickTimeTypes = new HashSet<>();
        if(filterTypeMap.containsKey(REAL_TIME_STATUS) && isCollectionNotEmpty(filterTypeMap.get(REAL_TIME_STATUS))) {
            sickTimeTypes = userIntegrationService.getSickSettingsOfUnit(unitId);
        }
        return new RealTimeStatusFilter(filterTypeMap, sickTimeTypes);
    }

    private <G> ShiftFilter getFunctionFilter(Long unitId, Map<FilterType, Set<G>> filterTypeMap) {
        Set<LocalDate> functionDates = new HashSet<>();
        if(filterTypeMap.containsKey(FilterType.FUNCTIONS) && isCollectionNotEmpty(filterTypeMap.get(FUNCTIONS))) {
            List<Long> functionIds = filterTypeMap.get(FUNCTIONS).stream().map(s -> new Long(s.toString())).collect(Collectors.toList());
            functionDates = userIntegrationService.getAllDateByFunctionIds(unitId, functionIds);
        }
        return new FunctionsFilter(filterTypeMap, functionDates);
    }

    private <T extends ShiftDTO, G> ShiftFilter getValidatedFilter(List<T> shiftWithActivityDTOS, List<BigInteger> shiftStateIds, Map<FilterType, Set<G>> filterTypeMap) {
        if(filterTypeMap.containsKey(FilterType.VALIDATED_BY) && isCollectionNotEmpty(filterTypeMap.get(VALIDATED_BY))) {
            Set<BigInteger> shiftIds = shiftWithActivityDTOS.stream().map(shiftDTO -> shiftDTO.getId()).collect(Collectors.toSet());
            List<ShiftState> shiftStates = shiftStateService.findAllByShiftIdsByAccessgroupRole(shiftIds, filterTypeMap.get(FilterType.VALIDATED_BY).stream().map(v->v.toString()).collect(Collectors.toSet()));
            shiftStateIds=shiftStates.stream().map(shiftState -> shiftState.getShiftId()).collect(Collectors.toList());
        }
        return new TimeAndAttendanceFilter(filterTypeMap,shiftStateIds);
    }

    private <G> ShiftFilter getActivityFilter(Long unitId, Map<FilterType, Set<G>> filterTypeMap) {
        List<BigInteger> selectedActivityIds = new ArrayList<>();
        if(filterTypeMap.containsKey(ABSENCE_ACTIVITY) && isCollectionNotEmpty(filterTypeMap.get(ABSENCE_ACTIVITY))) {
            selectedActivityIds.addAll(filterTypeMap.get(ABSENCE_ACTIVITY).stream().map(s -> new BigInteger(s.toString())).collect(Collectors.toList()));
        }
        if(filterTypeMap.containsKey(TEAM) && isCollectionNotEmpty(filterTypeMap.get(TEAM))){
            Set<String> teamIds = KPIUtils.getStringByList(filterTypeMap.get(TEAM));
            ShiftFilterDefaultData shiftFilterDefaultData = userIntegrationService.getShiftFilterDefaultData(new SelfRosteringFilterDTO(unitId,teamIds));
            selectedActivityIds.addAll(shiftFilterDefaultData.getTeamActivityIds());
        }
        return new ActivityFilter(filterTypeMap, selectedActivityIds);
    }


    private <G> ShiftFilter getTimeTypeFilter(Map<FilterType, Set<G>> filterTypeMap) {
        Set<BigInteger> timeTypeIds = new HashSet<>();
        if(filterTypeMap.containsKey(TIME_TYPE) && isCollectionNotEmpty(filterTypeMap.get(TIME_TYPE))) {
            Set<BigInteger> ids = new HashSet<>(getBigInteger(filterTypeMap.get(TIME_TYPE)));
            timeTypeIds = timeTypeService.getAllTimeTypeWithItsLowerLevel(UserContext.getUserDetails().getCountryId(), ids).keySet();
        }
        return new TimeTypeFilter(filterTypeMap, timeTypeIds);
    }

    private <G> ShiftFilter getEmploymentTypeFilter(Map<FilterType, Set<G>> filterTypeMap,List<StaffKpiFilterDTO> staffKpiFilterDTOS){
        Map<Long,Long> employmentIdAndEmploymentTypeIdMap = new HashMap<>();
        if(filterTypeMap.containsKey(EMPLOYMENT_TYPE)&&isCollectionNotEmpty(filterTypeMap.get(EMPLOYMENT_TYPE))){
            if(isCollectionEmpty(staffKpiFilterDTOS)){
                filterTypeMap.remove(EMPLOYMENT_TYPE);
            }else {
                employmentIdAndEmploymentTypeIdMap = getEmploymentIdAndEmploymentTypeIdMap(staffKpiFilterDTOS);
            }
       }
       return new EmploymentTypeFilter(filterTypeMap,employmentIdAndEmploymentTypeIdMap);
    }

    private <G> ShiftFilter getEmploymentSubTypeFilter(Map<FilterType, Set<G>> filterTypeMap,List<StaffKpiFilterDTO> staffKpiFilterDTOS){
        Map<Long,EmploymentSubType> employmentIdAndEmploymentSubTypeIdMap = new HashMap<>();
        if(filterTypeMap.containsKey(EMPLOYMENT_SUB_TYPE)&&isCollectionNotEmpty(filterTypeMap.get(EMPLOYMENT_SUB_TYPE))) {
          if(isCollectionNotEmpty(staffKpiFilterDTOS)) {
              filterTypeMap.remove(EMPLOYMENT_SUB_TYPE);
          }
           else {
               employmentIdAndEmploymentSubTypeIdMap = getEmploymentIdAndEmploymentSubType(staffKpiFilterDTOS);
            }

        }
        return new EmploymentSubTypeFilter(filterTypeMap,employmentIdAndEmploymentSubTypeIdMap);
    }

    private Map<Long, Long> getEmploymentIdAndEmploymentTypeIdMap(List<StaffKpiFilterDTO> staffKpiFilterDTOS){
        Map<Long,Long> employmentIdAndEmploymentTypeIdMap = new HashMap<>();
        for(StaffKpiFilterDTO staffKpiFilterDTO : staffKpiFilterDTOS){
            for(EmploymentWithCtaDetailsDTO employmentWithCtaDetailsDTO :staffKpiFilterDTO.getEmployment()){
                employmentIdAndEmploymentTypeIdMap.put(employmentWithCtaDetailsDTO.getId(),employmentWithCtaDetailsDTO.getEmploymentLines().get(0).getEmploymentTypeId());
            }
        }
        return employmentIdAndEmploymentTypeIdMap;
    }

    private Map<Long, EmploymentSubType> getEmploymentIdAndEmploymentSubType(List<StaffKpiFilterDTO> staffKpiFilterDTOS){
        Map<Long, EmploymentSubType> employmentIdAndEmploymentSubTypeIdMap = new HashMap<>();
        for(StaffKpiFilterDTO staffKpiFilterDTO : staffKpiFilterDTOS){
            for(EmploymentWithCtaDetailsDTO employmentWithCtaDetailsDTO :staffKpiFilterDTO.getEmployment()){
                List<EmploymentLinesDTO> employmentLinesDTOS = employmentWithCtaDetailsDTO.getEmploymentLines();
                Collections.sort(employmentLinesDTOS);
                employmentIdAndEmploymentSubTypeIdMap.put(employmentWithCtaDetailsDTO.getId(),employmentLinesDTOS.get(employmentLinesDTOS.size()-1).getEmploymentSubType());
            }
        }
        return employmentIdAndEmploymentSubTypeIdMap;
    }

    private <T> List<BigInteger> getBigInteger(Collection<T> objects) {
        List<BigInteger> ids = new ArrayList<>();
        for (T object : objects) {
            String id = (object instanceof String) ? (String) object : ""+object;
            ids.add(new BigInteger(id));
        }
        return ids;
    }

    private <G> ShiftFilter getPlannedByFilter(Long unitId,Map<FilterType, Set<G>> filterTypeMap) {
        Set<Long> staffUserIds = new HashSet<>();
        if(filterTypeMap.containsKey(PLANNED_BY) && isCollectionNotEmpty(filterTypeMap.get(PLANNED_BY))){
            List<StaffPersonalDetail> staffDTOS = userIntegrationService.getStaffByUnitId(unitId);
            Set<AccessGroupRole> accessGroups = filterTypeMap.get(PLANNED_BY).stream().map(s -> AccessGroupRole.valueOf(s.toString())).collect(Collectors.toSet());
            for (StaffPersonalDetail staffDTO : staffDTOS) {
                if(isNotNull(staffDTO.getRoles()) && CollectionUtils.containsAny(staffDTO.getRoles(),accessGroups)){
                    staffUserIds.add(staffDTO.getStaffUserId());
                }
            }
        }
        return new PlannedByFilter(staffUserIds,filterTypeMap);
    }

}
