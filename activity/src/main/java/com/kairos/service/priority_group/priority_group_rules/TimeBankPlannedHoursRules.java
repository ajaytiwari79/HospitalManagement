package com.kairos.service.priority_group.priority_group_rules;

import com.kairos.dto.activity.open_shift.priority_group.PriorityGroupDTO;
import com.kairos.dto.user.staff.employment.StaffEmploymentQueryResult;

import java.math.BigInteger;
import java.util.*;

public class TimeBankPlannedHoursRules implements PriorityGroupRuleFilter{


    public void filter(Map<BigInteger, List<StaffEmploymentQueryResult>> openShiftStaffMap, PriorityGroupDTO priorityGroupDTO){

        for(Map.Entry<BigInteger,List<StaffEmploymentQueryResult>> entry: openShiftStaffMap.entrySet()) {
            Iterator<StaffEmploymentQueryResult> staffEmploymentIterator = entry.getValue().iterator();

            while(staffEmploymentIterator.hasNext()) {

                StaffEmploymentQueryResult staffEmploymentQueryResult = staffEmploymentIterator.next();
                if((Optional.ofNullable(priorityGroupDTO.getStaffExcludeFilter().getMinTimeBank()).isPresent()&&
                        staffEmploymentQueryResult.getAccumulatedTimeBank()<priorityGroupDTO.getStaffExcludeFilter().getMinTimeBank())||
                        (Optional.ofNullable(priorityGroupDTO.getStaffExcludeFilter().getMaxPlannedTime()).isPresent()&&
                        staffEmploymentQueryResult.getPlannedHoursWeek()>priorityGroupDTO.getStaffExcludeFilter().getMaxPlannedTime())||
                (Optional.ofNullable(priorityGroupDTO.getStaffExcludeFilter().getMaxDeltaWeeklyTimeBankPerWeek()).isPresent()&&
                        staffEmploymentQueryResult.getDeltaWeeklytimeBank()>priorityGroupDTO.getStaffExcludeFilter().getMaxDeltaWeeklyTimeBankPerWeek())) {
                  //  staffEmploymentIterator.remove();
                }


            }
        }

    }




}
