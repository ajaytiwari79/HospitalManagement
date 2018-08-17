package com.kairos.activity.counter.distribution.tab;

import com.kairos.activity.enums.counter.CounterSize;

import java.math.BigInteger;
import java.util.List;

public class TabKPIEntryConfDTO {
    private List<String> tabIds;
    private List<BigInteger> kpiIds;

    public TabKPIEntryConfDTO() {

    }

    public TabKPIEntryConfDTO(List<String> tabIds, List<BigInteger> kpiIds) {
        this.tabIds = tabIds;
        this.kpiIds = kpiIds;
    }

    public List<String> getTabIds() {
        return tabIds;
    }

    public void setTabIds(List<String> tabIds) {
        this.tabIds = tabIds;
    }

    public List<BigInteger> getKpiIds() {
        return kpiIds;
    }

    public void setKpiIds(List<BigInteger> kpiIds) {
        this.kpiIds = kpiIds;
    }
}


