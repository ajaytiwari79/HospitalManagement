package com.kairos.persistence.model.shift;

import org.springframework.data.mongodb.core.mapping.Document;

import java.math.BigInteger;

@Document(collection = "shiftState")
public class ShiftState extends Shift {

    private BigInteger shiftId;

    public BigInteger getShiftId() {
        return shiftId;
    }

    public void setShiftId(BigInteger shiftId) {
        this.shiftId = shiftId;
    }
}
