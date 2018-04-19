package com.planner.enums;

import com.fasterxml.jackson.annotation.JsonValue;

public enum SkillLevel {

    BASIC("Basic"), ADVANCE("Advance"), EXPERT("Expert");

    private String value;

    SkillLevel(String value) {
        this.value = value;
    }

    @JsonValue
    public String toValue() {
        return value;
    }

    public static SkillLevel getEnumByString(String status) {
        for (SkillLevel is : SkillLevel.values()) {
            if (status.equals(is.toValue()))
                return is;
        }
        return null;
    }

}
