package com.kairos.activity.counter;

import com.kairos.activity.counter.enums.ConfLevel;

import java.math.BigInteger;

public class ApplicableKPIDTO {
    private KPIDTO kpiIds;
    private ConfLevel level;

    public KPIDTO getKpiIds() {
        return kpiIds;
    }

    public void setKpiIds(KPIDTO kpiIds) {
        this.kpiIds = kpiIds;
    }

    public ConfLevel getLevel() {
        return level;
    }

    public void setLevel(ConfLevel level) {
        this.level = level;
    }

}
