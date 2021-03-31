package com.kairos.dto.activity.counter.enums;

import java.io.Serializable;

public enum KPIValidity implements Serializable {
    MANDATORY(1),
    OPTIONAL(1000),
    BASIC(10);

    public int value;

    KPIValidity(int value) {this.value=value;}

}
