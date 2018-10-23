package com.kairos.enums.constraint;

import com.fasterxml.jackson.annotation.JsonValue;
import com.kairos.enums.solver_config.PlanningType;

/**
 * @author pradeep
 * @date - 20/6/18
 */

public enum ConstraintLevel {

    HARD("Hard"),MEDIUM("Medium"),SOFT("Soft");

    private String value;
    ConstraintLevel(String value) {
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
