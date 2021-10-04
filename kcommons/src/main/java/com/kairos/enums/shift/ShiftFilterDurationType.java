package com.kairos.enums.shift;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.io.Serializable;

@Getter
@AllArgsConstructor
public enum ShiftFilterDurationType implements Serializable {
    TIMEBALANCE(5,"TIME-BALANCE"),INDIVIDUAL(5,"INDIVIDUAL"),DAILY(5,"DAILY"),WEEKLY(3,"WEEKLY"),MONTHLY(1,"MONTHLY"),;

    private int duration;
    private String value;
}


