package com.kairos.persistence.model.user.resources;

import java.util.List;

/**
 * Created by prabjot on 13/10/17.
 */
public class ResourceWrapper {

    private List<Vehicle> vehicleTypeList;
    private List<FuelType> fuelTypeList;

    public List<Vehicle> getVehicleTypeList() {
        return vehicleTypeList;
    }

    public void setVehicleTypeList(List<Vehicle> vehicleTypeList) {
        this.vehicleTypeList = vehicleTypeList;
    }

    public List<FuelType> getFuelTypeList() {
        return fuelTypeList;
    }

    public void setFuelTypeList(List<FuelType> fuelTypeList) {
        this.fuelTypeList = fuelTypeList;
    }

    public ResourceWrapper(List<Vehicle> vehicleTypeList, List<FuelType> fuelTypeList) {
        this.vehicleTypeList = vehicleTypeList;
        this.fuelTypeList = fuelTypeList;
    }
}
