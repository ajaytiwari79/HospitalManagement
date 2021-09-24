package com.kairos.dto.activity.counter.enums;

public enum KPIValidity {
    MANDATORY(1),
    OPTIONAL(1000),
    BASIC(10);

    public int value;

    KPIValidity(int value) {this.value=value;}

}
