package com.kairos.activity.counter.distribution.tab;

import java.math.BigInteger;
import java.util.List;

public class TabKPIMappingDTO {
    private String tabId;
    private List<BigInteger> kpiIds;

    public String getTabId() {
        return tabId;
    }

    public void setTabId(String tabId) {
        this.tabId = tabId;
    }

    public List<BigInteger> getKpiIds() {
        return kpiIds;
    }

    public void setKpiIds(List<BigInteger> kpiIds) {
        this.kpiIds = kpiIds;
    }
}
