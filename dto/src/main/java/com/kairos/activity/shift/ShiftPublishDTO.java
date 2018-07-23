package com.kairos.activity.shift;

import com.kairos.enums.shift.ShiftState;

import javax.validation.constraints.NotNull;
import java.math.BigInteger;
import java.util.List;
import java.util.Set;

/**
 * Created by vipul on 9/5/18.
 */
public class ShiftPublishDTO {
    List<BigInteger> shiftIds;
    @NotNull
    ShiftState shiftState;
    Set<ShiftState> shiftStates;


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

    public Set<ShiftState> getShiftStates() {
        return shiftStates;
    }

    public void setShiftStates(Set<ShiftState> shiftStates) {
        this.shiftStates = shiftStates;
    }
}
