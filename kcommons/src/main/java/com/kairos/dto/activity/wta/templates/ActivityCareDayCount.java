package com.kairos.dto.activity.wta.templates;

import lombok.Getter;
import lombok.Setter;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * @author pradeep
 * @date - 10/10/18
 */
@Getter
@Setter
public class ActivityCareDayCount {
    private BigInteger activityId;
    private int count;
    private List<ActivityCutOffCount> activityCutOffCounts=new ArrayList<>();


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ActivityCareDayCount)) return false;
        ActivityCareDayCount that = (ActivityCareDayCount) o;
        return Objects.equals(activityId, that.activityId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(activityId);
    }
}
