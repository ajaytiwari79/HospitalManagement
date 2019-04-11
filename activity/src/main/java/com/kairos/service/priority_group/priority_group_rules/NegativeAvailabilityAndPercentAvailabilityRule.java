package com.kairos.service.priority_group.priority_group_rules;

import com.kairos.dto.activity.open_shift.priority_group.PriorityGroupDTO;
import com.kairos.dto.user.staff.unit_position.StaffEmploymentQueryResult;
import com.kairos.persistence.model.shift.Shift;
import com.kairos.persistence.model.open_shift.OpenShift;
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
    public void filter(Map<BigInteger, List<StaffEmploymentQueryResult>> openShiftStaffMap, PriorityGroupDTO priorityGroupDTO) {

        for (Map.Entry<BigInteger, List<StaffEmploymentQueryResult>> entry : openShiftStaffMap.entrySet()) {
            Iterator<StaffEmploymentQueryResult> staffEmploymentIterator = entry.getValue().iterator();
            Date filterEndDate = DateUtils.getEndOfDay(openShiftMap.get(entry.getKey()).getStartDate());
            Date filterStartDate = DateUtils.getStartOfDay(openShiftMap.get(entry.getKey()).getStartDate());
            Date flterStartDatePerAvailability = openShiftMap.get(entry.getKey()).getStartDate();
            Date filterEndDatePerAvailability = openShiftMap.get(entry.getKey()).getEndDate();
            DateTimeInterval dateTimeInterval = new DateTimeInterval(filterStartDate.getTime(), filterEndDate.getTime());
            DateTimeInterval dateTimeIntervalPerAvailability = new DateTimeInterval(flterStartDatePerAvailability.getTime(), filterEndDatePerAvailability.getTime());


            Set<Long> employmentIds = new HashSet<Long>();
            for (Shift shift : shifts) {
                if (priorityGroupDTO.getStaffExcludeFilter().isNegativeAvailabilityInCalender() && dateTimeInterval.overlaps(shift.getInterval())
                        && unavailableActivitySet.contains(shift.getActivities().get(0).getActivityId()) && !employmentIds.contains(shift.getEmploymentId())) {
                    employmentIds.add(shift.getEmploymentId());
                }
                if (Optional.ofNullable(priorityGroupDTO.getStaffIncludeFilter().getStaffAvailability()).isPresent() && dateTimeIntervalPerAvailability.
                        overlaps(shift.getInterval()) && ((((int)dateTimeIntervalPerAvailability.overlap(shift.getInterval()).getMinutes()) /
                        (dateTimeIntervalPerAvailability.getMinutes())) * 100) < priorityGroupDTO.getStaffIncludeFilter().getStaffAvailability() &&
                        !employmentIds.contains(shift.getEmploymentId())) {
                    employmentIds.add(shift.getEmploymentId());

                }
            }
            removeStaffFromList(staffEmploymentIterator, employmentIds);

        }
    }
    private void removeStaffFromList(Iterator<StaffEmploymentQueryResult> staffEmploymentIterator, Set<Long> employmentIds) {

        while(staffEmploymentIterator.hasNext()) {
            StaffEmploymentQueryResult staffEmploymentQueryResult = staffEmploymentIterator.next();
            if(employmentIds.contains(staffEmploymentQueryResult.getEmploymentId())) {
                staffEmploymentIterator.remove();
            }
        }
    }
}
