package com.kairos.user.country;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

public enum Day {
    SUNDAY,MONDAY,TUESDAY,WEDNESDAY,THURSDAY,FRIDAY,SATURDAY,EVERYDAY;

    @JsonCreator
    public static Day fromValue(String value) {
        return getEnumFromString(Day.class, value);
    }

    @JsonValue
    public String toJson() {
        return name();
    }

    public static <T extends Enum<T>> T getEnumFromString(Class<T> enumClass, String value) {
        if (enumClass == null) {
            throw new IllegalArgumentException("EnumClass value can't be null.");
        }

        for (Enum<?> enumValue : enumClass.getEnumConstants()) {
            if (enumValue.toString().equalsIgnoreCase(value)) {
                return (T) enumValue;
            }
        }

        //Construct an error message that indicates all possible values for the enum.
        StringBuilder errorMessage = new StringBuilder();
        boolean bFirstTime = true;
        for (Enum<?> enumValue : enumClass.getEnumConstants()) {
            errorMessage.append(bFirstTime ? "" : ", ").append(enumValue);
            bFirstTime = false;
        }
        throw new IllegalArgumentException(value + " is invalid value. Supported values are " + errorMessage);
    }
}
