package com.kairos.shiftplanning.domain.staffing_level;

import com.kairos.shiftplanning.domain.activity.ActivityLineInterval;
import org.joda.time.LocalDate;

import java.util.List;

public class DailyActivityLine extends DailyLine {
    private List<ActivityLineInterval> activityLineIntervals;

    public DailyActivityLine() {
    }

    public DailyActivityLine(LocalDate date, List<ActivityLineInterval> activityLineIntervals) {
        this.date = date;
        this.activityLineIntervals = activityLineIntervals;
    }


    public List<ActivityLineInterval> getActivityLineIntervals() {
        return activityLineIntervals;
    }

    public void setActivityLineIntervals(List<ActivityLineInterval> activityLineIntervals) {
        this.activityLineIntervals = activityLineIntervals;
    }
}
