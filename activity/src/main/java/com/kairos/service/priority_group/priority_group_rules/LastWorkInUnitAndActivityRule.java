package com.kairos.service.priority_group.priority_group_rules;

import com.kairos.activity.open_shift.priority_group.PriorityGroupDTO;
import com.kairos.persistence.model.activity.Shift;
import com.kairos.persistence.model.open_shift.OpenShift;
import com.kairos.user.staff.unit_position.StaffUnitPositionQueryResult;
import com.kairos.util.DateTimeInterval;
import com.kairos.util.DateUtils;

import java.math.BigInteger;
import java.time.LocalDate;
import java.util.*;

public class LastWorkInUnitAndActivityRule implements PriorityGroupRuleFilter{

    private Map<Long,List<Shift>> shiftUnitPositionsMap;
    private Map<BigInteger, OpenShift> openShiftMap;

    public LastWorkInUnitAndActivityRule(Map<Long,List<Shift>> shiftUnitPositionsMap,Map<BigInteger, OpenShift> openShiftMap) {
        this.shiftUnitPositionsMap = shiftUnitPositionsMap;
        this.openShiftMap = openShiftMap;
    }
    @Override
    public void filter(Map<BigInteger, List<StaffUnitPositionQueryResult>> openShiftStaffMap, PriorityGroupDTO priorityGroupDTO) {

        int lastWorkingDaysWithUnit = priorityGroupDTO.getStaffExcludeFilter().getLastWorkingDaysInUnit();
        for (Map.Entry<BigInteger, List<StaffUnitPositionQueryResult>> entry : openShiftStaffMap.entrySet()) {
            BigInteger activityId = null;

            Iterator<StaffUnitPositionQueryResult> staffUnitPositionIterator = entry.getValue().iterator();
            LocalDate openShiftDate = DateUtils.asLocalDate(openShiftMap.get(entry.getKey()).getStartDate());
            Date filterStartDate = DateUtils.getDateFromLocalDate(openShiftDate.minusDays(lastWorkingDaysWithUnit));
            Date filterEndDate = DateUtils.getDateFromLocalDate(openShiftDate);
            DateTimeInterval dateTimeInterval = new DateTimeInterval(filterStartDate.getTime(),filterEndDate.getTime());
            if(Optional.ofNullable(priorityGroupDTO.getStaffExcludeFilter().getLastWorkingDaysInUnit()).isPresent()) {
                activityId = null;
            }
            else if(Optional.ofNullable(priorityGroupDTO.getStaffExcludeFilter().getLastWorkingDaysWithActivity()).isPresent()) {
                activityId = openShiftMap.get(entry.getKey()).getActivityId();
            }
            removeStaffFromList(staffUnitPositionIterator,dateTimeInterval,activityId);
        }
    }

    private void removeStaffFromList(Iterator<StaffUnitPositionQueryResult> staffUnitPositionIterator, DateTimeInterval dateTimeInterval, BigInteger activityId) {
        int shiftCount = 0;
        while(staffUnitPositionIterator.hasNext()) {
            StaffUnitPositionQueryResult staffUnitPositionQueryResult = staffUnitPositionIterator.next();
            List<Shift> shifts = shiftUnitPositionsMap.get(staffUnitPositionQueryResult.getUnitPositionId());
            shiftCount = 0;
            if(Optional.ofNullable(shifts).isPresent()&&!shifts.isEmpty()) {
                for (Shift shift : shifts) {
                    if (dateTimeInterval.overlaps(shift.getInterval())) {
                        if (Optional.ofNullable(activityId).isPresent() && shift.getActivityId().equals(activityId)) {
                            shiftCount++;
                            break;
                        } else if (!Optional.ofNullable(activityId).isPresent()) {
                            shiftCount++;
                            break;
                        }

                    }
                }
            }
            if(shiftCount==0){
                staffUnitPositionIterator.remove();

            }
        }
    }
}
