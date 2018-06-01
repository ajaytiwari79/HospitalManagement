package com.kairos.activity.persistence.model.kpi;

import com.kairos.activity.persistence.enums.counter.CounterType;
import com.kairos.activity.persistence.model.common.MongoBaseEntity;

import java.math.BigInteger;

public class ModulewiseKpiSettings extends MongoBaseEntity {
    private BigInteger countryId;
    protected String tabId;
    protected CounterType type;
    protected long order;

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

    public long getOrder() {
        return order;
    }

    public void setOrder(long order) {
        this.order = order;
    }
}
