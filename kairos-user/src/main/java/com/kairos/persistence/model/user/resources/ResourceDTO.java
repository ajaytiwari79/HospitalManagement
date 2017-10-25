package com.kairos.persistence.model.user.resources;

import javax.validation.constraints.NotNull;

/**
 * Created by prabjot on 13/10/17.
 */
public class ResourceDTO {

    @NotNull(message = "Registration number can't be empty")
    private String registrationNumber;
    @NotNull(message = "error.Resource.registrationNumber.notnull")
    private String number;
    @NotNull(message = "error.description.notnull")
    private String modelDescription;
    private float costPerKM;
    private FuelType fuelType;
    private Long vehicleTypeId;

    public Long getVehicleTypeId() {
        return vehicleTypeId;
    }

    public void setVehicleTypeId(Long vehicleTypeId) {
        this.vehicleTypeId = vehicleTypeId;
    }



    public String getRegistrationNumber() {
        return registrationNumber;
    }

    public void setRegistrationNumber(String registrationNumber) {
        this.registrationNumber = registrationNumber;
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public String getModelDescription() {
        return modelDescription;
    }

    public void setModelDescription(String modelDescription) {
        this.modelDescription = modelDescription;
    }

    public float getCostPerKM() {
        return costPerKM;
    }

    public void setCostPerKM(float costPerKM) {
        this.costPerKM = costPerKM;
    }

    public FuelType getFuelType() {
        return fuelType;
    }

    public void setFuelType(FuelType fuelType) {
        this.fuelType = fuelType;
    }

    @Override
    public String toString() {
        return "ResourceDTO{" +
                "registrationNumber='" + registrationNumber + '\'' +
                ", number='" + number + '\'' +
                ", modelDescription='" + modelDescription + '\'' +
                ", costPerKM=" + costPerKM +
                ", vehicleTypeId=" + vehicleTypeId +
                '}';
    }
}
