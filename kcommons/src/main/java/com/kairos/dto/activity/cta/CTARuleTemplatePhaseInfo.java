package com.kairos.dto.activity.cta;

import java.math.BigInteger;

public class CTARuleTemplatePhaseInfo{
    private BigInteger phaseId;
    private phaseType type;
    private int beforeStart;
    public enum  phaseType{
        DAYS,HOURS;
    }

    public BigInteger getPhaseId() {
        return phaseId;
    }

    public void setPhaseId(BigInteger phaseId) {
        this.phaseId = phaseId;
    }

    public phaseType getType() {
        return type;
    }

    public void setType(phaseType type) {
        this.type = type;
    }

    public int getBeforeStart() {
        return beforeStart;
    }

    public void setBeforeStart(int beforeStart) {
        this.beforeStart = beforeStart;
    }
}
