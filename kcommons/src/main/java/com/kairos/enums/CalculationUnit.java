package com.kairos.enums;

public enum CalculationUnit {
    HOURS("Hours"), PERCENTAGE("Percentage");

    public String value;

    CalculationUnit(String value) {
        this.value = value;
    }
}
