package com.kairos.utils.user_context;

import java.io.Serializable;

/**
 * Created by prabjot on 19/10/16.
 */
public enum Gender implements Serializable {

    MALE("Male"), FEMALE("Female");
    public String value;

    Gender(String value) {
        this.value = value;
    }

    public static Gender getByValue(String value) {
        for (Gender gender : Gender.values()) {
            if (gender.value.equals(value)) {
                return gender;
            }
        }
        return null;
    }
}
