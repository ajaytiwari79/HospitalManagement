package com.kairos.persistence.model.user.resources;

import com.fasterxml.jackson.annotation.*;
import org.springframework.data.neo4j.annotation.QueryResult;

import java.io.Serializable;
import java.util.List;

/**
 * Created by prabjot on 16/10/17.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
@QueryResult
public class ResourceWrapper implements Serializable {
    private Long id;
    private String registrationNumber;
    private String number;
    private String modelDescription;
    private float costPerKM;
    private FuelType fuelType;
    private Vehicle vehicleType;
    private Long creationDate;
    private Long decommissionDate;
    private boolean isDecommision;
    private List<ResourceUnAvailability> resourceUnAvailabilities;

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

    public Vehicle getVehicleType() {
        return vehicleType;
    }

    public void setVehicleType(Vehicle vehicleType) {
        this.vehicleType = vehicleType;
    }

    public List<ResourceUnAvailability> getResourceUnAvailabilities() {
        return resourceUnAvailabilities;
    }

    public void setResourceUnAvailabilities(List<ResourceUnAvailability> resourceUnAvailabilities) {
        this.resourceUnAvailabilities = resourceUnAvailabilities;
    }

    public Long getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(Long creationDate) {
        this.creationDate = creationDate;
    }

    public Long getDecommissionDate() {
        return decommissionDate;
    }

    public void setDecommissionDate(Long decommissionDate) {
        this.decommissionDate = decommissionDate;
    }

    @JsonProperty(value = "isDecommision")
    public boolean isDecommision() {
        return isDecommision;
    }

    public void setDecommision(boolean decommision) {
        isDecommision = decommision;
    }
}
