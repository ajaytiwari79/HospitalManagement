package com.kairos.dto.activity.shift;
/*
 *Created By Pavan on 17/9/18
 *
 */

import java.math.BigInteger;
import java.time.LocalTime;

public class ShiftTimeDetails {
    private BigInteger activityId;
    private LocalTime activityStartTime;
    private Short totalTime;
    private boolean overNightActivity;


    public ShiftTimeDetails(BigInteger activityId, LocalTime activityStartTime, Short totalTime,boolean overNightActivity) {
        this.activityId = activityId;
        this.activityStartTime = activityStartTime;
        this.totalTime = totalTime;
        this.overNightActivity=overNightActivity;
    }

    public ShiftTimeDetails() {
        //Default Constructor
    }

    public BigInteger getActivityId() {
        return activityId;
    }

    public void setActivityId(BigInteger activityId) {
        this.activityId = activityId;
    }

    public LocalTime getActivityStartTime() {
        return activityStartTime;
    }

    public void setActivityStartTime(LocalTime activityStartTime) {
        this.activityStartTime = activityStartTime;
    }

    public Short getTotalTime() {
        return totalTime;
    }

    public void setTotalTime(Short totalTime) {
        this.totalTime = totalTime;
    }

    public boolean isOverNightActivity() {
        return overNightActivity;
    }

    public void setOverNightActivity(boolean overNightActivity) {
        this.overNightActivity = overNightActivity;
    }
}
