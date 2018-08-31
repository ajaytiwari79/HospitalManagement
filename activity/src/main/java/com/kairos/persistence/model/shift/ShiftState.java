package com.kairos.persistence.model.shift;

import com.kairos.persistence.model.activity.Shift;
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
