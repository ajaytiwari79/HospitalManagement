package com.kairos.persistence.model.counter;

import com.kairos.persistence.model.common.MongoBaseEntity;

import java.math.BigInteger;

public class TabKPIEntry extends MongoBaseEntity {
    private String tabId;
    private BigInteger kpiId;

    public TabKPIEntry(String tabId, BigInteger kpiId) {
        this.kpiId = kpiId;
        this.tabId = tabId;
    }

    public String getTabId() {
        return tabId;
    }

    public void setTabId(String tabId) {
        this.tabId = tabId;
    }

    public BigInteger getKpiId() {
        return kpiId;
    }

    public void setKpiId(BigInteger kpiId) {
        this.kpiId = kpiId;
    }
}
