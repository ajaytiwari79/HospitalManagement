package com.kairos.dto.activity.counter.distribution.dashboard;

import java.math.BigInteger;
import java.util.List;

public class DashboardKPIsDTO {
    private BigInteger dashboartId;
    private List<BigInteger> kpiIds;

    public DashboardKPIsDTO() {
    }

    public DashboardKPIsDTO(BigInteger dashboartId, List<BigInteger> kpiIds) {
        this.dashboartId = dashboartId;
        this.kpiIds = kpiIds;
    }

    public BigInteger getDashboartId() {
        return dashboartId;
    }

    public void setDashboartId(BigInteger dashboartId) {
        this.dashboartId = dashboartId;
    }

    public List<BigInteger> getKpiIds() {
        return kpiIds;
    }

    public void setKpiIds(List<BigInteger> kpiIds) {
        this.kpiIds = kpiIds;
    }
}
