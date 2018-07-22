package com.kairos.persistence.model.activity.tabs;

import java.io.Serializable;
import java.math.BigInteger;

/**
 * Created by vipul on 23/8/17.
 */

public class CompositeActivity implements Serializable {

    private BigInteger activityId;
    private boolean allowedBefore;
    private boolean allowedAfter;

    public CompositeActivity() {
        // DC
    }

    public CompositeActivity(BigInteger activityId, boolean allowedBefore, boolean allowedAfter) {
        this.activityId = activityId;
        this.allowedBefore = allowedBefore;
        this.allowedAfter = allowedAfter;
    }

    public BigInteger getActivityId() {
        return activityId;
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
