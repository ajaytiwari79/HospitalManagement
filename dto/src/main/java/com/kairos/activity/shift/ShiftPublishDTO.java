package com.kairos.activity.shift;

import com.kairos.enums.shift.ShiftStatus;

import java.math.BigInteger;
import java.util.List;

/**
 * Created by vipul on 9/5/18.
 */
public class ShiftPublishDTO {
    List<BigInteger> shiftIds;
    List<ShiftStatus> status;


    public ShiftPublishDTO() {
        //default
    }

    public ShiftPublishDTO(List<BigInteger> shiftIds, List<ShiftStatus> status) {
        this.shiftIds = shiftIds;
        this.status = status;
    }

    public List<BigInteger> getShiftIds() {
        return shiftIds;
    }

    public void setShiftIds(List<BigInteger> shiftIds) {
        this.shiftIds = shiftIds;
    }

    public List<ShiftStatus> getStatus() {
        return status;
    }

    public void setStatus(List<ShiftStatus> status) {
        this.status = status;
    }
}
