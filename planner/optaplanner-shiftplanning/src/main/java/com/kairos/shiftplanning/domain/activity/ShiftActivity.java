package com.kairos.shiftplanning.domain.activity;

import com.kairos.commons.utils.DateTimeInterval;
import com.kairos.dto.activity.shift.PlannedTime;
import com.kairos.shiftplanning.utils.ShiftPlanningUtility;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.joda.time.DateTime;
import org.joda.time.Interval;

import java.time.ZonedDateTime;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ShiftActivity {
    private ZonedDateTime startTime;
    private Activity activity;
    private ZonedDateTime endTime;
    private List<PlannedTime> plannedTimes;

    public DateTimeInterval getInterval() {
        return new DateTimeInterval(this.startTime,this.endTime);
    }

    @Override
    public String toString() {
        return activity.getName() + "-" + getIntervalAsString();
    }

    public String getIntervalAsString() {
        return ShiftPlanningUtility.getIntervalAsString(getInterval());
    }
}
