package com.kairos.activity.counter.distribution.tab;

import java.math.BigInteger;

public class TabKPIConfDTO {
    private String tabId;
    private BigInteger kpiId;

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
