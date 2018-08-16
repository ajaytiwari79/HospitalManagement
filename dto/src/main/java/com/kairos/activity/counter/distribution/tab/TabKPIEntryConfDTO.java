package com.kairos.activity.counter.distribution.tab;

import java.math.BigInteger;
import java.util.List;

public class TabKPIEntryConfDTO {
    private List<String> tabIds;
    private List<BigInteger> kpiIds;

    public TabKPIEntryConfDTO(){

    }

    public TabKPIEntryConfDTO(List tabIds, List kpiIds){
        this.kpiIds = kpiIds;
        this.tabIds = tabIds;
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
