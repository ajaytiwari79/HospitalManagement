package com.kairos.persistence.model.user.resources;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.kairos.persistence.model.common.UserBaseEntity;
import com.kairos.persistence.model.constants.RelationshipConstants;
import org.neo4j.ogm.annotation.NodeEntity;
import org.neo4j.ogm.annotation.Relationship;

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
    private Long decommissionDate;

    public Resource(Vehicle vehicleType, String registrationNumber, String number, String modelDescription,
                    float costPerKM,FuelType fuelType) {
        this.vehicleType = vehicleType;
        this.registrationNumber = registrationNumber;
        this.number = number;
        this.modelDescription = modelDescription;
        this.costPerKM = costPerKM;
        this.fuelType = fuelType;
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

    public Long getDecommissionDate() {
        return decommissionDate;
    }

    public void setDecommissionDate(Long decommissionDate) {
        this.decommissionDate = decommissionDate;
    }
}



