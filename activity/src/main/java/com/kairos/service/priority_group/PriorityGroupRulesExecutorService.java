package com.kairos.service.priority_group;

import com.kairos.dto.activity.open_shift.FibonacciCounter;
import com.kairos.dto.activity.open_shift.priority_group.PriorityGroupDTO;
import com.kairos.service.priority_group.priority_group_rules.*;
import com.kairos.dto.user.staff.unit_position.StaffUnitPositionQueryResult;
import com.kairos.wrapper.priority_group.PriorityGroupRuleDataDTO;
import org.springframework.stereotype.Component;

import java.math.BigInteger;
import java.util.*;
import java.util.stream.Collectors;

@Component
public class PriorityGroupRulesExecutorService {


    public PriorityGroupRulesExecutorService() {

    }
// TODO refactor this method to createRulesList
    private List<PriorityGroupRuleFilter> getRulesList(PriorityGroupDTO priorityGroupDTO,PriorityGroupRuleDataDTO priorityGroupRuleDataDTO) {

        List<PriorityGroupRuleFilter> priorityGroupRules = new ArrayList<>();
        if(Optional.ofNullable(priorityGroupDTO.getStaffExcludeFilter().getMinTimeBank()).isPresent()||
                Optional.ofNullable(priorityGroupDTO.getStaffExcludeFilter().getMaxDeltaWeeklyTimeBankPerWeek()).isPresent()||
                Optional.ofNullable(priorityGroupDTO.getStaffExcludeFilter().getMaxPlannedTime()).isPresent()) {
            PriorityGroupRuleFilter priorityGroupRuleFilter = new TimeBankPlannedHoursRules();
            priorityGroupRules.add(priorityGroupRuleFilter);
        }
        if(Optional.ofNullable(priorityGroupDTO.getStaffExcludeFilter().getLastWorkingDaysInUnit()).isPresent()||
                Optional.ofNullable(priorityGroupDTO.getStaffExcludeFilter().getLastWorkingDaysWithActivity()).isPresent()) {
            PriorityGroupRuleFilter priorityGroupRuleFilter = new LastWorkInUnitAndActivityRule(priorityGroupRuleDataDTO.getShiftUnitPositionsMap(),
                    priorityGroupRuleDataDTO.getOpenShiftMap());

            priorityGroupRules.add(priorityGroupRuleFilter);
        }
        if(Optional.ofNullable(priorityGroupDTO.getStaffExcludeFilter().getMinRestingTimeBeforeShiftStart()).isPresent()||
                Optional.ofNullable(priorityGroupDTO.getStaffExcludeFilter().getMinRestingTimeAfterShiftEnd()).isPresent()) {
            PriorityGroupRuleFilter priorityGroupRuleFilter = new RestingHoursRule(priorityGroupRuleDataDTO.getOpenShiftMap(),priorityGroupRuleDataDTO.getShifts());
            priorityGroupRules.add(priorityGroupRuleFilter);
        }
        if(priorityGroupDTO.getStaffExcludeFilter().isNegativeAvailabilityInCalender()||
                Optional.ofNullable(priorityGroupDTO.getStaffIncludeFilter().getStaffAvailability()).isPresent()) {
            PriorityGroupRuleFilter priorityGroupRuleFilter = new NegativeAvailabilityAndPercentAvailabilityRule(priorityGroupRuleDataDTO.getUnavailableActivitySet(),
                    priorityGroupRuleDataDTO.getShifts(),priorityGroupRuleDataDTO.getOpenShiftMap());
            priorityGroupRules.add(priorityGroupRuleFilter);
        }
        if(Optional.ofNullable(priorityGroupDTO.getStaffExcludeFilter().getNumberOfPendingRequest()).isPresent()) {
            PriorityGroupRuleFilter priorityGroupRuleFilter = new PendingRequestRule(priorityGroupRuleDataDTO.getOpenShiftNotifications());
            priorityGroupRules.add(priorityGroupRuleFilter);
        }
        if(Optional.ofNullable(priorityGroupDTO.getStaffExcludeFilter().getUnitExperienceInWeek()).isPresent()||
                Optional.ofNullable(priorityGroupDTO.getStaffExcludeFilter().getNumberOfShiftAssigned()).isPresent()) {
            PriorityGroupRuleFilter priorityGroupRuleFilter = new UnitExperienceAndAssignedOpenShiftRule(priorityGroupRuleDataDTO.getAssignedOpenShiftMap());
            priorityGroupRules.add(priorityGroupRuleFilter);

        }


        return priorityGroupRules;
    }


    public void executeRules(PriorityGroupDTO priorityGroupDTO, PriorityGroupRuleDataDTO priorityGroupRuleDataDTO, ImpactWeight impactWeight) {

        List<PriorityGroupRuleFilter> priorityGroupRules = getRulesList(priorityGroupDTO,priorityGroupRuleDataDTO);
        Map<BigInteger,List<StaffUnitPositionQueryResult>> openShiftStaffMap = priorityGroupRuleDataDTO.getOpenShiftStaffMap();
        for(PriorityGroupRuleFilter priorityGroupRule : priorityGroupRules) {
            priorityGroupRule.filter(openShiftStaffMap,priorityGroupDTO);
        }

        Map<BigInteger,List<StaffUnitPositionQueryResult>> openShiftStaffMapFibonacci = new HashMap<>();
        for(Map.Entry<BigInteger,List<StaffUnitPositionQueryResult>> entry:openShiftStaffMap.entrySet()) {
            openShiftStaffMapFibonacci.put(entry.getKey(),applyFibonacci(entry.getValue(),priorityGroupRuleDataDTO.getAssignedOpenShiftMap(),impactWeight));
        }

        openShiftStaffMap = openShiftStaffMapFibonacci;
        priorityGroupRuleDataDTO.setOpenShiftStaffMap(openShiftStaffMap);
    }

    public List<StaffUnitPositionQueryResult> applyFibonacci(List<StaffUnitPositionQueryResult> staffsUnitPositions, Map<Long,Integer> assignedOpenShiftMap, ImpactWeight impactWeight) {

        FibonacciCounterApply fibonacciCounterApply = new FibonacciCounterApply();
        List<FibonacciCounter> fibonacciCounters = fibonacciCounterApply.findBestCandidates(impactWeight,staffsUnitPositions, assignedOpenShiftMap);
        Map<Long,StaffUnitPositionQueryResult> staffUnitPositionMap = staffsUnitPositions.stream().collect(Collectors.toMap(
                StaffUnitPositionQueryResult::getStaffId,staffUnitPositionQueryResult -> staffUnitPositionQueryResult));
        staffsUnitPositions = new ArrayList<>();
        for(FibonacciCounter fibonacciCounter:fibonacciCounters) {
            staffsUnitPositions.add(staffUnitPositionMap.get(fibonacciCounter.getStaffId()));
        }

        return staffsUnitPositions;
    }


}
