package com.kairos.service.priority_group.priority_group_rules;

import com.kairos.dto.activity.open_shift.priority_group.PriorityGroupDTO;
import com.kairos.persistence.model.shift.Shift;
import com.kairos.persistence.model.open_shift.OpenShift;
import com.kairos.dto.user.staff.unit_position.StaffUnitPositionQueryResult;
import com.kairos.commons.utils.DateTimeInterval;
import com.kairos.commons.utils.DateUtils;

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
            Date filterEndDate = DateUtils.getEndOfDay(openShiftMap.get(entry.getKey()).getStartDate());
            Date filterStartDate = DateUtils.getStartOfDay(openShiftMap.get(entry.getKey()).getStartDate());
            Date flterStartDatePerAvailability = openShiftMap.get(entry.getKey()).getStartDate();
            Date filterEndDatePerAvailability = openShiftMap.get(entry.getKey()).getEndDate();
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
