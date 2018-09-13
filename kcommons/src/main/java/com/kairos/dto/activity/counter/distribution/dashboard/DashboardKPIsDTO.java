package com.kairos.dto.activity.counter.distribution.dashboard;

import java.math.BigInteger;
import java.util.List;

public class DashboardKPIsDTO {
    private BigInteger dashboardId;
    private String moduleId;
    private List<BigInteger> kpiIds;

    public DashboardKPIsDTO() {
    }

    public DashboardKPIsDTO(BigInteger dashboardId,String moduleId, List<BigInteger> kpiIds) {
        this.dashboardId = dashboardId;
        this.moduleId=moduleId;
        this.kpiIds = kpiIds;
    }

    public String getModuleId() {
        return moduleId;
    }

    public void setModuleId(String moduleId) {
        this.moduleId = moduleId;
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
