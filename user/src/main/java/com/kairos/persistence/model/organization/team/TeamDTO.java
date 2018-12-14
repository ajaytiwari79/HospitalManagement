package com.kairos.persistence.model.organization.team;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.kairos.dto.user.organization.AddressDTO;


import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

/**
 * Created by prabjot on 20/1/17.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class TeamDTO {

    @NotEmpty(message = "error.name.notnull") @NotNull(message = "error.name.notnull")
    private String name;

    private String description;

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
