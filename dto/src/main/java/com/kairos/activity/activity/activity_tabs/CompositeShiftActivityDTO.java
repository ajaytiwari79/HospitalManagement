package com.kairos.activity.activity.activity_tabs;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.math.BigInteger;
import java.util.Set;

/**
 * Created by vipul on 23/8/17.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class CompositeShiftActivityDTO {

    private BigInteger activityId;
    private Set<BigInteger> activityList;

    public BigInteger getActivityId() {
        return activityId;
    }

    public void setActivityId(BigInteger activityId) {
        this.activityId = activityId;
    }

    public Set<BigInteger> getActivityList() {
        return activityList;
    }

    public void setActivityList(Set<BigInteger> activityList) {
        this.activityList = activityList;
    }

}
