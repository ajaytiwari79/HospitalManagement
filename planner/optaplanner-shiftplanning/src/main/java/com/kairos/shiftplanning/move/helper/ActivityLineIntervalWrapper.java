package com.kairos.shiftplanning.move.helper;

import com.kairos.shiftplanning.domain.activity.ActivityLineInterval;
import com.kairos.shiftplanning.domain.shift.ShiftImp;

public  class ActivityLineIntervalWrapper{
    private ActivityLineInterval activityLineInterval;
    private ShiftImp shiftImp;

    public ActivityLineIntervalWrapper(ActivityLineInterval activityLineInterval, ShiftImp shiftImp) {
        this.activityLineInterval = activityLineInterval;
        this.shiftImp = shiftImp;
    }

    public ActivityLineInterval getActivityLineInterval() {
        return activityLineInterval;
    }

    public ShiftImp getShiftImp() {
        return shiftImp;
    }

    @Override
    public String toString() {
        return "ActivityLineIntervalWrapper{" +
                "activityLineInterval=" + activityLineInterval +
                ", shiftImp=" + shiftImp +
                '}';
    }
}
