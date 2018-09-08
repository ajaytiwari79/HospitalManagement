package com.kairos.enums.solver_config;

import com.fasterxml.jackson.annotation.JsonValue;

/**
 * @author pradeep
 * @date - 20/6/18
 */

public enum PlanningType {

    VRPPLANNING("VRP planning"),TASKPLANNING("Task Planning"),SHIFTPLANNINGRECOMENDATION("Shift Planning Recomendation"),SHIFTPLANNING("Shift Planning");

    private String value;
    PlanningType(String value) {
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
