package com.kairos.dto.activity.counter.distribution.dashboard;

import com.kairos.dto.activity.counter.configuration.KPIDTO;
import com.kairos.dto.activity.counter.distribution.tab.KPIPosition;

import java.math.BigInteger;

public class DashboardKPIDTO {
    private BigInteger id;
    private String dashboardId;
    private KPIDTO kpi;
    private String data;
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
