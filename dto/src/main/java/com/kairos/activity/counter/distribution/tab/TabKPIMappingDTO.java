package com.kairos.activity.counter.distribution.tab;

import com.kairos.activity.enums.counter.CounterSize;

import java.math.BigInteger;
import java.util.List;

public class TabKPIMappingDTO {
    private String tabId;
    private BigInteger kpiId;
    private CounterSize counterSize;
    private KPIPosition kpiPosition;

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

    public CounterSize getCounterSize() {
        return counterSize;
    }

    public KPIPosition getKpiPosition() {
        return kpiPosition;
    }

    public void setKpiPosition(KPIPosition kpiPosition) {
        this.kpiPosition = kpiPosition;
    }

    public void setCounterSize(CounterSize counterSize) {
        this.counterSize = counterSize;

    }
}
