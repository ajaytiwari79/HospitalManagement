package com.kairos.service.priority_group.priority_group_rules;

import com.kairos.dto.activity.open_shift.priority_group.PriorityGroupDTO;
import com.kairos.persistence.model.shift.Shift;
import com.kairos.persistence.model.open_shift.OpenShift;
import com.kairos.dto.user.staff.unit_position.StaffEmploymentQueryResult;
import com.kairos.commons.utils.DateTimeInterval;
import com.kairos.commons.utils.DateUtils;

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
    public void filter(Map<BigInteger, List<StaffEmploymentQueryResult>> openShiftStaffMap, PriorityGroupDTO priorityGroupDTO) {

        for (Map.Entry<BigInteger, List<StaffEmploymentQueryResult>> entry : openShiftStaffMap.entrySet()) {
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
            Iterator<StaffEmploymentQueryResult> staffUnitPositionIterator = entry.getValue().iterator();

            removeStaffFromList(staffUnitPositionIterator,unitDateTimeInterval,activityDateTimeInterval,activityId);
        }
    }

    private void removeStaffFromList(Iterator<StaffEmploymentQueryResult> staffUnitPositionIterator, DateTimeInterval unitDateTimeInterval, DateTimeInterval activityDateTimeInterval, BigInteger activityId) {
                while(staffUnitPositionIterator.hasNext()) {
            int shiftCountUnit = 0;
            int shiftCountActivity = 0;
            StaffEmploymentQueryResult staffEmploymentQueryResult = staffUnitPositionIterator.next();
            List<Shift> shifts = shiftUnitPositionsMap.get(staffEmploymentQueryResult.getUnitPositionId());
            if(Optional.ofNullable(shifts).isPresent()&&!shifts.isEmpty()) {
                for (Shift shift : shifts) {
                    if (Optional.ofNullable(unitDateTimeInterval).isPresent()&&unitDateTimeInterval.overlaps(shift.getInterval())) {
                            shiftCountUnit++;
                            if(shiftCountActivity>0){
                                break;
                            }
                        }
                    if(Optional.ofNullable(activityDateTimeInterval).isPresent()&&shift.getActivities().get(0).getActivityId().equals(activityId)&&(activityDateTimeInterval.overlaps(shift.getInterval()))) {
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

