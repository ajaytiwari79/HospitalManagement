package com.kairos.enums;

import java.io.Serializable;

public enum TimeTypes implements Serializable {

    WORKING_TYPE("Working time"), NON_WORKING_TYPE("Non working time");

    private String value;

    TimeTypes(String value) {
        this.value = value;
    }

    public static TimeTypes getByValue(String value) {
        for (TimeTypes type : TimeTypes.values()) {
            if (type.value.equals(value)) {
                return type;
            }
        }
        return null;
    }

    public String toValue(){
        return value;
    }

}