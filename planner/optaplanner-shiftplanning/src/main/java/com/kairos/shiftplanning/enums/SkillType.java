package com.kairos.shiftplanning.enums;

import java.io.Serializable;

public enum SkillType implements Serializable {
    BASIC("Basic"), ADVANCE("Advance"), EXPERT("Expert");

    private String value;

    private SkillType(String value) {
        this.value = value;
    }

    public String toValue() {
        return value;
    }

    public static SkillType getEnumByString(String status) {
        for (SkillType is : SkillType.values()) {
            if (status.equals(is.toValue()))
                return is;
        }
        return null;
    }
}
