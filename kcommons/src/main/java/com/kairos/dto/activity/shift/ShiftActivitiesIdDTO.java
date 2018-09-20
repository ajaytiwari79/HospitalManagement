package com.kairos.dto.activity.shift;

import java.math.BigInteger;
import java.util.List;

/**
 * @author pradeep
 * @date - 19/9/18
 */

public class ShiftActivitiesIdDTO {

    private BigInteger shiftId;
    private List<BigInteger> activitieIds;

    public BigInteger getShiftId() {
        return shiftId;
    }

    public void setShiftId(BigInteger shiftId) {
        this.shiftId = shiftId;
    }

    public List<BigInteger> getActivitieIds() {
        return activitieIds;
    }

    public void setActivitieIds(List<BigInteger> activitieIds) {
        this.activitieIds = activitieIds;
    }
}
