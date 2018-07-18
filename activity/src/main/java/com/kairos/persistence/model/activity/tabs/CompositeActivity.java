package com.kairos.persistence.model.activity.tabs;

import java.io.Serializable;
import java.math.BigInteger;
import java.util.List;
import java.util.Set;

/**
 * Created by vipul on 23/8/17.
 */

public class CompositeActivity implements Serializable {

    private BigInteger activityId;
    private boolean restrictedBefore;
    private boolean restrictedAfter;

    public CompositeActivity() {
        // DC
    }

    public CompositeActivity(BigInteger activityId, boolean restrictedBefore, boolean restrictedAfter) {
        this.activityId = activityId;
        this.restrictedBefore = restrictedBefore;
        this.restrictedAfter = restrictedAfter;
    }

    public BigInteger getActivityId() {
        return activityId;
    }

    public void setActivityId(BigInteger activityId) {
        this.activityId = activityId;
    }

    public boolean isRestrictedBefore() {
        return restrictedBefore;
    }

    public void setRestrictedBefore(boolean restrictedBefore) {
        this.restrictedBefore = restrictedBefore;
    }

    public boolean isRestrictedAfter() {
        return restrictedAfter;
    }

    public void setRestrictedAfter(boolean restrictedAfter) {
        this.restrictedAfter = restrictedAfter;
    }
}
