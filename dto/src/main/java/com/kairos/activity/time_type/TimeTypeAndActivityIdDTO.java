package com.kairos.activity.time_type;

import java.math.BigInteger;

public class TimeTypeAndActivityIdDTO {
    private BigInteger activityId;
    private String timeType;

    public BigInteger getActivityId() {
        return activityId;
    }

    public void setActivityId(BigInteger activityId) {
        this.activityId = activityId;
    }

    public String getTimeType() {
        return timeType;
    }

    public void setTimeType(String timeType) {
        this.timeType = timeType;
    }
}
