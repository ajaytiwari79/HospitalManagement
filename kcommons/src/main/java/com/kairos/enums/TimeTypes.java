package com.kairos.enums;

public enum TimeTypes {

    WORKING_TYPE("Working time"),NON_WORKING_TYPE("Non working time");

    private String value;
    TimeTypes(String value){
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