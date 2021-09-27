package com.kairos.enums.kpi;

import java.io.Serializable;

public enum Direction implements Serializable {

    ASC, DESC;

    public boolean isAscending() {
        return this.equals(ASC);
    }

    public boolean isDescending() {
        return this.equals(DESC);
    }




}
