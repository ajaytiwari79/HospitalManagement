package com.kairos.activity.response.dto.kpi;

import java.math.BigInteger;

public class ModulewiseKpiDTO {
    private BigInteger id;
    private BigInteger kpiId;

    public BigInteger getId() {
        return id;
    }

    public void setId(BigInteger id) {
        this.id = id;
    }

    public BigInteger getKpiId() {
        return kpiId;
    }

    public void setKpiId(BigInteger kpiId) {
        this.kpiId = kpiId;
    }
}
