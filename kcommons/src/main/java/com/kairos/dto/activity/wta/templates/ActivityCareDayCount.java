package com.kairos.dto.activity.wta.templates;

import java.math.BigInteger;
import java.util.Objects;

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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ActivityCareDayCount)) return false;
        ActivityCareDayCount that = (ActivityCareDayCount) o;
        return count == that.count &&
                Objects.equals(activityId, that.activityId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(activityId, count);
    }
}
