package com.kairos.enums;

import java.io.Serializable;

/**
 * Created by prerna on 10/4/18.
 */
public enum DurationType implements Serializable {
    MONTHS("Months"), WEEKS("Weeks"), DAYS("Days"), HOURS("Hours"), MINUTES("Minutes"), YEAR("Year");

    private String value;

    DurationType(String value) {
        this.value = value;
    }

    public String toValue() {
        return value;
    }

}
