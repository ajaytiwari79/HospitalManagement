package com.kairos.activity.counter.distribution.tab;

import com.kairos.activity.counter.KPIDTO;

import java.math.BigInteger;

public class TabKPIDTO {
    private BigInteger id;
    private String tabId;
    private KPIDTO kpi;
    private String data;
    private KPIPosition position;

    public String getTabId() {
        return tabId;
    }

    public BigInteger getId() {
        return id;
    }

    public void setId(BigInteger id) {
        this.id = id;
    }

    public void setTabId(String tabId) {
        this.tabId = tabId;
    }

    public KPIDTO getKpi() {
        return kpi;
    }

    public void setKpi(KPIDTO kpi) {
        this.kpi = kpi;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    public KPIPosition getPosition() {
        return position;
    }

    public void setPosition(KPIPosition position) {
        this.position = position;
    }
}
