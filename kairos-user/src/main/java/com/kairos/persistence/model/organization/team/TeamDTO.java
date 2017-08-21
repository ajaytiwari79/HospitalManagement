package com.kairos.persistence.model.organization.team;

import javax.validation.constraints.NotNull;

import org.hibernate.validator.constraints.NotEmpty;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.kairos.persistence.model.organization.AddressDTO;

/**
 * Created by prabjot on 20/1/17.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class TeamDTO {

    @NotEmpty(message = "error.name.notnull") @NotNull(message = "error.name.notnull")
    private String name;

    @NotEmpty(message = "error.description.notnull") @NotNull(message = "error.description.notnull")
    private String description;

    @NotEmpty(message = "error.visitour.teamid.notnull") @NotNull(message = "error.visitour.teamid.notnull")
    private String visitourId;

    private AddressDTO contactAddress;

    private boolean hasAddressOfUnit;

    public String getName() {
        return name;
    }

    public boolean isHasAddressOfUnit() {
        return hasAddressOfUnit;
    }

    public void setHasAddressOfUnit(boolean hasAddressOfUnit) {
        this.hasAddressOfUnit = hasAddressOfUnit;
    }

    public void setName(String name) {
        this.name = name;
    }

    public AddressDTO getContactAddress() {
        return contactAddress;
    }

    public void setContactAddress(AddressDTO contactAddress) {
        this.contactAddress = contactAddress;
    }

    public String getVisitourId() {
        return visitourId;
    }

    public void setVisitourId(String visitourId) {
        this.visitourId = visitourId;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
