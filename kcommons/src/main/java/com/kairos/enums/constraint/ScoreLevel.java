package com.kairos.enums.constraint;

import java.io.Serializable;

/**
 * @author pradeep
 * @date - 20/6/18
 */

public enum ScoreLevel implements Serializable {

    HARD("Hard"),MEDIUM("Medium"),SOFT("Soft");

    private String value;
    ScoreLevel(String value) {
        this.value = value;
    }

    public String toValue() {
        return value;
    }

    public static ScoreLevel getEnumByString(String status) {
        for (ScoreLevel is : ScoreLevel.values()) {
            if (status.equals(is.toValue()))
                return is;
        }
        return null;
    }
}
