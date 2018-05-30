package com.kairos.dto;

import com.fasterxml.jackson.annotation.JsonInclude;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;


@JsonInclude(JsonInclude.Include.NON_NULL)
public class ClauseDto {


    @NotEmpty(message = "error.clause.title.cannot.be.empty")
    private String title;

    @NotEmpty(message = "error.message.list.cannot.be.empty")
    private List<ClauseTagDto> tags = new ArrayList<>();

    @NotNull(message = "error.clause.title.cannot.be.null")
    private String description;

    @NotEmpty(message = "error.message.list.cannot.be.empty")
    private List<OrganizationTypeAndServiceBasicDto>  organizationTypes;

    @NotEmpty(message = "error.message.list.cannot.be.empty")
    private List<OrganizationTypeAndServiceBasicDto>  organizationSubTypes;

    @NotEmpty(message = "error.message.list.cannot.be.empty")
    private List<OrganizationTypeAndServiceBasicDto>  organizationServices;

    @NotEmpty(message = "error.message.list.cannot.be.empty")
    private List<OrganizationTypeAndServiceBasicDto>  organizationSubServices;

    @NotEmpty(message = "error.message.list.cannot.be.empty")
    private Set<BigInteger> accountType;


    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public List<ClauseTagDto> getTags() {
        return tags;
    }

    public void setTags(List<ClauseTagDto> tags) {
        this.tags = tags;
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

    public Set<BigInteger> getAccountType() {
        return accountType;
    }

    public void setAccountType(Set<BigInteger> accountType) {
        this.accountType = accountType;
    }
}
