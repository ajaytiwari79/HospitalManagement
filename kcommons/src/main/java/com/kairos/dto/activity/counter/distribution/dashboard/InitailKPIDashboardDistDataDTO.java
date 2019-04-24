package com.kairos.dto.activity.counter.distribution.dashboard;

import java.util.List;

public class InitailKPIDashboardDistDataDTO {
    private List<KPIDashboardDTO> dashboards;
    private List<DashboardKPIMappingDTO> dashboardKPIMap;

    public InitailKPIDashboardDistDataDTO() {
    }

    public InitailKPIDashboardDistDataDTO(List<KPIDashboardDTO> dashboards, List<DashboardKPIMappingDTO> dashboardKPIMap) {
        this.dashboards = dashboards;
        this.dashboardKPIMap = dashboardKPIMap;
    }

    public List<KPIDashboardDTO> getDashboards() {
        return dashboards;
    }

    public void setDashboards(List<KPIDashboardDTO> dashboards) {
        this.dashboards = dashboards;
    }

    public List<DashboardKPIMappingDTO> getDashboardKPIMap() {
        return dashboardKPIMap;
    }

    public void setDashboardKPIMap(List<DashboardKPIMappingDTO> dashboardKPIMap) {
        this.dashboardKPIMap = dashboardKPIMap;
    }
}
