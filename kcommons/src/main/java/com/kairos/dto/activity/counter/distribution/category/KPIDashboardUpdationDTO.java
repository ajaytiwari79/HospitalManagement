package com.kairos.dto.activity.counter.distribution.category;

import com.kairos.dto.activity.counter.distribution.dashboard.KPIDashboardDTO;

import java.util.List;

public class KPIDashboardUpdationDTO {
    private List<KPIDashboardDTO> deleteDashboardTab;
    private List<KPIDashboardDTO> updateDashboardTab;

    public List<KPIDashboardDTO> getDeleteDashboardTab() {
        return deleteDashboardTab;
    }

    public void setDeleteDashboardTab(List<KPIDashboardDTO> deleteDashboardTab) {
        this.deleteDashboardTab = deleteDashboardTab;
    }

    public List<KPIDashboardDTO> getUpdateDashboardTab() {
        return updateDashboardTab;
    }

    public void setUpdateDashboardTab(List<KPIDashboardDTO> updateDashboardTab) {
        this.updateDashboardTab = updateDashboardTab;
    }
}
