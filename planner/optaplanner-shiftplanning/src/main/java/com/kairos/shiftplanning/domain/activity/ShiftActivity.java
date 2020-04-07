package com.kairos.shiftplanning.domain.activity;

import com.kairos.commons.utils.DateTimeInterval;
import com.kairos.shiftplanning.domain.shift.PlannedTime;
import com.kairos.shiftplanning.utils.ShiftPlanningUtility;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.ZonedDateTime;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ShiftActivity {
    private ZonedDateTime startDate;
    private Activity activity;
    private ZonedDateTime endDate;
    private List<PlannedTime> plannedTimes;

    public DateTimeInterval getInterval() {
        return new DateTimeInterval(this.startDate,this.endDate);
    }

    @Override
    public String toString() {
        return activity.getName() + "-" + getIntervalAsString();
    }

    public String getIntervalAsString() {
        return ShiftPlanningUtility.getIntervalAsString(getInterval());
    }
}
