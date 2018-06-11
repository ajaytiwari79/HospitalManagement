package com.kairos.activity.service.priority_group.priority_group_rules;

import com.kairos.activity.persistence.model.activity.Shift;
import com.kairos.activity.persistence.model.open_shift.OpenShift;
import com.kairos.activity.util.DateTimeInterval;
import com.kairos.activity.util.DateUtils;
import com.kairos.response.dto.web.StaffUnitPositionQueryResult;
import com.kairos.response.dto.web.open_shift.priority_group.PriorityGroupDTO;

import java.math.BigInteger;
import java.util.*;

public class RestingHoursRule implements PriorityGroupRuleFilter {

    private Map<BigInteger, OpenShift> openShiftMap;
    private List<Shift> shifts;

    public RestingHoursRule(Map<BigInteger, OpenShift> openShiftMap,List<Shift> shifts) {
        this.openShiftMap = openShiftMap;
        this.shifts = shifts;
    }
    @Override
    public void filter(Map<BigInteger, List<StaffUnitPositionQueryResult>> openShiftStaffMap, PriorityGroupDTO priorityGroupDTO) {
        int restingHoursBeforeStart = priorityGroupDTO.getStaffExcludeFilter().getMinRestingTimeBeforeShiftStart();
        int restingHoursAfterEnd = priorityGroupDTO.getStaffExcludeFilter().getMinRestingTimeAfterShiftEnd();

        boolean both = false, restingBeforeStart = false, restingAfterEnd = false;
        if(Optional.ofNullable(priorityGroupDTO.getStaffExcludeFilter().getMinRestingTimeBeforeShiftStart()).isPresent()&&
                Optional.ofNullable(priorityGroupDTO.getStaffExcludeFilter().getMinRestingTimeAfterShiftEnd()).isPresent()) {
            both = true;
            restingBeforeStart= true;
            restingAfterEnd = true;
        }
        else if(Optional.ofNullable(priorityGroupDTO.getStaffExcludeFilter().getMinRestingTimeBeforeShiftStart()).isPresent()) {
            restingBeforeStart = true;
        }
        else if(Optional.ofNullable(priorityGroupDTO.getStaffExcludeFilter().getMinRestingTimeAfterShiftEnd()).isPresent()) {
            restingAfterEnd = true;
        }
        DateTimeInterval dateTimeIntervalBeforeStart = null;
        DateTimeInterval dateTimeIntervalAfterEnd = null;
        for(Map.Entry<BigInteger,List<StaffUnitPositionQueryResult>> entry: openShiftStaffMap.entrySet()) {
            Iterator<StaffUnitPositionQueryResult> staffUnitPositionIterator = entry.getValue().iterator();
            if(restingBeforeStart) {
                Date filterEndDate = DateUtils.asDate(openShiftMap.get(entry.getKey()).getStartDate(),openShiftMap.get(entry.getKey()).getFromTime());
                Date filterStartDate = DateUtils.asDate(openShiftMap.get(entry.getKey()).getStartDate(),openShiftMap.get(entry.getKey()).
                        getFromTime().minusHours(restingHoursBeforeStart));
                dateTimeIntervalBeforeStart = new DateTimeInterval(filterStartDate.getTime(),filterEndDate.getTime());
            }
           if(restingAfterEnd) {
               Date filterEndDateAfterEnd = DateUtils.asDate(openShiftMap.get(entry.getKey()).getStartDate(),openShiftMap.get(entry.getKey()).getToTime().
                       plusHours(restingHoursAfterEnd));
               Date filterStartDateAfterEnd = DateUtils.asDate(openShiftMap.get(entry.getKey()).getStartDate(),openShiftMap.get(entry.getKey()).getToTime());
               dateTimeIntervalAfterEnd = new DateTimeInterval(filterStartDateAfterEnd.getTime(),filterEndDateAfterEnd.getTime());
           }

            /*DateTimeInterval dateTimeIntervalBeforeStart = new DateTimeInterval(filterStartDate.getTime(),filterEndDate.getTime());
            DateTimeInterval dateTimeIntervalAfterEnd = new DateTimeInterval(filterStartDateAfterEnd.getTime(),filterEndDateAfterEnd.getTime());*/

            removeStaffFromListByRestingHours(staffUnitPositionIterator,dateTimeIntervalBeforeStart,dateTimeIntervalAfterEnd,both,restingBeforeStart,restingAfterEnd);
        }
    }


    private void removeStaffFromListByRestingHours(Iterator<StaffUnitPositionQueryResult> staffUnitPositionIterator,DateTimeInterval dateTimeIntervalBeforeStart,
                                                   DateTimeInterval dateTimeIntervalAfterEnd, boolean both, boolean restingBeforeStart, boolean restingAfterEnd) {

        Set<Long> unitPositionIds = new HashSet<Long>();
        for(Shift shift:shifts) {
            if((both&&dateTimeIntervalBeforeStart.overlaps(shift.getInterval())||dateTimeIntervalAfterEnd.overlaps(shift.getInterval()))||
                    (restingBeforeStart&&dateTimeIntervalBeforeStart.overlaps(shift.getInterval()))||
                    (restingAfterEnd&&dateTimeIntervalAfterEnd.overlaps(shift.getInterval()))) {
                if(!unitPositionIds.contains(shift.getUnitPositionId())) {
                    unitPositionIds.add(shift.getUnitPositionId());
                }
            }
        }
        while(staffUnitPositionIterator.hasNext()) {
            StaffUnitPositionQueryResult staffUnitPositionQueryResult = staffUnitPositionIterator.next();
            if(unitPositionIds.contains(staffUnitPositionQueryResult.getUnitPositionId())) {
                staffUnitPositionIterator.remove();
            }
        }
    }
}
