package com.kairos.dto.activity.counter.distribution.dashboard;

import com.kairos.dto.activity.counter.distribution.tab.KPIPosition;
import com.kairos.dto.activity.counter.enums.CounterSize;

import java.math.BigInteger;

public class DashboardKPIMappingDTO {
    private BigInteger id;
    private String dashboardId;
    private BigInteger kpiId;
    private CounterSize size;
    private KPIPosition position;

    public BigInteger getId() {
        return id;
    }

    public void setId(BigInteger id) {
        this.id = id;
    }

    public String getDashboardId() {
        return dashboardId;
    }

    public void setDashboardId(String dashboardId) {
        this.dashboardId = dashboardId;
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

    public void setSize(CounterSize size) {
        this.size = size;
    }

    public KPIPosition getPosition() {
        return position;
    }

    public void setPosition(KPIPosition position) {
        this.position = position;
    }
}
