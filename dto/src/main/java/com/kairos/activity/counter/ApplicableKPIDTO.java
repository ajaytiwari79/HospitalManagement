package com.kairos.activity.counter;

import com.kairos.activity.counter.enums.ConfLevel;

import java.math.BigInteger;

public class ApplicableKPIDTO {
    private KPIDTO kpi;
    private ConfLevel level;

    public KPIDTO getKpi() {
        return kpi;
    }

    public void setKpi(KPIDTO kpi) {
        this.kpi = kpi;
    }

    public ConfLevel getLevel() {
        return level;
    }

    public void setLevel(ConfLevel level) {
        this.level = level;
    }

}
