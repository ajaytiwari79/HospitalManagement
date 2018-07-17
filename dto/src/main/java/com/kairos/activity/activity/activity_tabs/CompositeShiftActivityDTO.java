package com.kairos.activity.activity.activity_tabs;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.math.BigInteger;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

/**
 * Created by vipul on 23/8/17.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class CompositeShiftActivityDTO {

    private BigInteger activityId;
    private boolean restrictedBefore;
    private boolean restrictedAfter;

    public CompositeShiftActivityDTO() {
        // DC
    }

    public BigInteger getActivityId() {
        return this.activityId;
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
