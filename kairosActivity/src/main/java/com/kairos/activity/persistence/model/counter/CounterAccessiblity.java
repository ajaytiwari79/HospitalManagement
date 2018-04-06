package com.kairos.activity.persistence.model.counter;

import com.kairos.activity.persistence.enums.counter.CounterLevel;
import com.kairos.activity.persistence.model.common.MongoBaseEntity;

import java.math.BigInteger;

public class CounterAccessiblity extends MongoBaseEntity{
    private BigInteger unitId;
    private CounterLevel accessLevel;
    private BigInteger counterModuleLinkId;

    public BigInteger getUnitId() {
        return unitId;
    }

    public void setUnitId(BigInteger unitId) {
        this.unitId = unitId;
    }

    public CounterLevel getAccessLevel() {
        return accessLevel;
    }

    public void setAccessLevel(CounterLevel accessLevel) {
        this.accessLevel = accessLevel;
    }

    public BigInteger getCounterModuleLinkId() {
        return counterModuleLinkId;
    }

    public void setCounterModuleLinkId(BigInteger counterModuleLinkId) {
        this.counterModuleLinkId = counterModuleLinkId;
    }
}
