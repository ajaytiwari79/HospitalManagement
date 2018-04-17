package com.planning.enums;

import com.fasterxml.jackson.annotation.JsonValue;

public enum FuelType {

    PETROL("Petrol"),DIESEL("Diesel"),ELECTRIC("Electric");

    private String value;

    private FuelType(String value) {
        this.value = value;
    }

    @JsonValue
    public String toValue() {
        return value;
    }

    public static FuelType getEnumByString(String status) {
        for (FuelType is : FuelType.values()) {
            if (status.equals(is.toValue()))
                return is;
        }
        return null;
    }
}
