package com.kairos.persistence.model.activity.tabs;

import java.io.Serializable;
import java.math.BigInteger;
import java.util.List;
import java.util.Set;

/**
 * Created by vipul on 23/8/17.
 */

public class CompositeShiftActivityTab implements Serializable {
    private Set<BigInteger> activityTypeList;

    public CompositeShiftActivityTab(Set<BigInteger> activityTypeList) {
        this.activityTypeList = activityTypeList;
    }

    public Set<BigInteger> getActivityList() {
        return activityTypeList;
    }

    public void setActivityTypeList(Set<BigInteger> activityTypeList) {
        this.activityTypeList = activityTypeList;
    }
}
