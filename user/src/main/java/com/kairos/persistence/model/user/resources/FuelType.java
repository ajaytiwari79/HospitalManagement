package com.kairos.persistence.model.user.resources;

public enum FuelType{
DIESEL("DIESEL"),PETROL("PETROL"),GAS("GAS"),ELECTRIC("ELECTRIC"),HYBRID("HYBRID"),NONE("NONE");

    private String value;
    FuelType(String value){
        this.value = value;
    }

    public static FuelType getByValue(String value){
        for(FuelType fuelType:FuelType.values()){
            if(fuelType.value == value){
                return fuelType;
            }
        }
        return null;
    }


}
