package com.kairos.persistence.model.user.resources;

import javax.validation.constraints.NotNull;

import static com.kairos.constants.UserMessagesConstants.ERROR_DESCRIPTION_NOTNULL;

/**
 * Created by prabjot on 13/10/17.
 */
public class ResourceDTO {

    @NotNull(message = "Registration number can't be empty")
    private String registrationNumber;
    private String number;
    @NotNull(message = ERROR_DESCRIPTION_NOTNULL)
    private String modelDescription;
    @NotNull(message = "Cost per km can not be null")
    private float costPerKM;
    @NotNull(message = "Fuel type can not be null")
    private FuelType fuelType;
    private Long vehicleTypeId;
    private String decommissionDate;

    public ResourceDTO() {
        //default constructor
    }

    public ResourceDTO(String registrationNumber, String number, String modelDescription, float costPerKM,
                       FuelType fuelType, Long vehicleTypeId) {
        this.registrationNumber = registrationNumber;
        this.number = number;
        this.modelDescription = modelDescription;
        this.costPerKM = costPerKM;
        this.fuelType = fuelType;
        this.vehicleTypeId = vehicleTypeId;
    }

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

    public String getDecommissionDate() {
        return decommissionDate;
    }

    public void setDecommissionDate(String decommissionDate) {
        this.decommissionDate = decommissionDate;
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
