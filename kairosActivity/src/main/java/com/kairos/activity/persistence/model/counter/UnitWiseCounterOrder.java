package com.kairos.activity.persistence.model.counter;

import java.math.BigInteger;
import java.util.List;

public class UnitWiseCounterOrder extends DefaultCounterOrder{
    protected BigInteger unitId;

    public UnitWiseCounterOrder(){

    }

    public UnitWiseCounterOrder(BigInteger unitId, String moduleId, String tabId, List<BigInteger> orderedCounterIds){
        this.unitId = unitId;
        this.moduleId = moduleId;
        this.tabId = tabId;
        this.orderedCounterIds = orderedCounterIds;
    }

    public BigInteger getUnitId() {
        return unitId;
    }

    public void setUnitId(BigInteger unitId) {
        this.unitId = unitId;
    }
}
