package com.kairos.persistence.model.time_bank;

import java.math.BigInteger;

/**
 * @author pradeep
 * @date - 16/8/18
 */

public class ShiftTimeBank {

    private BigInteger shiftId;
    private BigInteger activityId;
    private int minutes;


    public ShiftTimeBank(BigInteger shiftId, BigInteger activityId, int minutes) {
        this.shiftId = shiftId;
        this.activityId = activityId;
        this.minutes = minutes;
    }

    public BigInteger getShiftId() {
        return shiftId;
    }

    public void setShiftId(BigInteger shiftId) {
        this.shiftId = shiftId;
    }

    public BigInteger getActivityId() {
        return activityId;
    }

    public void setActivityId(BigInteger activityId) {
        this.activityId = activityId;
    }

    public int getMinutes() {
        return minutes;
    }

    public void setMinutes(int minutes) {
        this.minutes = minutes;
    }
}
