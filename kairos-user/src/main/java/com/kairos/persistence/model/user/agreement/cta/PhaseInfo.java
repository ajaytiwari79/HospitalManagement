package com.kairos.persistence.model.user.agreement.cta;

public class PhaseInfo {
    private Long phaseId;
    private phaseType type;
    private int beforeStart;
    public enum  phaseType{
        DAYS,HOURS;
    }
}
