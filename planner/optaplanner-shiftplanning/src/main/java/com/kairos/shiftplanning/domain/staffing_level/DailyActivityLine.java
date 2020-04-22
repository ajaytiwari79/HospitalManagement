package com.kairos.shiftplanning.domain.staffing_level;

import com.kairos.shiftplanning.domain.activity.ActivityLineInterval;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.util.List;
@Getter
@Setter
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class DailyActivityLine extends DailyLine {
    private List<ActivityLineInterval> activityLineIntervals;

    public DailyActivityLine(LocalDate date, List<ActivityLineInterval> activityLineIntervals) {
        this.date = date;
        this.activityLineIntervals = activityLineIntervals;
    }

}
