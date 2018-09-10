package com.kairos.service.priority_group.priority_group_rules;


import com.kairos.dto.activity.open_shift.priority_group.PriorityGroupDTO;
import com.kairos.dto.user.staff.unit_position.StaffUnitPositionQueryResult;

import java.math.BigInteger;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class TimeBankPlannedHoursRules implements PriorityGroupRuleFilter{


    public void filter(Map<BigInteger, List<StaffUnitPositionQueryResult>> openShiftStaffMap, PriorityGroupDTO priorityGroupDTO){

        for(Map.Entry<BigInteger,List<StaffUnitPositionQueryResult>> entry: openShiftStaffMap.entrySet()) {
            Iterator<StaffUnitPositionQueryResult> staffUnitPositionIterator = entry.getValue().iterator();

            while(staffUnitPositionIterator.hasNext()) {

                StaffUnitPositionQueryResult staffUnitPositionQueryResult = staffUnitPositionIterator.next();
                if((Optional.ofNullable(priorityGroupDTO.getStaffExcludeFilter().getMinTimeBank()).isPresent()&&
                        staffUnitPositionQueryResult.getAccumulatedTimeBank()<priorityGroupDTO.getStaffExcludeFilter().getMinTimeBank())||
                        (Optional.ofNullable(priorityGroupDTO.getStaffExcludeFilter().getMaxPlannedTime()).isPresent()&&
                        staffUnitPositionQueryResult.getPlannedHoursWeek()>priorityGroupDTO.getStaffExcludeFilter().getMaxPlannedTime())||
                (Optional.ofNullable(priorityGroupDTO.getStaffExcludeFilter().getMaxDeltaWeeklyTimeBankPerWeek()).isPresent()&&
                        staffUnitPositionQueryResult.getDeltaWeeklytimeBank()>priorityGroupDTO.getStaffExcludeFilter().getMaxDeltaWeeklyTimeBankPerWeek())) {
                  //  staffUnitPositionIterator.remove();
                }


            }
        }

    }




}
