package com.kairos.enums.solver_config;

import com.fasterxml.jackson.annotation.JsonValue;

/**
 * @author pradeep
 * @date - 20/6/18
 */

public enum ConstraintCategory {

    EFFICIENCY_CONSTRAINTS("Efficiency Constraint"),LONGEST_TASK("Longest Task"),DURATION_CONSTRAINTS("Duration Constraint"),BREAK_CONSTRAINTS("Break Constraints"),LOCATION_CONSTRAINTS("Location Constraint");

    private String value;
    ConstraintCategory(String value) {
        this.value = value;
    }

    @JsonValue
    public String toValue() {
        return value;
    }

    public static PlanningType getEnumByString(String status) {
        for (PlanningType is : PlanningType.values()) {
            if (status.equals(is.toValue()))
                return is;
        }
        return null;
    }

}
