package com.kairos.persistance.model.asset;


import com.kairos.persistance.country.Country;
import com.kairos.persistance.model.common.MongoBaseEntity;
import org.springframework.data.mongodb.core.mapping.Document;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

@Document(collection = "asset")
public class Asset extends MongoBaseEntity {


    @NotNull(message = "error.asset.name.can't.be.null")
    @NotEmpty(message = "error.asset.name.can't.be.empty")
    String name;

    @NotNull(message = "error.asset.name.can't.be.null")
    @NotEmpty(message = "error.asset.name.can't.be.empty")
    String description;
    String managingDepartment;
    Country hostingLocation;

    int dataRetentionPeriod;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getManagingDepartment() {
        return managingDepartment;
    }

    public void setManagingDepartment(String managingDepartment) {
        this.managingDepartment = managingDepartment;
    }

    public Country getHostingLocation() {
        return hostingLocation;
    }

    public void setHostingLocation(Country hostingLocation) {
        this.hostingLocation = hostingLocation;
    }

    public int getDataRetentionPeriod() {
        return dataRetentionPeriod;
    }

    public void setDataRetentionPeriod(int dataRetentionPeriod) {
        this.dataRetentionPeriod = dataRetentionPeriod;
    }
}
