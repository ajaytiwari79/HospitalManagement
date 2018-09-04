package com.kairos.activity.shift;

import java.math.BigInteger;

/**
 * @author pradeep
 * @date - 30/8/18
 */

public class ActivityRuleViolation {

    private BigInteger activityId;
    private String name;
    private int counter;

    public ActivityRuleViolation() {
    }

    public ActivityRuleViolation(BigInteger activityId, String name, int counter) {
        this.activityId = activityId;
        this.name = name;
        this.counter = counter;
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

    public int getCounter() {
        return counter;
    }

    public void setCounter(int counter) {
        this.counter = counter;
    }
}
