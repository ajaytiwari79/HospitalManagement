package com.kairos.dto.master_data;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.kairos.dto.OrganizationTypeAndServiceBasicDto;
import com.kairos.utils.custome_annotation.NotNullOrEmpty;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import java.util.List;
import java.util.Set;

@JsonIgnoreProperties(ignoreUnknown = true)
public class MasterAssetDto {

    @NotNullOrEmpty(message = "error.description.cannot.be.null.or.empty")
    @Pattern(regexp = "^[a-zA-Z\\s]+$",message = "title cannot contain number or special character")
    private String name;

    @NotNullOrEmpty(message = "error.description.cannot.be.null.or.empty")
    private String description;

    @NotNull
    @NotEmpty(message = "error.list.cannot.be.empty")
    private List<OrganizationTypeAndServiceBasicDto> organizationTypes;

    @NotNull
    @NotEmpty(message = "error.list.cannot.be.empty")
    private List<OrganizationTypeAndServiceBasicDto> organizationSubTypes;

    @NotNull
    @NotEmpty(message = "error.list.cannot.be.empty")
    private List<OrganizationTypeAndServiceBasicDto> organizationServices;

    @NotNull
    @NotEmpty(message = "error.list.cannot.be.empty")
    private List<OrganizationTypeAndServiceBasicDto> organizationSubServices;


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

    public List<OrganizationTypeAndServiceBasicDto> getOrganizationTypes() {
        return organizationTypes;
    }

    public void setOrganizationTypes(List<OrganizationTypeAndServiceBasicDto> organizationTypes) {
        this.organizationTypes = organizationTypes;
    }

    public List<OrganizationTypeAndServiceBasicDto> getOrganizationSubTypes() {
        return organizationSubTypes;
    }

    public void setOrganizationSubTypes(List<OrganizationTypeAndServiceBasicDto> organizationSubTypes) {
        this.organizationSubTypes = organizationSubTypes;
    }

    public List<OrganizationTypeAndServiceBasicDto> getOrganizationServices() {
        return organizationServices;
    }

    public void setOrganizationServices(List<OrganizationTypeAndServiceBasicDto> organizationServices) {
        this.organizationServices = organizationServices;
    }

    public List<OrganizationTypeAndServiceBasicDto> getOrganizationSubServices() {
        return organizationSubServices;
    }

    public void setOrganizationSubServices(List<OrganizationTypeAndServiceBasicDto> organizationSubServices) {
        this.organizationSubServices = organizationSubServices;
    }
}
