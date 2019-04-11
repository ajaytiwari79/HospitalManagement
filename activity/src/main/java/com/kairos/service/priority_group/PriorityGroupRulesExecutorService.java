package com.kairos.service.priority_group;

import com.kairos.dto.activity.open_shift.FibonacciCounter;
import com.kairos.dto.activity.open_shift.priority_group.PriorityGroupDTO;
import com.kairos.dto.user.staff.unit_position.StaffEmploymentQueryResult;
import com.kairos.service.priority_group.priority_group_rules.*;
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
            PriorityGroupRuleFilter priorityGroupRuleFilter = new LastWorkInUnitAndActivityRule(priorityGroupRuleDataDTO.getShiftEmploymentsMap(),
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
        Map<BigInteger,List<StaffEmploymentQueryResult>> openShiftStaffMap = priorityGroupRuleDataDTO.getOpenShiftStaffMap();
        for(PriorityGroupRuleFilter priorityGroupRule : priorityGroupRules) {
            priorityGroupRule.filter(openShiftStaffMap,priorityGroupDTO);
        }

        Map<BigInteger,List<StaffEmploymentQueryResult>> openShiftStaffMapFibonacci = new HashMap<>();
        for(Map.Entry<BigInteger,List<StaffEmploymentQueryResult>> entry:openShiftStaffMap.entrySet()) {
            openShiftStaffMapFibonacci.put(entry.getKey(),applyFibonacci(entry.getValue(),priorityGroupRuleDataDTO.getAssignedOpenShiftMap(),impactWeight));
        }

        openShiftStaffMap = openShiftStaffMapFibonacci;
        priorityGroupRuleDataDTO.setOpenShiftStaffMap(openShiftStaffMap);
    }

    public List<StaffEmploymentQueryResult> applyFibonacci(List<StaffEmploymentQueryResult> staffsEmployments, Map<Long,Integer> assignedOpenShiftMap, ImpactWeight impactWeight) {

        FibonacciCounterApply fibonacciCounterApply = new FibonacciCounterApply();
        List<FibonacciCounter> fibonacciCounters = fibonacciCounterApply.findBestCandidates(impactWeight,staffsEmployments, assignedOpenShiftMap);
        Map<Long,StaffEmploymentQueryResult> staffEmploymentMap = staffsEmployments.stream().collect(Collectors.toMap(
                StaffEmploymentQueryResult::getStaffId, staffEmploymentQueryResult -> staffEmploymentQueryResult));
        staffsEmployments = new ArrayList<>();
        for(FibonacciCounter fibonacciCounter:fibonacciCounters) {
            staffsEmployments.add(staffEmploymentMap.get(fibonacciCounter.getStaffId()));
        }

        return staffsEmployments;
    }


}
