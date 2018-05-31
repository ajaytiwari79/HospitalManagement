package com.kairos.activity.shift;

import com.kairos.enums.shift.ShiftState;

import javax.validation.constraints.NotNull;
import java.math.BigInteger;
import java.util.List;

/**
 * Created by vipul on 9/5/18.
 */
public class ShiftPublishDTO {
    List<BigInteger> shiftIds;
    @NotNull
    ShiftState shiftState;

    public ShiftPublishDTO() {
        //default
    }

    public ShiftPublishDTO(List<BigInteger> shiftIds, @NotNull ShiftState shiftState) {
        this.shiftIds = shiftIds;
        this.shiftState = shiftState;
    }

    public List<BigInteger> getShiftIds() {
        return shiftIds;
    }

    public void setShiftIds(List<BigInteger> shiftIds) {
        this.shiftIds = shiftIds;
    }

    public ShiftState getShiftState() {
        return shiftState;
    }

    public void setShiftState(ShiftState shiftState) {
        this.shiftState = shiftState;
    }
}
