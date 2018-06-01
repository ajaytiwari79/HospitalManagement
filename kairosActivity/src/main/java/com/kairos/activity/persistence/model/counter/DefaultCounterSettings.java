package com.kairos.activity.persistence.model.counter;

import com.kairos.activity.persistence.enums.counter.CounterType;
import com.kairos.activity.persistence.model.common.MongoBaseEntity;

import java.math.BigInteger;

public class DefaultCounterSettings extends MongoBaseEntity {
    private BigInteger countryId;
    protected String tabId;
    protected CounterType type;

    public BigInteger getCountryId() {
        return countryId;
    }

    public void setCountryId(BigInteger countryId) {
        this.countryId = countryId;
    }

    public String getTabId() {
        return tabId;
    }

    public void setTabId(String tabId) {
        this.tabId = tabId;
    }

    public CounterType getType() {
        return type;
    }

    public void setType(CounterType type) {
        this.type = type;
    }
}
