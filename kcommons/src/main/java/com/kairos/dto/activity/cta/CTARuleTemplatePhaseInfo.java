package com.kairos.dto.activity.cta;

import java.math.BigInteger;
import java.util.Objects;

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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CTARuleTemplatePhaseInfo that = (CTARuleTemplatePhaseInfo) o;
        return beforeStart == that.beforeStart &&
                Objects.equals(phaseId, that.phaseId) &&
                type == that.type;
    }

    @Override
    public int hashCode() {

        return Objects.hash(phaseId, type, beforeStart);
    }
}
