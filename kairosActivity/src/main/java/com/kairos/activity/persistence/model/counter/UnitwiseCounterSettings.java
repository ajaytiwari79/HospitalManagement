package com.kairos.activity.persistence.model.counter;

import java.math.BigInteger;

public class UnitwiseCounterSettings extends DefaultCounterSettings {
    protected BigInteger unitId;
    protected BigInteger refCounterId;

    public BigInteger getUnitId() {
        return unitId;
    }

    public void setUnitId(BigInteger unitId) {
        this.unitId = unitId;
    }

    public BigInteger getRefCounterId() {
        return refCounterId;
    }

    public void setRefCounterId(BigInteger refCounterId) {
        this.refCounterId = refCounterId;
    }
}
