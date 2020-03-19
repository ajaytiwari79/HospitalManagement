package com.kairos.enums.shift;

public enum ShiftFilterDurationType {
    INDIVIDUAL(5,"INDIVIDUAL"),DAILY(5,"DAILY"),WEEKLY(3,"WEEKLY"),MONTHLY(1,"MONTHLY");

    private int duration;
    private String value;

    ShiftFilterDurationType(int duration,String value){
        this.duration = duration;
        this.value = value;
    }

    public int getDuration() {
        return duration;
    }

    public String getValue() {
        return value;
    }
}


