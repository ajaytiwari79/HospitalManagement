package com.kairos.shiftplanning.domain;

public  class ActivityLineIntervalWrapper{
    private ActivityLineInterval activityLineInterval;
    private ShiftRequestPhase shiftRequestPhase;

    public ActivityLineIntervalWrapper(ActivityLineInterval activityLineInterval, ShiftRequestPhase shiftRequestPhase) {
        this.activityLineInterval = activityLineInterval;
        this.shiftRequestPhase = shiftRequestPhase;
    }

    public ActivityLineInterval getActivityLineInterval() {
        return activityLineInterval;
    }

    public ShiftRequestPhase getShiftRequestPhase() {
        return shiftRequestPhase;
    }

    @Override
    public String toString() {
        return "ActivityLineIntervalWrapper{" +
                "activityLineInterval=" + activityLineInterval +
                ", shiftRequestPhase=" + shiftRequestPhase +
                '}';
    }
}
