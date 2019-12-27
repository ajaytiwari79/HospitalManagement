package com.planner.enums;

import com.fasterxml.jackson.annotation.JsonValue;

public enum PlanningProblemStatus {
    IN_PROGRESS("In Progress"),
    SOLVED("Solved");
    private String value;

    private PlanningProblemStatus(String value) {
        this.value = value;
    }

    @JsonValue
    public String toValue() {
        return value;
    }

}
