package com.kairos.persistence.model.shift;

import org.springframework.data.mongodb.core.mapping.Document;

import java.math.BigInteger;

@Document(collection = "shiftState")
public class ShiftState extends Shift {

    private BigInteger shiftId;
    private Shift previousShiftState;

    public Shift getPreviousShiftState() {
        return previousShiftState;
    }

    public void setPreviousShiftState(Shift previousShiftState) {
        this.previousShiftState = previousShiftState;
    }

    public BigInteger getShiftId() {
        return shiftId;
    }

    public void setShiftId(BigInteger shiftId) {
        this.shiftId = shiftId;
    }
}
