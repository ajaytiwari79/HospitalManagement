package com.planner.enums;

import com.fasterxml.jackson.annotation.JsonValue;

public enum VehicleType {

    CAR("Car"), BIKE("Bike"), BICYCLE("Bicycle");

    private String value;

    private VehicleType(String value) {
        this.value = value;
    }

    @JsonValue
    public String toValue() {
        return value;
    }

    public static VehicleType getEnumByString(String status) {
        for (VehicleType is : VehicleType.values()) {
            if (status.equals(is.toValue()))
                return is;
        }
        return null;
    }
}
