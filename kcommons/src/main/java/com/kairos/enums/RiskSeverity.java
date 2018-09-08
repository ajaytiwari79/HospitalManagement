package com.kairos.enums;

public enum RiskSeverity {

    HIGH("high"),  LOW("low"), MEDIUM("medium"),MODERATE("moderate");
    public String value;
    RiskSeverity(String value) {
        this.value = value;
    }

}
