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
    List<ShiftState> shiftStates;


    public ShiftPublishDTO() {
        //default
    }



    public List<BigInteger> getShiftIds() {
        return shiftIds;
    }

    public void setShiftIds(List<BigInteger> shiftIds) {
        this.shiftIds = shiftIds;
    }

    public List<ShiftState> getShiftStates() {
        return shiftStates;
    }

    public void setShiftStates(List<ShiftState> shiftStates) {
        this.shiftStates = shiftStates;
    }
}
