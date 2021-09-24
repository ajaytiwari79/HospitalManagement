package com.kairos.enums.shift;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ShiftFilterDurationType {
    INDIVIDUAL(5,"INDIVIDUAL"),DAILY(5,"DAILY"),WEEKLY(3,"WEEKLY"),MONTHLY(1,"MONTHLY");

    private int duration;
    private String value;
}


