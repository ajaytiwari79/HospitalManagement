package com.kairos.dto.activity.counter.enums;

public enum DisplayUnit {
    HOURS("Hours"),COUNT("Count"),PERCENTAGE("Percentage"),PERCENTAGE_OF_HOURS("Percentage of hours"),PERCENTAGE_OF_TIME("Percentage of Times");

    private String displayValue;
    private DisplayUnit(String displayValue){
        this.displayValue = displayValue;
    }

    public String getDisplayValue() {
        return displayValue;
    }
}
