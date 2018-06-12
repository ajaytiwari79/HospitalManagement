package com.kairos.activity.service.priority_group.priority_group_rules;

import com.kairos.activity.persistence.model.activity.Shift;
import com.kairos.activity.persistence.model.open_shift.OpenShift;
import com.kairos.activity.util.DateTimeInterval;
import com.kairos.activity.util.DateUtils;
import com.kairos.response.dto.web.StaffUnitPositionQueryResult;
import com.kairos.response.dto.web.open_shift.priority_group.PriorityGroupDTO;

import java.math.BigInteger;
import java.util.*;

public class NegativeAvailabilityAndPercentAvailabilityRule implements PriorityGroupRuleFilter{

    private Set<BigInteger> unavailableActivitySet;
    private List<Shift> shifts;
    private Map<BigInteger,OpenShift> openShiftMap;

    public NegativeAvailabilityAndPercentAvailabilityRule() {

    }
    public NegativeAvailabilityAndPercentAvailabilityRule(Set<BigInteger> unavailableActivitySet, List<Shift> shifts, Map<BigInteger,OpenShift> openShiftMap) {

        this.unavailableActivitySet = unavailableActivitySet;
        this.shifts = shifts;
        this.openShiftMap = openShiftMap;
    }
    @Override
    public void filter(Map<BigInteger, List<StaffUnitPositionQueryResult>> openShiftStaffMap, PriorityGroupDTO priorityGroupDTO) {

        for (Map.Entry<BigInteger, List<StaffUnitPositionQueryResult>> entry : openShiftStaffMap.entrySet()) {
            Iterator<StaffUnitPositionQueryResult> staffUnitPositionIterator = entry.getValue().iterator();
            Date filterEndDate = DateUtils.asDateEndOfDay(openShiftMap.get(entry.getKey()).getStartDate());
            Date filterStartDate = DateUtils.asDate(openShiftMap.get(entry.getKey()).getStartDate());
            Date flterStartDatePerAvailability = DateUtils.asDate(openShiftMap.get(entry.getKey()).getStartDate(), openShiftMap.get(entry.getKey()).getFromTime());
            Date filterEndDatePerAvailability = DateUtils.asDate(openShiftMap.get(entry.getKey()).getStartDate(), openShiftMap.get(entry.getKey()).getToTime());
            DateTimeInterval dateTimeInterval = new DateTimeInterval(filterStartDate.getTime(), filterEndDate.getTime());
            DateTimeInterval dateTimeIntervalPerAvailability = new DateTimeInterval(flterStartDatePerAvailability.getTime(), filterEndDatePerAvailability.getTime());

            /* Map<Long,List<Shift>> shiftsUnitPositionMapForOpenShift = shiftUnitPositionsMap.values().stream().flatMap(shifts -> shifts.stream().
                    filter(shift ->dateTimeInterval.overlaps(shift.getInterval()))).collect(groupingBy(Shift::getUnitPositionId));*/

            Set<Long> unitPositionIds = new HashSet<Long>();
            for (Shift shift : shifts) {
                if (priorityGroupDTO.getStaffExcludeFilter().isNegativeAvailabilityInCalender() && dateTimeInterval.overlaps(shift.getInterval())
                        && unavailableActivitySet.contains(shift.getActivityId()) && !unitPositionIds.contains(shift.getUnitPositionId())) {
                    unitPositionIds.add(shift.getUnitPositionId());
                }
                if (Optional.ofNullable(priorityGroupDTO.getStaffIncludeFilter().getStaffAvailability()).isPresent() && dateTimeIntervalPerAvailability.
                        overlaps(shift.getInterval()) && (((dateTimeIntervalPerAvailability.overlap(shift.getInterval()).getMinutes()) /
                        (dateTimeIntervalPerAvailability.getMinutes())) * 100) < priorityGroupDTO.getStaffIncludeFilter().getStaffAvailability() &&
                        !unitPositionIds.contains(shift.getUnitPositionId())) {
                    unitPositionIds.add(shift.getUnitPositionId());

                }
            }
            removeStaffFromList(staffUnitPositionIterator, unitPositionIds);

        }
    }
    private void removeStaffFromList(Iterator<StaffUnitPositionQueryResult> staffUnitPositionIterator,Set<Long> unitPositionIds) {

        while(staffUnitPositionIterator.hasNext()) {
            StaffUnitPositionQueryResult staffUnitPositionQueryResult = staffUnitPositionIterator.next();
            if(unitPositionIds.contains(staffUnitPositionQueryResult.getUnitPositionId())) {
                staffUnitPositionIterator.remove();
            }
        }
    }
}
