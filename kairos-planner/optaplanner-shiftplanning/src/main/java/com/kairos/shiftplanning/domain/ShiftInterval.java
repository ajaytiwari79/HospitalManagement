package com.kairos.shiftplanning.domain;

import org.joda.time.DateTime;

public class ShiftInterval {
    private DateTime start;
    private DateTime end;
    private ActivityPlannerEntity activityPlannerEntity;

    public ShiftInterval(DateTime start, DateTime end, ActivityPlannerEntity activityPlannerEntity) {
        this.start = start;
        this.end = end;
        this.activityPlannerEntity = activityPlannerEntity;
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

    public ActivityPlannerEntity getActivityType() {
        return activityPlannerEntity;
    }

    public void setActivityType(ActivityPlannerEntity activityPlannerEntityType) {
        this.activityPlannerEntity = activityPlannerEntity;
    }
}
