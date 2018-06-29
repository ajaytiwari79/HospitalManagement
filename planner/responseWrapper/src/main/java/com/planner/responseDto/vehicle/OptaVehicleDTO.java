package com.planner.responseDto.vehicle;

import com.planner.responseDto.commonDto.BaseDTO;

import java.util.List;

public class OptaVehicleDTO extends BaseDTO{

    private Double speed;
    private String vehicleType;
    private List<Long> skills;
    private Double range;
    private Integer fuelLimitation;
    private Integer capacity;
    private String registrationNumber;
    private String number;
    private String modelDescription;
    private Float costPerKM;
    private String fuelType;
    private List<OptaVehicleAvailabality> unAvailabalities;


    public List<OptaVehicleAvailabality> getUnAvailabalities() {
        return unAvailabalities;
    }

    public void setUnAvailabalities(List<OptaVehicleAvailabality> unAvailabalities) {
        this.unAvailabalities = unAvailabalities;
    }


    public Integer getFuelLimitation() {
        return fuelLimitation;
    }

    public void setFuelLimitation(Integer fuelLimitation) {
        this.fuelLimitation = fuelLimitation;
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

    public Float getCostPerKM() {
        return costPerKM;
    }

    public void setCostPerKM(Float costPerKM) {
        this.costPerKM = costPerKM;
    }

    public String getFuelType() {
        return fuelType;
    }

    public void setFuelType(String fuelType) {
        this.fuelType = fuelType;
    }

    public Integer getCapacity() {
        return capacity;
    }

    public void setCapacity(Integer capacity) {
        this.capacity = capacity;
    }


    public Double getSpeed() {
        return speed;
    }

    public void setSpeed(Double speed) {
        this.speed = speed;
    }

    public String getVehicleType() {
        return vehicleType;
    }

    public void setVehicleType(String vehicleType) {
        this.vehicleType = vehicleType;
    }

    public List<Long> getSkills() {
        return skills;
    }

    public void setSkills(List<Long> skills) {
        this.skills = skills;
    }

    public Double getRange() {
        return range;
    }

    public void setRange(Double range) {
        this.range = range;
    }


}