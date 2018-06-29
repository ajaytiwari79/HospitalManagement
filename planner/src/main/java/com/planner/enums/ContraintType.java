package com.planner.enums;

import com.fasterxml.jackson.annotation.JsonValue;

public enum ContraintType {

    IMPOSSIBLE("Impossible"),HARD("Hard"),MEDIUM("Medium"),SOFT("Soft");

    String value;

    ContraintType(String value) {
        this.value = value;
    }

    @JsonValue
    public String toValue() {
        return value;
    }

    public static ContraintType getEnumByString(String status) {
        for (ContraintType is : ContraintType.values()) {
            if (status.equals(is.toValue()))
                return is;
        }
        return null;
    }
}
