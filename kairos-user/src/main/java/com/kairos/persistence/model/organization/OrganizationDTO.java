package com.kairos.persistence.model.organization;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import org.hibernate.validator.constraints.NotEmpty;

import javax.validation.constraints.NotNull;
import java.util.List;

/**
 * Created by prabjot on 20/1/17.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class OrganizationDTO {

    @NotEmpty(message = "error.name.notnull") @NotNull(message = "error.name.notnull")
    private String name;
    private String description;
    private boolean isPreKairos;
    private List<Long> organizationTypeId;
    private List<Long> organizationSubTypeId;
    private List<Long> businessTypeId;
    private AddressDTO contactAddress;


    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getName() {
        return name;
    }

    public AddressDTO getContactAddress() {
        return contactAddress;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setContactAddress(AddressDTO contactAddress) {
        this.contactAddress = contactAddress;
    }

    public boolean isPreKairos() {
        return isPreKairos;
    }

    public void setPreKairos(boolean preKairos) {
        isPreKairos = preKairos;
    }

    public List<Long> getOrganizationTypeId() {
        return organizationTypeId;
    }

    public List<Long> getOrganizationSubTypeId() {
        return organizationSubTypeId;
    }

    public List<Long> getBusinessTypeId() {
        return businessTypeId;
    }

    public void setOrganizationTypeId(List<Long> organizationTypeId) {
        this.organizationTypeId = organizationTypeId;
    }

    public void setOrganizationSubTypeId(List<Long> organizationSubTypeId) {
        this.organizationSubTypeId = organizationSubTypeId;
    }

    public void setBusinessTypeId(List<Long> businessTypeId) {
        this.businessTypeId = businessTypeId;
    }
}
