package com.kairos.dto.activity.counter.distribution.dashboard;

import java.math.BigInteger;
import java.util.List;

public class DashboardKPIsDTO {
    private BigInteger dashboardId;
    private List<BigInteger> kpiIds;

    public DashboardKPIsDTO() {
    }

    public DashboardKPIsDTO(BigInteger dashboardId, List<BigInteger> kpiIds) {
        this.dashboardId = dashboardId;
        this.kpiIds = kpiIds;
    }

    public BigInteger getDashboardId() {
        return dashboardId;
    }

    public void setDashboardId(BigInteger dashboardId) {
        this.dashboardId = dashboardId;
    }

    public List<BigInteger> getKpiIds() {
        return kpiIds;
    }

    public void setKpiIds(List<BigInteger> kpiIds) {
        this.kpiIds = kpiIds;
    }
}
