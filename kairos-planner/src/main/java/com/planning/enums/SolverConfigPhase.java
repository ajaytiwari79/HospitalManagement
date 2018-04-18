package com.planning.enums;

import com.fasterxml.jackson.annotation.JsonValue;

public enum SolverConfigPhase {

    TASKPLANNING("Task Planning"),SHIFTPLANNINGRECOMENDATION("Shift Planning Recomendation");

    private String value;
    SolverConfigPhase(String value) {
        this.value = value;
    }

    @JsonValue
    public String toValue() {
        return value;
    }

    public static SolverConfigPhase getEnumByString(String status) {
        for (SolverConfigPhase is : SolverConfigPhase.values()) {
            if (status.equals(is.toValue()))
                return is;
        }
        return null;
    }

}
