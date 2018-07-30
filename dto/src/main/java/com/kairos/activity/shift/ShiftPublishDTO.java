package com.kairos.activity.shift;

import com.kairos.enums.shift.ShiftStatus;

import java.math.BigInteger;
import java.util.List;
import java.util.Set;

/**
 * Created by vipul on 9/5/18.
 */
public class ShiftPublishDTO {
    List<BigInteger> shiftIds;
    List<ShiftStatus> status;


    public ShiftPublishDTO() {
        //default
    }

    public <T> ShiftPublishDTO(List<BigInteger> shifts, Set<T> singleton) {
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
