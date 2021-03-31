package com.kairos.dto.activity.counter.enums;

import java.io.Serializable;

public enum LocationType implements Serializable {
    FIX(1),
    FLOAT(100);

    public int value;

    LocationType(int value) {this.value=value;}
}
