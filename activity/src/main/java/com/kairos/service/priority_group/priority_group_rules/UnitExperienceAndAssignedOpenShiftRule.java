package com.kairos.service.priority_group.priority_group_rules;

import com.kairos.activity.open_shift.priority_group.PriorityGroupDTO;
import com.kairos.user.staff.unit_position.StaffUnitPositionQueryResult;
import com.kairos.util.DateUtils;

import java.math.BigInteger;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class UnitExperienceAndAssignedOpenShiftRule implements PriorityGroupRuleFilter{

    private Map<Long,Integer> assignedOpenShiftMap;

    public UnitExperienceAndAssignedOpenShiftRule(Map<Long,Integer> assignedOpenShiftMap) {
        this.assignedOpenShiftMap = assignedOpenShiftMap;
    }
    @Override
    public void filter(Map<BigInteger, List<StaffUnitPositionQueryResult>> openShiftStaffMap, PriorityGroupDTO priorityGroupDTO) {

        int thresholdShiftCount = priorityGroupDTO.getStaffExcludeFilter().getNumberOfShiftAssigned();
        int experienceInDays = priorityGroupDTO.getStaffExcludeFilter().getUnitExperienceInWeek()*7;
        Long startDate = DateUtils.getLongFromLocalDate(LocalDate.now().minusDays(experienceInDays));
        for(Map.Entry<BigInteger,List<StaffUnitPositionQueryResult>> entry: openShiftStaffMap.entrySet()) {
            entry.getValue().removeIf(staffUnitPosition-> (Optional.ofNullable(priorityGroupDTO.getStaffExcludeFilter().getNumberOfShiftAssigned()).isPresent()&&
                    assignedOpenShiftMap.containsKey(staffUnitPosition.getUnitPositionId())
                    &&(assignedOpenShiftMap.get(staffUnitPosition.getUnitPositionId())>thresholdShiftCount))||
                    (Optional.ofNullable(priorityGroupDTO.getStaffExcludeFilter().getUnitExperienceInWeek()).isPresent()&&staffUnitPosition.getStartDate()<=startDate));
        }
    }

   }
