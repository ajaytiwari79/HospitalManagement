package com.kairos.dto;

import com.kairos.utils.custome_annotation.NotNullOrEmpty;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.List;
import java.util.Set;

public class MasterAssetDto {

    @NotNullOrEmpty(message = "error.message.name.cannot.be.null.or.empty")
    private String name;

    @NotNullOrEmpty(message = "error.message.name.cannot.be.null.or.empty")
    private String description;

    @NotNull(message = "error.message.list.cannot.be.null")
    @NotEmpty(message = "error.message.list.cannot.be.empty")
    private List<OrganizationTypeAndServiceBasicDto> organizationTypes;

    @NotNull(message = "error.message.list.cannot.be.null")
    @NotEmpty(message = "error.message.list.cannot.be.empty")
    private List<OrganizationTypeAndServiceBasicDto> organizationSubTypes;

    @NotNull(message = "error.message.list.cannot.be.null")
    @NotEmpty(message = "error.message.list.cannot.be.empty")
    private List<OrganizationTypeAndServiceBasicDto> organizationServices;

    @NotNull(message = "error.message.list.cannot.be.null")
    @NotEmpty(message = "error.message.list.cannot.be.empty")
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
