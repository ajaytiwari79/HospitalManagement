package com.kairos.persistence.model.enums.phase;

public enum PhaseType {
    PLANNING("Planning"), ACTUAL("Actual");

    public String value;

    PhaseType(String value) {
        this.value = value;
    }

}
