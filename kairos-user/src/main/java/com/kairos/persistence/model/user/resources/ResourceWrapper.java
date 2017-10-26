package com.kairos.persistence.model.user.resources;

import com.fasterxml.jackson.annotation.JsonFormat;
import org.neo4j.ogm.annotation.typeconversion.Convert;
import org.springframework.data.neo4j.annotation.QueryResult;

import javax.validation.constraints.NotNull;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

/**
 * Created by prabjot on 16/10/17.
 */
@QueryResult
public class ResourceWrapper {
    private Long id;
    private String registrationNumber;
    private String number;
    private String modelDescription;
    private float costPerKM;
    private FuelType fuelType;
    private Long startDate;
    private Long endDate;
    private Long timeFrom;
    private Long timeTo;
    private Vehicle vehicleType;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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

    public Long getStartDate() {
        return startDate;
    }

    public void setStartDate(Long startDate) {
        this.startDate = startDate;
    }

    public Long getEndDate() {
        return endDate;
    }

    public void setEndDate(Long endDate) {
        this.endDate = endDate;
    }

    public Long getTimeFrom() {
        return timeFrom;
    }

    public void setTimeFrom(Long timeFrom) {
        this.timeFrom = timeFrom;
    }

    public Long getTimeTo() {
        return timeTo;
    }

    public void setTimeTo(Long timeTo) {
        this.timeTo = timeTo;
    }

    public Vehicle getVehicleType() {
        return vehicleType;
    }

    public void setVehicleType(Vehicle vehicleType) {
        this.vehicleType = vehicleType;
    }
}
