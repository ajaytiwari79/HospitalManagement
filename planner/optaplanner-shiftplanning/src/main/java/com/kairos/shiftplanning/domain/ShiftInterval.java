package com.kairos.shiftplanning.domain;

import org.joda.time.DateTime;

public class ShiftInterval {
    private DateTime start;
    private DateTime end;
    private Activity activity;

    public ShiftInterval(DateTime start, DateTime end, Activity activity) {
        this.start = start;
        this.end = end;
        this.activity = activity;
    }

    public DateTime getStart() {
        return start;
    }

    public void setStart(DateTime start) {
        this.start = start;
    }

    public DateTime getEnd() {
        return end;
    }

    public void setEnd(DateTime end) {
        this.end = end;
    }

    public Activity getActivityType() {
        return activity;
    }

    public void setActivityType(Activity activityType) {
        this.activity = activity;
    }
}
