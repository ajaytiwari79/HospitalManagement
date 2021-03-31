package com.kairos.dto.activity.counter.enums;

import java.io.Serializable;

public enum ConfLevel implements Serializable {

    DEFAULT(0),
    COUNTRY(1),
    UNIT(2),
    STAFF(3),
    ORGANIZATION(4);

    public int value;

    ConfLevel(int value) { this.value=value; }
}
