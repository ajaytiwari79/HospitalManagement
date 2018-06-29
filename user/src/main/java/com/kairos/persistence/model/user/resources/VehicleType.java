package com.kairos.persistence.model.user.resources;

public enum VehicleType{
    CAR("Car"),BICYCLE("Bicycle"),ELECTRIC_BICYCLE("Electric Bicycle"),MANUAL_BICYCLE("Manual Bicycle"),ELECTRIC_CAR("Electric Car"),AMBULANCE("Ambulance");
    public String value;

    VehicleType(String value) {
        this.value = value;
    }


}
