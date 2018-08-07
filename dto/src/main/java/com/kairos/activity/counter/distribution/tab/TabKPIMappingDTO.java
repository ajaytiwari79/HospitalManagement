package com.kairos.activity.counter.distribution.tab;

import java.math.BigInteger;
import java.util.List;

public class TabKPIMappingDTO {
    private String tabId;
    private BigInteger kpiId;

    public TabKPIMappingDTO() {
    }

    public TabKPIMappingDTO(String tabId, BigInteger kpiId) {
        this.tabId = tabId;
        this.kpiId = kpiId;
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
