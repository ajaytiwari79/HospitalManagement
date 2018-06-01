package com.kairos.activity.persistence.model.kpi;

import java.math.BigInteger;

public class UnitwiseKpiSettings extends ModulewiseKpiSettings {
    protected BigInteger unitId;
    protected BigInteger refKpiId;

    public BigInteger getUnitId() {
        return unitId;
    }

    public void setUnitId(BigInteger unitId) {
        this.unitId = unitId;
    }

    public BigInteger getRefKpiId() {
        return refKpiId;
    }

    public void setRefKpiId(BigInteger refKpiId) {
        this.refKpiId = refKpiId;
    }
}
