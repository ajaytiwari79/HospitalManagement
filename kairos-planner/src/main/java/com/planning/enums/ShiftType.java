package com.planning.enums;

import com.fasterxml.jackson.annotation.JsonValue;

public enum ShiftType {

    PRESENT("Present"), ABSENT("Absent"), PARTIALLY_ABSENT("Pratially_Absent");

    private String value;

    private ShiftType(String value) {
        this.value = value;
    }

    @JsonValue
    public String toValue() {
        return value;
    }

    public static ShiftType getEnumByString(String status) {
        for (ShiftType is : ShiftType.values()) {
            if (status.equals(is.toValue()))
                return is;
        }
        return null;
    }
}
