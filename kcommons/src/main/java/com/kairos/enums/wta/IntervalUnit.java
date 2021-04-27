package com.kairos.enums.wta;

import java.io.Serializable;

public enum IntervalUnit implements Serializable {
    DAYS("Days"),WEEKS("Weeks"), MONTHS("Months"),YEARS("Years"),NEXT("Next"), CURRENT("Current"), LAST("Last");
    private String value;
    IntervalUnit(String value){
        this.value=value;
    }
    @Override
    public String toString() {
        return super.toString();
    }
}
