package com.kairos.activity.response.dto.counter;

import com.kairos.activity.persistence.enums.counter.CounterLevel;
import com.kairos.activity.persistence.model.counter.CounterModuleLink;

import java.math.BigInteger;

public class CounterAccessiblityDTO {
    private BigInteger id;
    private BigInteger unitId;
    private CounterLevel accessLevel;
    private CounterModuleLink counterModule;

    public BigInteger getId() {
        return id;
    }

    public void setId(BigInteger id) {
        this.id = id;
    }

    public CounterModuleLink getCounterModule() {
        return counterModule;
    }

    public void setCounterModule(CounterModuleLink counterModule) {
        this.counterModule = counterModule;
    }

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
}
