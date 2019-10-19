package com.kairos.dto.activity.counter.enums;

public enum DisplayUnit {
    HOURS("Hours"),COUNT("Count"),PERCENTAGE("Percentage");

    private String displayValue;
    private DisplayUnit(String displayValue){
        this.displayValue = displayValue;
    }

    public String getDisplayValue() {
        return displayValue;
    }
}
