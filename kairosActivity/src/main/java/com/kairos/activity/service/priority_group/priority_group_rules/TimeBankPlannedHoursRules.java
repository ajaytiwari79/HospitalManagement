package com.kairos.activity.service.priority_group.priority_group_rules;

import com.kairos.activity.persistence.model.open_shift.OpenShift;
import com.kairos.activity.persistence.model.time_bank.DailyTimeBankEntry;
import com.kairos.activity.response.dto.time_bank.UnitPositionWithCtaDetailsDTO;
import com.kairos.activity.util.DateUtils;
import com.kairos.response.dto.web.StaffUnitPositionQueryResult;
import com.kairos.response.dto.web.open_shift.priority_group.PriorityGroupDTO;
import org.joda.time.Interval;

import java.math.BigInteger;
import java.time.LocalDate;
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
                if(Optional.ofNullable(priorityGroupDTO.getStaffExcludeFilter().getMinTimeBank()).isPresent()&&
                        staffUnitPositionQueryResult.getAccumulatedTimeBank()<priorityGroupDTO.getStaffExcludeFilter().getMinTimeBank()) {
                    staffUnitPositionIterator.remove();
                }
                if(Optional.ofNullable(priorityGroupDTO.getStaffExcludeFilter().getMaxPlannedTime()).isPresent()&&
                        staffUnitPositionQueryResult.getPlannedHoursWeek()>priorityGroupDTO.getStaffExcludeFilter().getMinTimeBank()) {
                    staffUnitPositionIterator.remove();
                }
                if(Optional.ofNullable(priorityGroupDTO.getStaffExcludeFilter().getMaxDeltaWeeklyTimeBankPerWeek()).isPresent()&&
                        staffUnitPositionQueryResult.getDeltaWeeklytimeBank()>priorityGroupDTO.getStaffExcludeFilter().getMinTimeBank()) {
                    staffUnitPositionIterator.remove();
                }


            }
        }

    }




}
