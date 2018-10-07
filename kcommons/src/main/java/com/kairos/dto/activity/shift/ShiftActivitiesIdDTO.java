package com.kairos.dto.activity.shift;

import java.math.BigInteger;
import java.util.List;

/**
 * @author pradeep
 * @date - 19/9/18
 */

public class ShiftActivitiesIdDTO {

    private BigInteger shiftId;
    private List<BigInteger> activityIds;

    public BigInteger getShiftId() {
        return shiftId;
    }

    public void setShiftId(BigInteger shiftId) {
        this.shiftId = shiftId;
    }

    public List<BigInteger> getActivityIds() {
        return activityIds;
    }

    public void setActivityIds(List<BigInteger> activityIds) {
        this.activityIds = activityIds;
    }
}
