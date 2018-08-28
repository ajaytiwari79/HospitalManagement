package com.kairos.activity.counter.distribution.tab;

import com.kairos.activity.counter.KPIDTO;

public class TabKPIDTO {
    private String tabId;
    private KPIDTO kpis;
    private String data;
    private KPIPosition kpiPosition;

    public String getTabId() {
        return tabId;
    }

    public void setTabId(String tabId) {
        this.tabId = tabId;
    }

    public KPIDTO getKpis() {
        return kpis;
    }

    public void setKpis(KPIDTO kpis) {
        this.kpis = kpis;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    public KPIPosition getKpiPosition() {
        return kpiPosition;
    }

    public void setKpiPosition(KPIPosition kpiPosition) {
        this.kpiPosition = kpiPosition;
    }
}
