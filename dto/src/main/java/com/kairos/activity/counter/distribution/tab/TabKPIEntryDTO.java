package com.kairos.activity.counter.distribution.tab;

import java.util.List;

public class TabKPIEntryDTO {
    private String tabId;
    private List<TabKPIMappingDTO> kpiEntries;


    public String getTabId() {
        return tabId;
    }

    public void setTabId(String tabId) {
        this.tabId = tabId;
    }

    public List<TabKPIMappingDTO> getKpiEntries() {
        return kpiEntries;
    }

    public void setKpiEntries(List<TabKPIMappingDTO> kpiEntries) {
        this.kpiEntries = kpiEntries;
    }
}
