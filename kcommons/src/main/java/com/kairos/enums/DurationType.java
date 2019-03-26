package com.kairos.enums;

/**
 * Created by prerna on 10/4/18.
 */
public enum DurationType {
    MONTHS("Months"),WEEKS("Weeks"),DAYS("Days"),HOURS("Hours"),MINUTES("Minutes"),YEAR("Year");

    private String value;

    DurationType(String value) {
        this.value = value;
    }

    public String toValue(){
        return value;
    }

}
