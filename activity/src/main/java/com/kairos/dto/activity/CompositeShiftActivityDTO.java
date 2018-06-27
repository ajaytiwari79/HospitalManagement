package com.kairos.dto.activity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.kairos.persistence.model.activity.tabs.CompositeShiftActivityTab;

import java.math.BigInteger;
import java.util.List;
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

    public CompositeShiftActivityTab buildCompositeShiftActivityTab() {
        CompositeShiftActivityTab compositeShiftActivityTab = new CompositeShiftActivityTab(activityList);
        return compositeShiftActivityTab;
    }
}
