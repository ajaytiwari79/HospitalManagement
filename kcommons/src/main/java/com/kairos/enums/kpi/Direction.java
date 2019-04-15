package com.kairos.enums.kpi;

import java.util.Locale;
import java.util.Optional;

public enum Direction {

    ASC, DESC;

    public boolean isAscending() {
        return this.equals(ASC);
    }

    public boolean isDescending() {
        return this.equals(DESC);
    }

}
