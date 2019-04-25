package com.planner.domain.vehicle;

import com.planner.domain.common.BaseEntity;
import com.planner.enums.VehicleType;

import java.util.List;

//import org.springframework.data.cassandra.core.mapping.Table;

//@Table
public class PlanningVehicle extends BaseEntity {


    private Double speed;
    private VehicleType vehicleType;
    List<String> skills;
    private Double range;
    private Integer fuelLimitation;
    private Integer capacity;
    private String registrationNumber;
    private String number;
    private String modelDescription;
    private Float costPerKM;
    private String fuelType;
    private List<String> availabilities;
    private List<String> unAvailabilities;

    public Integer getFuelLimitation() {
        return fuelLimitation;
    }

    public void setFuelLimitation(Integer fuelLimitation) {
        this.fuelLimitation = fuelLimitation;
    }

    public Integer getCapacity() {
        return capacity;
    }

    public void setCapacity(Integer capacity) {
        this.capacity = capacity;
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

    public List<String> getAvailabilities() {
        return availabilities;
    }

    public void setAvailabilities(List<String> availabilities) {
        this.availabilities = availabilities;
    }

    public List<String> getUnAvailabilities() {
        return unAvailabilities;
    }

    public void setUnAvailabilities(List<String> unAvailabilities) {
        this.unAvailabilities = unAvailabilities;
    }

    public Double getSpeed() {
        return speed;
    }

    public void setSpeed(Double speed) {
        this.speed = speed;
    }

    public VehicleType getVehicleType() {
        return vehicleType;
    }

    public void setVehicleType(VehicleType vehicleType) {
        this.vehicleType = vehicleType;
    }

    public List<String> getSkills() {
        return skills;
    }

    public void setSkills(List<String> skills) {
        this.skills = skills;
    }

    public Double getRange() {
        return range;
    }

    public void setRange(Double range) {
        this.range = range;
    }
}
