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
    @NotNull(message = "start date can't be empty")
    private String startDate;
    private String endDate;
    private String timeFrom;
    private String timeTo;
    @NotNull(message = "Vehicle type can't be empty")
    private Long vehicleTypeId;

    public Long getVehicleTypeId() {
        return vehicleTypeId;
    }

    public void setVehicleTypeId(Long vehicleTypeId) {
        this.vehicleTypeId = vehicleTypeId;
    }

    public String getStartDate() {
        return startDate;
    }

    public void setStartDate(String startDate) {
        this.startDate = startDate;
    }

    public String getEndDate() {
        return endDate;
    }

    public void setEndDate(String endDate) {
        this.endDate = endDate;
    }

    public String getTimeFrom() {
        return timeFrom;
    }

    public void setTimeFrom(String timeFrom) {
        this.timeFrom = timeFrom;
    }

    public String getTimeTo() {
        return timeTo;
    }

    public void setTimeTo(String timeTo) {
        this.timeTo = timeTo;
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
                ", fuelType=" + fuelType +
                ", startDate=" + startDate +
                ", endDate=" + endDate +
                ", timeFrom=" + timeFrom +
                ", timeTo=" + timeTo +
                ", vehicleTypeId=" + vehicleTypeId +
                '}';
    }
}
