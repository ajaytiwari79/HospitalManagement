package com.kairos.shiftplanningNewVersion.move.helper;


import com.kairos.shiftplanningNewVersion.entity.ALI;
import com.kairos.shiftplanningNewVersion.entity.Shift;

public  class ALIWrapper {
    private ALI activityLineInterval;
    private Shift shiftImp;

    public ALIWrapper(ALI activityLineInterval, Shift shiftImp) {
        this.activityLineInterval = activityLineInterval;
        this.shiftImp = shiftImp;
    }

    public ALI getActivityLineInterval() {
        return activityLineInterval;
    }

    public Shift getShiftImp() {
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
