package com.kairos.dto.activity.counter.enums;

public enum LocationType {
    FIX(1),
    FLOAT(100);

    public int value;

    LocationType(int value) {this.value=value;}
}
