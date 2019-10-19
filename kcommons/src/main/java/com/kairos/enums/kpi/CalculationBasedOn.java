package com.kairos.enums.kpi;

public enum CalculationBasedOn {

    TIME_TYPE("TimeType"),ACTIVITY("Activity"),PLANNED_TIME("Planned Time");

    public String value;

    CalculationBasedOn(String value) {
        this.value = value;
    }
}
