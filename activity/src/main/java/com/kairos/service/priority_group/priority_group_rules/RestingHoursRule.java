package com.kairos.service.priority_group.priority_group_rules;

import com.kairos.dto.activity.open_shift.priority_group.PriorityGroupDTO;
import com.kairos.persistence.model.shift.Shift;
import com.kairos.persistence.model.open_shift.OpenShift;
import com.kairos.dto.user.staff.unit_position.StaffUnitPositionQueryResult;
import com.kairos.commons.utils.DateTimeInterval;
import com.kairos.commons.utils.DateUtils;

import java.math.BigInteger;
import java.time.LocalDate;
import java.time.LocalTime;
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
                LocalTime openShiftStartTime = DateUtils.asLocalTime(openShiftMap.get(entry.getKey()).getStartDate());
                LocalDate openshiftDate = DateUtils.asLocalDate(openShiftMap.get(entry.getKey()).getStartDate());
                Date filterEndDate = openShiftMap.get(entry.getKey()).getStartDate();
                Date filterStartDate = DateUtils.asDate(openshiftDate,openShiftStartTime.minusHours(restingHoursBeforeStart));
                dateTimeIntervalBeforeStart = new DateTimeInterval(filterStartDate.getTime(),filterEndDate.getTime());
            }
           if(restingAfterEnd) {
               LocalTime openShiftEndTime = DateUtils.asLocalTime(openShiftMap.get(entry.getKey()).getEndDate());
               LocalDate openshiftDate = DateUtils.asLocalDate(openShiftMap.get(entry.getKey()).getEndDate());
               Date filterEndDateAfterEnd = DateUtils.asDate(openshiftDate,openShiftEndTime.plusHours(restingHoursAfterEnd));
               Date filterStartDateAfterEnd = openShiftMap.get(entry.getKey()).getEndDate();
               dateTimeIntervalAfterEnd = new DateTimeInterval(filterStartDateAfterEnd.getTime(),filterEndDateAfterEnd.getTime());
           }

            /*DateTimeInterval dateTimeIntervalBeforeStart = new DateTimeInterval(filterStartDate.getTime(),filterEndDate.getTime());
            DateTimeInterval dateTimeIntervalAfterEnd = new DateTimeInterval(filterStartDateAfterEnd.getTime(),filterEndDateAfterEnd.getTime());*/

            removeStaffFromListByRestingHours(staffUnitPositionIterator,dateTimeIntervalBeforeStart,dateTimeIntervalAfterEnd,both,restingBeforeStart,restingAfterEnd);
        }
    }


    private void removeStaffFromListByRestingHours(Iterator<StaffUnitPositionQueryResult> staffUnitPositionIterator, DateTimeInterval dateTimeIntervalBeforeStart,
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
