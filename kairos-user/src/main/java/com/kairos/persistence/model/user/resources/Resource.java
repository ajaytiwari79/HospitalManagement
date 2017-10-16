package com.kairos.persistence.model.user.resources;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.kairos.persistence.model.common.UserBaseEntity;
import com.kairos.persistence.model.constants.RelationshipConstants;
import org.apache.commons.lang3.StringUtils;
import org.neo4j.ogm.annotation.NodeEntity;
import org.neo4j.ogm.annotation.Relationship;
import org.neo4j.ogm.annotation.typeconversion.Convert;

import java.time.*;
import java.util.List;

/**
 * Created by arvind on 6/10/16.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
@NodeEntity
public class Resource extends UserBaseEntity {


    private Vehicle vehicleType;
    private String registrationNumber;
    private String number;
    private String modelDescription;
    private float costPerKM;
    private FuelType fuelType;
    private boolean enabled ;
    private boolean deleted ;
    private Long startDate;
    private Long endDate;
    @Convert(Neo4jTimeConvertor.class)
    private LocalTime timeFrom;
    @Convert(Neo4jTimeConvertor.class)
    private LocalTime timeTo;
    public Resource(Vehicle vehicleType, String registrationNumber, String number, String modelDescription, float costPerKM) {
        this.vehicleType = vehicleType;
        this.registrationNumber = registrationNumber;
        this.number = number;
        this.modelDescription = modelDescription;
        this.costPerKM = costPerKM;
    }

    @Relationship(type = RelationshipConstants.RESOURCE_NOT_AVAILABLE_ON)
    private List<ResourceUnAvailability> resourceAvailabilities;

    public Resource() {
    }


    public void setRegistrationNumber(String registrationNumber) {
        this.registrationNumber = registrationNumber;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public void setModelDescription(String modelDescription) {
        this.modelDescription = modelDescription;
    }

    public void setCostPerKM(float costPerKM) {
        this.costPerKM = costPerKM;
    }

    public void setFuelType(FuelType fuelType) {
        this.fuelType = fuelType;
    }

    public List<ResourceUnAvailability> getResourceAvailabilities() {
        return resourceAvailabilities;
    }

    public void setResourceAvailabilities(List<ResourceUnAvailability> resourceAvailabilities) {
        this.resourceAvailabilities = resourceAvailabilities;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public boolean isDeleted() {
        return deleted;
    }

    public void setDeleted(boolean deleted) {
        this.deleted = deleted;
    }


    public String getRegistrationNumber() {
        return registrationNumber;
    }

    public String getNumber() {
        return number;
    }

    public String getModelDescription() {
        return modelDescription;
    }

    public float getCostPerKM() {
        return costPerKM;
    }

    public FuelType getFuelType() {
        return fuelType;
    }

    public Vehicle getVehicleType() {
        return vehicleType;
    }

    public void setVehicleType(Vehicle vehicleType) {
        this.vehicleType = vehicleType;
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

    public LocalTime getTimeFrom() {
        return timeFrom;
    }

    public void setTimeFrom(LocalTime timeFrom) {
        this.timeFrom = timeFrom;
    }

    public LocalTime getTimeTo() {
        return timeTo;
    }

    public void setTimeTo(LocalTime timeTo) {
        this.timeTo = timeTo;
    }

    public void setAvailability(ResourceDTO resourceDTO){
        Instant instant = Instant.parse(resourceDTO.getStartDate());
        LocalDateTime startDate = LocalDateTime.ofInstant(instant, ZoneId.of(ZoneOffset.UTC.getId()));
        this.startDate = startDate.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli();
        if(!StringUtils.isBlank(resourceDTO.getEndDate())){
            instant = Instant.parse(resourceDTO.getEndDate());
            LocalDateTime endDate = LocalDateTime.ofInstant(instant, ZoneId.of(ZoneOffset.UTC.getId()));
            this.endDate = endDate.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli();
        }
        if(!StringUtils.isBlank(resourceDTO.getTimeFrom())){
            instant = Instant.parse(resourceDTO.getTimeFrom());
            LocalDateTime startTime = LocalDateTime.ofInstant(instant, ZoneId.of(ZoneOffset.UTC.getId()));
            this.timeFrom = LocalTime.of(startTime.getHour(),startTime.getSecond());
        }
        if(!StringUtils.isBlank(resourceDTO.getTimeTo())){
            instant = Instant.parse(resourceDTO.getTimeTo());
            LocalDateTime endTime = LocalDateTime.ofInstant(instant, ZoneId.of(ZoneOffset.UTC.getId()));
            this.timeTo = LocalTime.of(endTime.getHour(),endTime.getSecond());
        }
    }
}



