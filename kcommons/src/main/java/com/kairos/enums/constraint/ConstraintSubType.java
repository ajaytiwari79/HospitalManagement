package com.kairos.enums.constraint;

public enum ConstraintSubType {
    //=========================SubTypes associated with its Type====
    //TODO refactor appropriate Constraint Sub Types
    //For Activity
    A1("activity1", ConstraintType.ACTIVITY),
    //For Shifts
    S1("shift1", ConstraintType.SHIFT),
    //For WTA
    WTA1("Wta1", ConstraintType.WTA);

    //===========================Variables==========================
    private String constraintSubTypeValue;
    private ConstraintType constraintType;

    //=======================Constructor============================
    ConstraintSubType(String constraintSubTypeValue, ConstraintType constraintType) {
        this.constraintSubTypeValue = constraintSubTypeValue;
        this.constraintType = constraintType;
    }

    //===========================Getters============================
    public String getConstraintType() {
        return constraintType.getConstraintTypeValue();
    }

    public String getConstraintSubType() {
        return constraintSubTypeValue;
    }
}
