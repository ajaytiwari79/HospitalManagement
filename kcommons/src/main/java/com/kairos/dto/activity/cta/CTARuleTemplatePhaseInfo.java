package com.kairos.dto.activity.cta;

public class CTARuleTemplatePhaseInfo{
    private Long phaseId;
    private phaseType type;
    private int beforeStart;
    public enum  phaseType{
        DAYS,HOURS;
    }

    public Long getPhaseId() {
        return phaseId;
    }

    public void setPhaseId(Long phaseId) {
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
