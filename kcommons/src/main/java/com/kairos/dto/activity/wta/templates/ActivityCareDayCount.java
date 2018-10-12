package com.kairos.dto.activity.wta.templates;

import java.math.BigInteger;

/**
 * @author pradeep
 * @date - 10/10/18
 */

public class ActivityCareDayCount {
    private BigInteger activityId;
    private int count;

    public BigInteger getActivityId() {
        return activityId;
    }

    public void setActivityId(BigInteger activityId) {
        this.activityId = activityId;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }
}
