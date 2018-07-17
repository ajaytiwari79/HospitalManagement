package com.kairos.persistence.model.activity.tabs;

import java.io.Serializable;
import java.math.BigInteger;
import java.util.List;
import java.util.Set;

/**
 * Created by vipul on 23/8/17.
 */

public class CompositeShiftActivityTab implements Serializable {

    private BigInteger activityId;
    private boolean canBeUsedBefore;
    private boolean canBeUsedAfter;

    public CompositeShiftActivityTab() {
        // DC
    }

    public BigInteger getActivityId() {
        return activityId;
    }

    public void setActivityId(BigInteger activityId) {
        this.activityId = activityId;
    }

    public boolean isCanBeUsedBefore() {
        return canBeUsedBefore;
    }

    public void setCanBeUsedBefore(boolean canBeUsedBefore) {
        this.canBeUsedBefore = canBeUsedBefore;
    }

    public boolean isCanBeUsedAfter() {
        return canBeUsedAfter;
    }

    public void setCanBeUsedAfter(boolean canBeUsedAfter) {
        this.canBeUsedAfter = canBeUsedAfter;
    }
}
