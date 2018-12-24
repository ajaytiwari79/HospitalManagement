package com.kairos.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

public enum Day {
    MONDAY(1),TUESDAY(2),WEDNESDAY(3),THURSDAY(4),FRIDAY(5),SATURDAY(6),SUNDAY(7),EVERYDAY(8);

    private int value;

    Day(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }

    @JsonCreator
        public static Day fromValue(String value) {
            return getEnumFromString(Day.class, value);
        }

        @JsonValue
        public String toJson() {
            return name().toLowerCase();
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


