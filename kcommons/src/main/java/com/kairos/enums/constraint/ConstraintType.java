package com.kairos.enums.constraint;

public enum ConstraintType {
    ACTIVITY("Activity"),WTA("Wta"),SHIFT("Shift");
    private String value;
    ConstraintType(String value) {
        this.value=value;
    }

    public String getConstraintTypeValue()
    {
        return value;
    }
}
