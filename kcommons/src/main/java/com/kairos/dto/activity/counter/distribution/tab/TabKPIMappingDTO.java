package com.kairos.dto.activity.counter.distribution.tab;

import com.kairos.dto.activity.counter.enums.CounterSize;

import java.math.BigInteger;

public class TabKPIMappingDTO {
    private BigInteger id;
    private String tabId;
    private BigInteger kpiId;
    private CounterSize size;
    private KPIPosition position;

    public TabKPIMappingDTO() {
    }

    public TabKPIMappingDTO(String tabId, BigInteger kpiId) {
        this.tabId = tabId;
        this.kpiId = kpiId;
    }

    public TabKPIMappingDTO(String tabId, BigInteger kpiId, CounterSize size, KPIPosition position) {
        this.tabId = tabId;
        this.kpiId = kpiId;
        this.size = size;
        this.position = position;
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

    public CounterSize getSize() {
        return size;
    }

    public KPIPosition getPosition() {
        return position;
    }

    public void setPosition(KPIPosition position) {
        this.position = position;
    }

    public void setSize(CounterSize size) {
        this.size = size;
    }

    public BigInteger getId() {
        return id;
    }

    public void setId(BigInteger id) {
        this.id = id;
    }
}
