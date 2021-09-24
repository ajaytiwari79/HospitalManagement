package com.kairos.dto.activity.activity.activity_tabs;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.math.BigInteger;

/**
 * Created by vipul on 23/8/17.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class CompositeShiftActivityDTO {

    private BigInteger activityId;
    private boolean allowedBefore;
    private boolean allowedAfter;

    public CompositeShiftActivityDTO() {
        // DC
    }

    public BigInteger getActivityId() {
        return this.activityId;
    }

    public void setActivityId(BigInteger activityId) {
        this.activityId = activityId;
    }

    public boolean isAllowedBefore() {
        return allowedBefore;
    }

    public void setAllowedBefore(boolean allowedBefore) {
        this.allowedBefore = allowedBefore;
    }

    public boolean isAllowedAfter() {
        return allowedAfter;
    }

    public void setAllowedAfter(boolean allowedAfter) {
        this.allowedAfter = allowedAfter;
    }
}
