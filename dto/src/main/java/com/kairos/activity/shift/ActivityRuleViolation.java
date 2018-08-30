package com.kairos.activity.shift;

import java.math.BigInteger;

/**
 * @author pradeep
 * @date - 30/8/18
 */

public class ActivityRuleViolation {

    private BigInteger activityId;
    private String name;
    private boolean canBeIgnore;

    public ActivityRuleViolation() {
    }

    public ActivityRuleViolation(BigInteger activityId, String name, boolean canBeIgnore) {
        this.activityId = activityId;
        this.name = name;
        this.canBeIgnore = canBeIgnore;
    }

    public BigInteger getActivityId() {
        return activityId;
    }

    public void setActivityId(BigInteger activityId) {
        this.activityId = activityId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean isCanBeIgnore() {
        return canBeIgnore;
    }

    public void setCanBeIgnore(boolean canBeIgnore) {
        this.canBeIgnore = canBeIgnore;
    }
}
