package com.kairos.service.priority_group.priority_group_rules;

import com.kairos.activity.open_shift.priority_group.PriorityGroupDTO;
import com.kairos.persistence.model.shift.Shift;
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

        for (Map.Entry<BigInteger, List<StaffUnitPositionQueryResult>> entry : openShiftStaffMap.entrySet()) {
            BigInteger activityId = null;
            LocalDate openShiftDate = DateUtils.asLocalDate(openShiftMap.get(entry.getKey()).getStartDate());
            Date unitFilterStartDate;
            Date filterEndDate = DateUtils.getDateFromLocalDate(openShiftDate);
            Date activityFilterStartDate;
            DateTimeInterval unitDateTimeInterval = null;
            DateTimeInterval activityDateTimeInterval = null;
            if(Optional.ofNullable(priorityGroupDTO.getStaffExcludeFilter().getLastWorkingDaysInUnit()).isPresent()) {
                unitFilterStartDate = DateUtils.getDateFromLocalDate(openShiftDate.minusDays(priorityGroupDTO.getStaffExcludeFilter().getLastWorkingDaysInUnit()));
                unitDateTimeInterval = new DateTimeInterval(unitFilterStartDate.getTime(),filterEndDate.getTime());
            }
            if(Optional.ofNullable(priorityGroupDTO.getStaffExcludeFilter().getLastWorkingDaysWithActivity()).isPresent()) {
                activityFilterStartDate = DateUtils.getDateFromLocalDate(openShiftDate.minusDays(priorityGroupDTO.getStaffExcludeFilter().getLastWorkingDaysWithActivity()));
                activityDateTimeInterval = new DateTimeInterval(activityFilterStartDate.getTime(),filterEndDate.getTime());
                activityId = openShiftMap.get(entry.getKey()).getActivityId();
            }
            Iterator<StaffUnitPositionQueryResult> staffUnitPositionIterator = entry.getValue().iterator();

            removeStaffFromList(staffUnitPositionIterator,unitDateTimeInterval,activityDateTimeInterval,activityId);
        }
    }

    private void removeStaffFromList(Iterator<StaffUnitPositionQueryResult> staffUnitPositionIterator, DateTimeInterval unitDateTimeInterval,DateTimeInterval activityDateTimeInterval, BigInteger activityId) {
                while(staffUnitPositionIterator.hasNext()) {
            int shiftCountUnit = 0;
            int shiftCountActivity = 0;
            StaffUnitPositionQueryResult staffUnitPositionQueryResult = staffUnitPositionIterator.next();
            List<Shift> shifts = shiftUnitPositionsMap.get(staffUnitPositionQueryResult.getUnitPositionId());
            if(Optional.ofNullable(shifts).isPresent()&&!shifts.isEmpty()) {
                for (Shift shift : shifts) {
                    if (Optional.ofNullable(unitDateTimeInterval).isPresent()&&unitDateTimeInterval.overlaps(shift.getInterval())) {
                            shiftCountUnit++;
                            if(shiftCountActivity>0){
                                break;
                            }
                        }
                    if(Optional.ofNullable(activityDateTimeInterval).isPresent()&&shift.getActivityId().equals(activityId)&&(activityDateTimeInterval.overlaps(shift.getInterval()))) {
                            shiftCountActivity++;
                        if(shiftCountUnit>0){
                            break;
                        }
                        }
                    }
                }
            if(Optional.ofNullable(unitDateTimeInterval).isPresent()&&shiftCountUnit==0||(Optional.ofNullable(activityDateTimeInterval).isPresent()&&shiftCountActivity==0)){
                staffUnitPositionIterator.remove();

            }
                }
        }
    }

