package com.kairos.persistence.model.organization.team;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.kairos.dto.user.organization.AddressDTO;

import javax.validation.constraints.NotBlank;

/**
 * Created by prabjot on 20/1/17.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class TeamDTO {

    private Long id;
    @NotBlank(message = "error.name.notnull")
    private String name;
    private AddressDTO contactAddress;
    private boolean hasAddressOfUnit;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

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

}
