package com.kairos.dto.activity.counter.enums;

public enum ConfLevel {

    DEFAULT(0),
    COUNTRY(1),
    UNIT(2),
    STAFF(3);

    public int value;

    ConfLevel(int value) { this.value=value; }
}
